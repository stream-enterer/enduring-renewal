"""FightLog - central combat state manager."""

from dataclasses import dataclass, field, replace
from enum import Enum, auto
from typing import Optional, TYPE_CHECKING
from copy import deepcopy
from math import gcd
import random

from .entity import Entity, Team, FIELD_CAPACITY, BONES
from .effects import EffectType

if TYPE_CHECKING:
    from .hero import Hero


class Temporality(Enum):
    PRESENT = auto()  # Current confirmed state
    FUTURE = auto()   # State after pending effects resolve


@dataclass
class EntityState:
    """Snapshot of an entity's state at a point in time."""
    entity: Entity
    hp: int
    max_hp: int
    shield: int = 0      # Temporary damage block
    spiky: int = 0       # Damage dealt back to attackers
    self_heal: bool = False  # If True, damage dealt heals self (negates pain)
    damage_blocked: int = 0  # Total damage blocked by shield this turn
    keep_shields: bool = False  # If True, shields persist across turns
    stone_hp: int = 0    # Stone HP pips - caps incoming damage to 1 per hit
    fled: bool = False   # If True, entity has fled the battle
    dodge: bool = False  # If True, entity is invincible (immune to damage)
    regen: int = 0       # Regen amount - heals this much HP each turn
    petrified_sides: list = field(default_factory=list)  # Side indices that are petrified (int or None for overflow)
    used_die: bool = False  # If True, die has been fully used this turn
    times_used_this_turn: int = 0  # How many times die has been used this turn
    buffs: list = field(default_factory=list)  # Active buffs with Personal triggers
    cleansed_map: dict = field(default_factory=dict)  # CleanseType -> amount cleansed this turn
    deaths_this_fight: int = 0  # Number of times entity has died this fight (for reborn keyword)
    is_exerted: bool = False  # If True, all sides are blanks until end of next turn
    turns_elapsed: int = 0  # Number of turns this entity has been in combat
    used_last_turn: bool = False  # If True, this entity's die was used last turn

    @property
    def is_dead(self) -> bool:
        return self.hp <= 0

    def is_used(self) -> bool:
        """Check if this entity's die has been fully used this turn."""
        return self.used_die

    @property
    def is_out_of_battle(self) -> bool:
        """Entity is out of battle if dead or fled."""
        return self.hp <= 0 or self.fled

    def get_active_personals(self) -> list:
        """Get all active Personal triggers from buffs, items, traits.

        Returns triggers sorted by priority (lower priority runs first).
        Same priority preserves insertion order (FIFO).
        """
        from .triggers import Personal

        personals = []

        # Collect from buffs
        for buff in self.buffs:
            if buff.personal is not None:
                personals.append(buff.personal)

        # Sort by priority (stable sort preserves insertion order for same priority)
        personals.sort(key=lambda p: p.get_priority())

        return personals

    def add_buff(self, buff):
        """Add a buff to this entity state, merging if possible.

        Mergeable buffs (like Poison) combine into a single trigger
        instead of creating multiple entries.
        """
        # Check if we can cleanse this new debuff with existing cleanse budget
        cleanse_type = buff.get_cleanse_type()
        if cleanse_type is not None:
            fully_cleansed = self._attempt_cleanse_new_buff(buff)
            if fully_cleansed:
                return  # Debuff was fully cleansed, don't add

        # Try to merge with existing buffs
        for existing in self.buffs:
            if existing.can_merge(buff):
                existing.merge(buff)
                return  # Merged, don't add new buff

        # No merge possible, add as new buff
        self.buffs.append(buff)

    def _attempt_cleanse_new_buff(self, buff) -> bool:
        """Attempt to cleanse a new debuff before it's added.

        Returns True if fully cleansed (shouldn't be added).
        """
        total_cleanse = self.get_total_cleanse_amt()
        if total_cleanse == 0:
            return False

        cleanse_type = buff.get_cleanse_type()
        if cleanse_type is None:
            return False

        already_cleansed = self.cleansed_map.get(cleanse_type, 0)
        remaining = total_cleanse - already_cleansed
        if remaining <= 0:
            return False

        used, fully_cleansed = buff.cleanse_by(remaining)
        self.cleansed_map[cleanse_type] = already_cleansed + used
        return fully_cleansed

    def get_total_cleanse_amt(self) -> int:
        """Get total cleanse budget from all active personals."""
        total = 0
        for personal in self.get_active_personals():
            total += personal.get_cleanse_amt()
        return total

    def get_side_state(self, index: int, fight_log: "FightLog" = None) -> "SideState":
        """Get the calculated state for a side, after applying all triggers.

        If the side is petrified, returns a blank petrified side.
        Otherwise creates a mutable copy and applies all active triggers.

        Args:
            index: The side index to get state for
            fight_log: Optional FightLog for meta-keyword processing (e.g., copycat)
        """
        from .dice import Side, petrified_blank, Keyword

        # Check if entity has a die
        die = getattr(self.entity, 'die', None)
        if die is None:
            raise ValueError(f"Entity {self.entity} has no die")

        # Check if this side is petrified
        if index in self.petrified_sides:
            blank = petrified_blank()
            return SideState(
                original_side=blank,
                index=index,
                calculated_effect=blank.copy(),
                is_petrified=True
            )

        # Get original side and create mutable copy
        original_side = die.get_side(index)
        calculated = original_side.copy()

        # Create side state
        side_state = SideState(
            original_side=original_side,
            index=index,
            calculated_effect=calculated,
            is_petrified=False
        )

        # Apply all triggers
        # STASIS: If the calculated effect has stasis, stop processing triggers
        personals = self.get_active_personals()
        for i, personal in enumerate(personals):
            if side_state.has_keyword(Keyword.STASIS):
                break  # Stasis blocks all further trigger processing
            personal.affect_side(side_state, self, i)

        # Process meta-keywords (like copycat) after triggers
        # STASIS also blocks meta-keyword processing
        if not side_state.has_keyword(Keyword.STASIS):
            self._process_meta_keywords(side_state, fight_log)

        return side_state

    def _process_meta_keywords(self, side_state: "SideState", fight_log: "FightLog" = None):
        """Process meta-keywords that depend on global state.

        Meta-keywords:
        - Turn-start processing (skip turn 0):
          - FUMBLE: 50% chance to become blank
          - FLUCTUATE: Change to random effect type, keep keywords and pips
          - SHIFTER: Add a random keyword
          - LUCKY: Randomize pips to [0, current_pips]
          - CRITICAL: 50% chance for +1 pip
        - Copy keywords:
          - COPYCAT: Copy keywords from the most recently used die side
          - ECHO: Copy pips (value) from the most recently used die side
          - RESONATE: Copy the effect from the most recently used die side,
                      retaining this side's pips and the RESONATE keyword
        - Value modifiers:
          - PAIR: x2 value if previous die had same calculated value
        """
        from .dice import Keyword

        calculated = side_state.calculated_effect

        # Turn-start processing keywords - all skip turn 0
        if fight_log is not None and fight_log._turn != 0:
            # FUMBLE: 50% chance to become blank
            if Keyword.FUMBLE in calculated.keywords:
                seed = fight_log.get_shifter_seed(side_state.index, self.entity)
                rng = random.Random(seed)
                if rng.random() < 0.5:
                    # Replace with blank, keeping only FUMBLE keyword
                    calculated.effect_type = EffectType.BLANK
                    calculated.value = 0
                    calculated.growth_bonus = 0
                    calculated.keywords = {Keyword.FUMBLE}
                    return  # No further processing for blanked side

            # FLUCTUATE: Change to random effect type, keep keywords and pips
            if Keyword.FLUCTUATE in calculated.keywords:
                seed = fight_log.get_shifter_seed(side_state.index, self.entity)
                rng = random.Random(seed)
                # Effect types that have value (exclude BLANK and MANA which are special)
                value_types = [EffectType.DAMAGE, EffectType.HEAL, EffectType.SHIELD]
                # Pick a random type different from current
                choices = [t for t in value_types if t != calculated.effect_type]
                if choices:
                    new_type = rng.choice(choices)
                    calculated.effect_type = new_type
                # Value and keywords are preserved

            # SHIFTER: Add a random extra keyword, changes each turn
            if Keyword.SHIFTER in calculated.keywords:
                seed = fight_log.get_shifter_seed(side_state.index, self.entity)
                rng = random.Random(seed)
                # Get list of all keywords (excluding meta/ability-only ones)
                # Try up to 20 times to find a keyword not already present
                all_keywords = list(Keyword)
                for _ in range(20):
                    possible = rng.choice(all_keywords)
                    if possible not in calculated.keywords:
                        calculated.keywords.add(possible)
                        break

            # LUCKY: Pips randomized to [0, current_pips], changes each turn
            if Keyword.LUCKY in calculated.keywords:
                seed = fight_log.get_shifter_seed(side_state.index, self.entity)
                rng = random.Random(seed)
                current_value = calculated.calculated_value
                if current_value > 0:
                    # Roll determines reduction: int(-roll * (value + 1))
                    roll = rng.random()
                    bonus = int(-roll * (current_value + 1))
                    if bonus != 0:
                        calculated.value += bonus

            # CRITICAL: 50% chance for +1 pip, rechecks each turn
            if Keyword.CRITICAL in calculated.keywords:
                seed = fight_log.get_shifter_seed(side_state.index, self.entity)
                rng = random.Random(seed)
                # In Java: nextBoolean() false = +1 bonus
                if not rng.random() < 0.5:  # 50% chance
                    calculated.value += 1

        # COPYCAT: Copy keywords from most recently used die
        if Keyword.COPYCAT in calculated.keywords and fight_log is not None:
            recent = fight_log.get_most_recent_die_effect()
            if recent is not None:
                # Copy all keywords from the recent die effect
                for kw in recent.calculated_effect.keywords:
                    calculated.keywords.add(kw)

        # ECHO: Copy pips (value) from most recently used die
        if Keyword.ECHO in calculated.keywords and fight_log is not None:
            recent = fight_log.get_most_recent_die_effect()
            if recent is not None:
                recent_calc = recent.calculated_effect
                # Copy the value from the recent die (use 0 if it has no value)
                calculated.value = recent_calc.calculated_value if recent_calc.calculated_value else 0
                # Reset growth_bonus since we're copying the calculated value
                calculated.growth_bonus = 0

        # RESONATE: Copy effect from most recently used die, keeping pips and resonate
        if Keyword.RESONATE in calculated.keywords and fight_log is not None:
            recent = fight_log.get_most_recent_die_effect()
            if recent is not None:
                recent_calc = recent.calculated_effect
                # Save current value and resonate keyword
                current_value = calculated.value
                current_growth = calculated.growth_bonus
                # Copy effect type and keywords from recent
                calculated.effect_type = recent_calc.effect_type
                calculated.keywords = set(recent_calc.keywords)
                # Restore our value/pips and add resonate back
                calculated.value = current_value
                calculated.growth_bonus = current_growth
                calculated.keywords.add(Keyword.RESONATE)

        # SPY: Copy all keywords from first enemy attack this turn
        if Keyword.SPY in calculated.keywords and fight_log is not None:
            first_attack = fight_log.get_first_enemy_attack()
            if first_attack is not None:
                # Copy all keywords from the first enemy attack
                for kw in first_attack.calculated_effect.keywords:
                    calculated.keywords.add(kw)

        # DEJAVU: Copy keywords from sides I used last turn
        if Keyword.DEJAVU in calculated.keywords and fight_log is not None:
            sides_last_turn = fight_log.get_sides_used_last_turn(self.entity)
            for side in sides_last_turn:
                # Copy keywords from each side used last turn
                for kw in side.calculated_effect.keywords:
                    calculated.keywords.add(kw)

        # PAIR: x2 if previous die had same calculated value
        # Checks current value (after triggers, before pair bonus) vs previous die's visible value
        # Visibility keywords (fault, doubled, etc.) modify how others see the previous die's value
        if Keyword.PAIR in calculated.keywords and fight_log is not None:
            recent = fight_log.get_most_recent_die_effect()
            if recent is not None:
                # Compare current calculated value with previous die's visible value
                current_value = calculated.calculated_value
                prev_value = recent.calculated_effect.get_visible_value()
                if current_value == prev_value:
                    # Double the base value (not growth_bonus)
                    calculated.value *= 2

        # TRIO: x3 if previous 2 dice had same calculated value (using visible values)
        if Keyword.TRIO in calculated.keywords and fight_log is not None:
            current_value = calculated.calculated_value
            previous_effects = fight_log.get_last_n_die_effects(2)
            if len(previous_effects) >= 2:
                if all(eff.calculated_effect.get_visible_value() == current_value for eff in previous_effects):
                    calculated.value *= 3

        # TRILL: trio + skill combined - x3 if previous 2 dice match (skill bonus in conditional)
        if Keyword.TRILL in calculated.keywords and fight_log is not None:
            current_value = calculated.calculated_value
            previous_effects = fight_log.get_last_n_die_effects(2)
            if len(previous_effects) >= 2:
                if all(eff.calculated_effect.get_visible_value() == current_value for eff in previous_effects):
                    calculated.value *= 3

        # QUIN: x5 if previous 4 dice had same calculated value (using visible values)
        if Keyword.QUIN in calculated.keywords and fight_log is not None:
            current_value = calculated.calculated_value
            previous_effects = fight_log.get_last_n_die_effects(4)
            if len(previous_effects) >= 4:
                if all(eff.calculated_effect.get_visible_value() == current_value for eff in previous_effects):
                    calculated.value *= 5

        # SEPT: x7 if previous 6 dice had same calculated value (using visible values)
        if Keyword.SEPT in calculated.keywords and fight_log is not None:
            current_value = calculated.calculated_value
            previous_effects = fight_log.get_last_n_die_effects(6)
            if len(previous_effects) >= 6:
                if all(eff.calculated_effect.get_visible_value() == current_value for eff in previous_effects):
                    calculated.value *= 7

        # REV_DIFF and DOUB_DIFF: Modify value based on delta from base
        # delta = calculated - base
        # doubDiff: adds delta (doubling the effect of any pip changes)
        # revDiff: adds -2*delta (inverting and doubling the effect of pip changes)
        if Keyword.REV_DIFF in calculated.keywords or Keyword.DOUB_DIFF in calculated.keywords:
            base_value = side_state.original_side.value
            calculated_value = calculated.calculated_value
            delta = calculated_value - base_value
            if delta != 0:
                if Keyword.DOUB_DIFF in calculated.keywords:
                    calculated.value += delta
                else:  # REV_DIFF
                    calculated.value += delta * -2

    def get_total_petrification(self) -> int:
        """Count the number of petrified sides.

        Only counts actual petrified sides (not None overflow markers).
        """
        return sum(1 for s in self.petrified_sides if s is not None)

    def get_poison_damage_taken(self) -> int:
        """Get total poison damage this entity will take at end of turn.

        Poison is tracked via Poison personal triggers in buffs.
        """
        total = 0
        for personal in self.get_active_personals():
            total += personal.get_poison_damage()
        return total


@dataclass
class SideState:
    """Calculated state of a die side after applying all buffs and triggers.

    The calculated_effect is a mutable copy of the original side that gets
    modified by triggers during side state calculation.
    """
    original_side: "Side"      # The unmodified original side
    index: int                 # Original side index
    calculated_effect: "Side"  # Mutable copy modified by triggers
    is_petrified: bool = False  # True if this side was petrified

    @property
    def effect_type(self) -> EffectType:
        """Get the calculated effect type of this side."""
        return self.calculated_effect.effect_type

    @property
    def value(self) -> int:
        """Get the calculated value of this side."""
        return self.calculated_effect.calculated_value

    def has_keyword(self, keyword) -> bool:
        """Check if this side has the given keyword."""
        return keyword in self.calculated_effect.keywords

    def add_keyword(self, keyword):
        """Add a keyword to this side's calculated effect."""
        self.calculated_effect.keywords.add(keyword)

    def remove_keyword(self, keyword):
        """Remove a keyword from this side's calculated effect."""
        self.calculated_effect.keywords.discard(keyword)

    def add_value(self, amount: int):
        """Add to the calculated value."""
        self.calculated_effect.value += amount

    def set_value(self, value: int):
        """Set the calculated value to an absolute value.

        Used by hypnotise to set DAMAGE sides to 0.
        This sets the base value directly, ignoring growth_bonus.
        """
        self.calculated_effect.value = value
        self.calculated_effect.growth_bonus = 0  # Clear growth bonus too

    def replace_with(self, new_side: "Side"):
        """Replace the calculated effect with a new side entirely.

        Handles special keywords:
        - DOGMA: Only pips change, keeps keywords and effect type
        - ENDURING: After replacement, restores original keywords
        - RESILIENT: After replacement, restores original pips and keeps resilient
        """
        from .dice import Keyword

        # Save original state
        original_keywords = set(self.calculated_effect.keywords)
        original_value = self.calculated_effect.calculated_value

        # DOGMA: Only change the pip value, keep everything else
        if Keyword.DOGMA in original_keywords:
            new_value = new_side.calculated_value if new_side.value != -999 else 0
            self.calculated_effect.value = new_value
            self.calculated_effect.growth_bonus = 0
        else:
            # Normal replacement
            self.calculated_effect = new_side.copy()

        # ENDURING: Restore original keywords after replacement
        if Keyword.ENDURING in original_keywords:
            self.calculated_effect.keywords.update(original_keywords)

        # RESILIENT: Restore original pip value and keep resilient keyword
        if Keyword.RESILIENT in original_keywords:
            self.calculated_effect.value = original_value
            self.calculated_effect.growth_bonus = 0
            self.calculated_effect.keywords.add(Keyword.RESILIENT)

    def get_calculated_effect(self) -> "Side":
        """Get the calculated effect (for Java-style API compatibility)."""
        return self.calculated_effect


@dataclass
class PendingDamage:
    """Damage that will be applied when the turn resolves."""
    target: Entity
    amount: int
    source: Entity  # If source dies, this damage is cancelled


@dataclass
class Action:
    """A recorded action for undo purposes."""
    # Snapshot of state before this action
    states_before: dict[Entity, EntityState]
    pending_before: list[PendingDamage]


class FightLog:
    """Central manager for combat state."""

    def __init__(self, heroes: list[Entity], monsters: list[Entity]):
        self.heroes = heroes
        self._all_monsters = monsters  # Full pool of monsters
        self.monsters: list[Entity] = []  # Currently present on field
        self._reinforcements: list[Entity] = []  # Waiting to spawn

        # Current HP state for all entities
        self._states: dict[Entity, EntityState] = {}
        for i, h in enumerate(heroes):
            h.position = i
            self._states[h] = EntityState(h, h.entity_type.hp, h.entity_type.hp)

        # Spawn as many monsters as fit on the field
        self._spawn_initial_monsters()

        # Pending damage (applies in future, cancelled if source dies)
        self._pending: list[PendingDamage] = []

        # Undo stack
        self._history: list[Action] = []

        # Hero registry for trigger system (Entity -> Hero mapping)
        self._hero_registry: dict[Entity, "Hero"] = {}

        # Mana pool (global resource for the fight)
        self._total_mana: int = 0

        # Most recently used die effect (for copycat keyword)
        self._most_recent_die_effect: Optional[SideState] = None

        # History of die effects (for trio/quin/sept keywords)
        self._die_effect_history: list[SideState] = []

        # Counter for dice used this turn (for first, sixth, fizz keywords)
        self._dice_used_this_turn: int = 0

        # Turn counter (starts at 0, incremented at end of each turn)
        # Turn 0 is the initial state - turn-start processing keywords skip turn 0
        self._turn: int = 0

        # Target tracking for duel/focus keywords
        # Maps entity -> set of entities that have targeted it this turn
        self._targeters_this_turn: dict[Entity, set[Entity]] = {}
        # Track target of most recent die command (for focus keyword)
        self._last_die_target: Optional[Entity] = None
        # Track source of most recent die command (for self-targeting focus check)
        self._last_die_source: Optional[Entity] = None
        # Track if last die was self-targeting (for focus keyword edge case)
        self._last_die_was_self_targeting: bool = False

        # Track first enemy attack this turn (for spy keyword)
        self._first_enemy_attack_this_turn: Optional[SideState] = None

        # Track sides used per entity per turn (for dejavu keyword)
        # Maps turn_number -> entity -> list of SideState
        self._sides_used_per_turn: dict[int, dict[Entity, list[SideState]]] = {}

    def get_shifter_seed(self, side_index: int, entity: Entity) -> int:
        """Generate a deterministic seed for turn-start processing keywords.

        The seed is based on turn number, side index, and entity position.
        This ensures effects like shifter/lucky/critical are deterministic
        per turn but change each turn.

        Args:
            side_index: The index of the side on the die (0-5)
            entity: The entity that owns the die

        Returns:
            An integer seed for random.Random(seed)
        """
        # Use entity position to distinguish between entities
        entity_id = entity.position if entity.position is not None else 0
        # Combine turn, side index, and entity id into a seed
        return self._turn * 1000 + side_index * 10 + entity_id

    def _update_state(self, entity: Entity, **kwargs) -> EntityState:
        """Update entity state with specific fields while preserving all others.

        This helper ensures fields like buffs and cleansed_map aren't accidentally
        reset when updating other fields like HP or shield.

        Args:
            entity: The entity to update
            **kwargs: Fields to update (e.g., hp=5, shield=10)

        Returns:
            The new EntityState that was stored.
        """
        old = self._states[entity]
        # Copy mutable fields if not explicitly provided to avoid sharing references
        if 'petrified_sides' not in kwargs:
            kwargs['petrified_sides'] = list(old.petrified_sides)
        if 'buffs' not in kwargs:
            kwargs['buffs'] = list(old.buffs)
        if 'cleansed_map' not in kwargs:
            kwargs['cleansed_map'] = dict(old.cleansed_map)
        new = replace(old, **kwargs)
        self._states[entity] = new
        return new

    def _get_field_usage(self) -> int:
        """Calculate current field usage from present (alive) monsters."""
        usage = 0
        for m in self.monsters:
            state = self._states.get(m)
            if state and not state.is_dead:
                usage += m.entity_type.size.value
        return usage

    def _spawn_initial_monsters(self):
        """Spawn monsters in order until field is full."""
        for m in self._all_monsters:
            size = m.entity_type.size.value
            if self._get_field_usage() + size <= FIELD_CAPACITY:
                m.position = len(self.monsters)
                self.monsters.append(m)
                self._states[m] = EntityState(m, m.entity_type.hp, m.entity_type.hp)
            else:
                self._reinforcements.append(m)

    def _try_spawn_reinforcements(self):
        """Try to spawn reinforcements if there's room on the field."""
        spawned = True
        while spawned and self._reinforcements:
            spawned = False
            for i, m in enumerate(self._reinforcements):
                size = m.entity_type.size.value
                if self._get_field_usage() + size <= FIELD_CAPACITY:
                    # Spawn this reinforcement
                    m.position = len(self.monsters)
                    self.monsters.append(m)
                    self._states[m] = EntityState(m, m.entity_type.hp, m.entity_type.hp)
                    self._reinforcements.pop(i)
                    spawned = True
                    break  # Restart loop to check remaining reinforcements

    def summon_entity(self, entity_type, count: int = 1):
        """Summon entities during combat.

        Creates new entities of the given type and adds them to the monster team.
        If the field is full, they go into reinforcements.

        Args:
            entity_type: The EntityType to summon (e.g., BONES)
            count: Number of entities to summon
        """
        for _ in range(count):
            # Create new entity
            new_entity = Entity(entity_type, Team.MONSTER)

            # Check if there's room on the field
            size = entity_type.size.value
            if self._get_field_usage() + size <= FIELD_CAPACITY:
                # Spawn directly on field
                new_entity.position = len(self.monsters)
                self.monsters.append(new_entity)
                self._states[new_entity] = EntityState(new_entity, entity_type.hp, entity_type.hp)
            else:
                # Add to reinforcements
                self._reinforcements.append(new_entity)

    def _check_flee_triggers(self):
        """Check if any monsters should flee after an ally died.

        Goblins (and similar) flee when they become the only remaining enemy.
        """
        # Count alive, non-fled monsters
        alive_monsters = []
        for m in self.monsters:
            state = self._states[m]
            if not state.is_out_of_battle:
                alive_monsters.append(m)

        # If only one monster remains and it has flees_on_ally_death, it flees
        if len(alive_monsters) == 1:
            remaining = alive_monsters[0]
            if remaining.entity_type.flees_on_ally_death:
                self._update_state(remaining, fled=True)

    def _snapshot_states(self) -> dict[Entity, EntityState]:
        """Deep copy current states."""
        result = {}
        for e, s in self._states.items():
            # Deep copy mutable fields to prevent mutations affecting history
            result[e] = replace(s,
                petrified_sides=list(s.petrified_sides),
                buffs=[b.copy() for b in s.buffs],
                cleansed_map=dict(s.cleansed_map)
            )
        return result

    def _record_action(self):
        """Record state before an action for undo."""
        self._history.append(Action(
            states_before=self._snapshot_states(),
            pending_before=list(self._pending)
        ))

    def register_hero(self, hero: "Hero"):
        """Register a Hero object for trigger system."""
        self._hero_registry[hero.entity] = hero

    def _get_incoming_bonus(self, target: Entity, effect_type) -> int:
        """Get incoming effect bonus for target from their items."""
        hero = self._hero_registry.get(target)
        if hero:
            return hero.get_incoming_bonus(effect_type)
        return 0

    def _check_and_fire_rescue(self, target: Entity, was_dying: bool):
        """Check if target was rescued and fire triggers if so.

        A rescue occurs when target transitions from dying (future HP <= 0)
        to surviving (future HP > 0).
        """
        if not was_dying:
            return  # Can't be rescued if wasn't dying

        future_state = self.get_state(target, Temporality.FUTURE)
        if future_state.is_dead:
            return  # Still dying, no rescue

        # Rescue occurred! Fire ON_RESCUE triggers
        hero = self._hero_registry.get(target)
        if hero:
            from .item import TriggerType
            for trigger in hero.get_triggers(TriggerType.ON_RESCUE):
                if trigger.target_self:
                    trigger.effect.apply(self, target)

    def get_state(self, entity: Entity, temporality: Temporality) -> EntityState:
        """Get entity state at given temporality."""
        base = self._states[entity]

        if temporality == Temporality.PRESENT:
            return base

        # FUTURE: apply pending damage (shield blocks pending damage)
        future_hp = base.hp
        shield_remaining = base.shield
        for pending in self._pending:
            if pending.target == entity:
                # Only apply if source is alive
                source_state = self._states[pending.source]
                if not source_state.is_dead:
                    # Shield blocks pending damage
                    blocked = min(shield_remaining, pending.amount)
                    actual_damage = pending.amount - blocked
                    shield_remaining -= blocked
                    future_hp -= actual_damage

        # FUTURE: project poison/regen damage for next turn
        # Poison damage is direct (bypasses shield) and calculated from active personals
        poison = base.get_poison_damage_taken()
        regen = base.regen
        for personal in base.get_active_personals():
            regen += personal.get_regen()

        health_delta = regen - poison
        if health_delta < 0:
            # Poison damage is direct (bypasses shield)
            future_hp += health_delta
        elif health_delta > 0:
            # Regen heals, capped at max HP
            future_hp = min(future_hp + health_delta, base.max_hp)

        return replace(base,
            hp=future_hp,
            petrified_sides=list(base.petrified_sides),
            buffs=list(base.buffs),
            cleansed_map=dict(base.cleansed_map)
        )

    def apply_damage(self, source: Entity, target: Entity, amount: int, is_pending: bool = False):
        """Apply damage to target. If is_pending, damage goes to future state.

        Immediate damage is reduced by shield. Blocked amount is tracked.
        Stone HP caps damage to 1 per hit.
        Dodge makes target immune to damage.
        """
        self._record_action()

        state = self._states[target]

        # Dodge makes target immune to damage
        if state.dodge:
            return  # No damage dealt

        # Stone HP caps damage to 1 per hit (0 damage stays 0)
        effective_amount = amount
        if state.stone_hp > 0 and amount > 0:
            effective_amount = 1

        if is_pending:
            self._pending.append(PendingDamage(target, effective_amount, source))
        else:
            # Shield blocks damage
            blocked = min(state.shield, effective_amount)
            actual_damage = effective_amount - blocked
            new_shield = state.shield - blocked
            new_hp = state.hp - actual_damage
            # Track if this damage kills the target (for reborn keyword)
            was_alive = state.hp > 0
            new_deaths = state.deaths_this_fight + (1 if was_alive and new_hp <= 0 else 0)
            self._update_state(target,
                hp=new_hp,
                shield=new_shield,
                damage_blocked=state.damage_blocked + blocked,
                deaths_this_fight=new_deaths
            )
            # Check if target died and triggers flee
            if new_hp <= 0 and target.team == Team.MONSTER:
                self._check_flee_triggers()
            # Check if something died and reinforcements can spawn
            self._try_spawn_reinforcements()

    def apply_cleave(self, source: Entity, target: Entity, amount: int, is_pending: bool = True):
        """Apply cleave damage to target and adjacent allies."""
        self._record_action()

        # Find adjacent entities in the same team
        team_list = self.heroes if target.team == Team.HERO else self.monsters
        targets = []

        for entity in team_list:
            if abs(entity.position - target.position) <= 1:
                targets.append(entity)

        for t in targets:
            if is_pending:
                self._pending.append(PendingDamage(t, amount, source))
            else:
                state = self._states[t]
                # Shield blocks damage
                blocked = min(state.shield, amount)
                actual_damage = amount - blocked
                new_shield = state.shield - blocked
                self._update_state(t,
                    hp=state.hp - actual_damage,
                    shield=new_shield,
                    damage_blocked=state.damage_blocked + blocked
                )

    def undo(self):
        """Undo the last action."""
        if not self._history:
            return
        action = self._history.pop()
        self._states = action.states_before
        self._pending = action.pending_before

    def count_dying_heroes(self) -> int:
        """Count heroes that will be dead in the future state."""
        count = 0
        for hero in self.heroes:
            future = self.get_state(hero, Temporality.FUTURE)
            if future.is_dead:
                count += 1
        return count

    def hash_death_state(self) -> str:
        """Create a hash of which heroes are alive/dead in future."""
        result = []
        for hero in self.heroes:
            future = self.get_state(hero, Temporality.FUTURE)
            result.append('d' if future.is_dead else 'a')
        return ''.join(result)

    def get_valid_enemy_targets(self) -> list[Entity]:
        """Get heroes that are valid targets (not dying in future state).

        Enemy AI uses this to avoid wasting attacks on already-doomed heroes.
        """
        valid = []
        for hero in self.heroes:
            future = self.get_state(hero, Temporality.FUTURE)
            if not future.is_dead:
                valid.append(hero)
        return valid

    def get_alive_heroes(self, temporality: Temporality) -> list[Entity]:
        """Get heroes that are alive at the given temporality."""
        alive = []
        for hero in self.heroes:
            state = self.get_state(hero, temporality)
            if not state.is_dead:
                alive.append(hero)
        return alive

    def get_alive_monsters(self, temporality: Temporality) -> list[Entity]:
        """Get monsters that are alive at the given temporality."""
        alive = []
        for monster in self.monsters:
            state = self.get_state(monster, temporality)
            if not state.is_dead:
                alive.append(monster)
        return alive

    def apply_pain_damage(self, source: Entity, target: Entity, damage: int, pain: int):
        """Apply damage to target with pain (self-damage) to source.

        Pain is immediate self-damage to the attacker. Can kill the attacker.
        The damage to target and pain to source happen in the same action.
        """
        self._record_action()

        # Pain: immediate self-damage to attacker (unless selfHeal negates it)
        source_state = self._states[source]
        if source_state.self_heal:
            # selfHeal negates pain - damage dealt heals back the pain
            new_hp = source_state.hp  # No change
        else:
            new_hp = source_state.hp - pain
        self._update_state(source, hp=new_hp)

        # Damage to target (also immediate for this test's behavior)
        target_state = self._states[target]
        self._update_state(target, hp=target_state.hp - damage)

    def modify_max_hp(self, target: Entity, amount: int):
        """Modify an entity's max HP by amount (can be positive or negative).

        Max HP changes are cumulative during combat.
        Max HP has a floor of 1 - it can never go to 0 or below.
        """
        self._record_action()

        state = self._states[target]
        new_max_hp = max(1, state.max_hp + amount)  # Floor at 1
        self._update_state(target, max_hp=new_max_hp)

    def apply_buff_spiky(self, target: Entity, amount: int):
        """Apply Spiky buff to target. Spiky deals damage back to attackers."""
        self._record_action()
        state = self._states[target]
        self._update_state(target, spiky=state.spiky + amount)

    def apply_shield(self, target: Entity, amount: int):
        """Apply shield to target. Shield blocks incoming damage.

        Also checks for rescue (dying -> surviving transition) and fires triggers.
        Applies incoming shield bonus from target's items.
        """
        from .effects import EffectType

        # Check if target was dying before shield
        was_dying = self.get_state(target, Temporality.FUTURE).is_dead

        # Add incoming shield bonus
        bonus = self._get_incoming_bonus(target, EffectType.SHIELD)
        total_shield = amount + bonus

        self._record_action()
        state = self._states[target]
        self._update_state(target, shield=state.shield + total_shield)

        # Check for rescue and fire triggers
        self._check_and_fire_rescue(target, was_dying)

    def apply_heal(self, target: Entity, amount: int):
        """Heal target. HP is capped at max HP.

        Applies incoming heal bonus from target's items.
        """
        from .effects import EffectType

        # Add incoming heal bonus
        bonus = self._get_incoming_bonus(target, EffectType.HEAL)
        total_heal = amount + bonus

        self._record_action()
        state = self._states[target]
        new_hp = min(state.hp + total_heal, state.max_hp)
        self._update_state(target, hp=new_hp)

    def apply_heal_shield(self, target: Entity, heal_amount: int, shield_amount: int):
        """Apply both heal and shield to target.

        Combined effect: heals first (capped at max HP), then adds shield.
        Also checks for rescue triggers.
        Applies incoming bonuses from target's items.
        """
        from .effects import EffectType

        # Check if target was dying before
        was_dying = self.get_state(target, Temporality.FUTURE).is_dead

        # Add incoming bonuses
        heal_bonus = self._get_incoming_bonus(target, EffectType.HEAL)
        shield_bonus = self._get_incoming_bonus(target, EffectType.SHIELD)
        total_heal = heal_amount + heal_bonus
        total_shield = shield_amount + shield_bonus

        self._record_action()
        state = self._states[target]
        new_hp = min(state.hp + total_heal, state.max_hp)
        self._update_state(target, hp=new_hp, shield=state.shield + total_shield)

        # Check for rescue and fire triggers
        self._check_and_fire_rescue(target, was_dying)

    def apply_buff_self_heal(self, target: Entity):
        """Apply selfHeal buff to target. SelfHeal negates pain damage."""
        self._record_action()
        self._update_state(target, self_heal=True)

    def apply_shield_repel(self, user: Entity, shield_amount: int):
        """Apply shield and repel pending damage back to attackers.

        Shield: blocks incoming damage
        Repel: reflects damage back to the source of pending damage

        If attacker has Spiky, the repel damage triggers Spiky,
        but the user's shield absorbs the Spiky return damage.
        """
        self._record_action()

        user_state = self._states[user]
        remaining_shield = shield_amount
        remaining_repel = shield_amount

        # Process pending damage targeting this user
        new_pending = []
        for pending in self._pending:
            if pending.target == user and remaining_repel > 0:
                # Repel: reflect damage back to source
                reflect_amount = min(remaining_repel, pending.amount)
                remaining_repel -= reflect_amount

                # Deal reflected damage to source
                source_state = self._states[pending.source]
                new_source_hp = source_state.hp - reflect_amount
                self._update_state(pending.source, hp=new_source_hp)

                # If source has Spiky, it triggers and damages user
                # But user's shield absorbs it
                if source_state.spiky > 0:
                    spiky_damage = source_state.spiky
                    absorbed = min(remaining_shield, spiky_damage)
                    remaining_shield -= absorbed
                    actual_spiky_damage = spiky_damage - absorbed
                    if actual_spiky_damage > 0:
                        user_state = self._states[user]
                        self._update_state(user, hp=user_state.hp - actual_spiky_damage)

                # Reduce pending damage by repel amount
                remaining_damage = pending.amount - reflect_amount
                if remaining_damage > 0:
                    new_pending.append(PendingDamage(pending.target, remaining_damage, pending.source))
            else:
                new_pending.append(pending)

        self._pending = new_pending

        # Apply remaining shield to user's state
        user_state = self._states[user]
        self._update_state(user, shield=user_state.shield + remaining_shield)

    def get_present_monsters(self, temporality: Temporality) -> list[Entity]:
        """Get monsters currently on the field (not dead, not fled, not reinforcement).

        This differs from get_alive_monsters in that it only counts monsters
        that have been spawned onto the field, not those waiting as reinforcements.
        """
        present = []
        for monster in self.monsters:
            state = self.get_state(monster, temporality)
            if not state.is_out_of_battle:
                present.append(monster)
        return present

    def is_victory(self, temporality: Temporality) -> bool:
        """Check if all monsters are dead (no alive monsters, no reinforcements)."""
        if self._reinforcements:
            return False
        return len(self.get_present_monsters(temporality)) == 0

    def apply_group_damage(self, source: Entity | None, amount: int, is_pending: bool = False):
        """Apply damage to all present monsters.

        Used for area-of-effect attacks that hit all enemies.
        If source is None, damage cannot be cancelled (e.g., environmental).
        """
        self._record_action()

        targets = self.get_present_monsters(Temporality.PRESENT)
        for target in targets:
            if is_pending and source is not None:
                self._pending.append(PendingDamage(target, amount, source))
            else:
                state = self._states[target]
                blocked = min(state.shield, amount)
                actual_damage = amount - blocked
                new_shield = state.shield - blocked
                self._update_state(target,
                    hp=state.hp - actual_damage,
                    shield=new_shield,
                    damage_blocked=state.damage_blocked + blocked
                )

        # After group damage, check for reinforcements
        if not is_pending:
            self._try_spawn_reinforcements()

    def apply_redirect(self, user: Entity, target: Entity, self_shield: int = 0):
        """Apply redirect (taunt) - move incoming damage from target to user.

        Redirect:
        1. Moves ALL pending damage from target to user (redirector)
        2. If self_shield > 0, gives user that many shields (selfShield keyword)

        Some redirects have selfShield keyword (e.g., die sides), others don't
        (e.g., Red Flag item). Pass self_shield=0 for redirects without selfShield.
        Multiple redirects stack shields. Redirecting to self just adds shields.
        """
        self._record_action()

        # Move all pending damage from target to user
        for pending in self._pending:
            if pending.target == target:
                pending.target = user

        # Give user shields if selfShield keyword present
        if self_shield > 0:
            state = self._states[user]
            self._update_state(user, shield=state.shield + self_shield)

    def apply_keep_shields(self, target: Entity):
        """Apply KeepShields buff - shields persist across turns."""
        self._record_action()
        self._update_state(target, keep_shields=True)

    def apply_stone_hp(self, target: Entity, amount: int):
        """Apply Stone HP buff - caps all incoming damage to 1 per hit.

        Stone HP represents hardened HP pips that can only take 1 damage at a time.
        Amount is the number of stone HP pips (affects how much total damage can be absorbed).
        """
        self._record_action()
        self._update_state(target, stone_hp=amount)

    def next_turn(self):
        """Advance to next turn - clears shields (unless keep_shields), pending damage, etc.

        Turn transition:
        - Poison damage is applied (direct, bypasses shield)
        - Regen healing is applied (capped at max HP)
        - Shields are cleared unless entity has keep_shields buff
        - Pending damage is cleared (already resolved)
        - cleansed_map is reset for new turn
        - Other turn-based buffs may be cleared (spiky, etc.)
        """
        self._record_action()

        # Increment turn counter
        self._turn += 1

        # Clear pending damage
        self._pending = []

        # Reset per-turn dice tracking
        self._dice_used_this_turn = 0

        # Clear target tracking for duel/focus keywords
        self._targeters_this_turn.clear()
        self._last_die_target = None
        self._last_die_source = None
        self._last_die_was_self_targeting = False

        # Clear first enemy attack tracking for spy keyword
        self._first_enemy_attack_this_turn = None

        # Process each entity's turn transition
        for entity, state in list(self._states.items()):
            new_shield = state.shield if state.keep_shields else 0

            # Calculate poison damage from active personals
            poison = state.get_poison_damage_taken()

            # Calculate regen from state and active personals
            regen = state.regen
            for personal in state.get_active_personals():
                regen += personal.get_regen()

            # Calculate health delta (regen - poison)
            health_delta = regen - poison
            new_hp = state.hp
            if health_delta > 0:
                # Regen heals (capped at max HP)
                new_hp = min(state.hp + health_delta, state.max_hp)
            elif health_delta < 0:
                # Poison deals direct damage (bypasses shield)
                new_hp = state.hp + health_delta

            # Track if poison kills the entity (for reborn keyword)
            was_alive = state.hp > 0
            new_deaths = state.deaths_this_fight + (1 if was_alive and new_hp <= 0 else 0)

            # Track whether die was used this turn (for patient keyword)
            was_used_this_turn = state.used_die

            # Tick buffs and remove expired ones
            new_buffs = []
            for buff in state.buffs:
                buff.tick()
                if not buff.is_expired():
                    new_buffs.append(buff)

            # Update state with remaining buffs and reset turn-specific state
            self._update_state(entity,
                buffs=new_buffs,
                hp=new_hp,
                shield=new_shield,
                damage_blocked=0,  # Reset damage_blocked
                used_die=False,  # Reset used_die
                times_used_this_turn=0,  # Reset times used
                cleansed_map={},  # Reset cleansed_map for new turn
                deaths_this_fight=new_deaths,  # Preserve/update death count
                turns_elapsed=state.turns_elapsed + 1,  # Increment turn counter
                used_last_turn=was_used_this_turn  # Track for patient keyword
            )

    def apply_kill(self, target: Entity):
        """Instantly kill an entity (set HP to 0).

        Increments death counter for reborn keyword.
        """
        self._record_action()
        state = self._states[target]
        self._update_state(target,
            hp=0,
            deaths_this_fight=state.deaths_this_fight + 1  # Record the death
        )

    def record_death(self, target: Entity):
        """Record that an entity has died (for reborn keyword).

        Call this after HP goes to 0 or below.
        """
        state = self._states[target]
        if state.hp <= 0:
            self._update_state(target, deaths_this_fight=state.deaths_this_fight + 1)

    def apply_resurrect(self, amount: int):
        """Resurrect up to N dead heroes with full HP.

        Resurrects dead heroes in order (by position).
        Capped at number of dead heroes.
        Resurrected heroes come back with full HP.
        Death count is preserved (for reborn keyword).
        """
        self._record_action()

        # Find dead heroes
        dead_heroes = []
        for hero in self.heroes:
            state = self._states[hero]
            if state.is_dead:
                dead_heroes.append(hero)

        # Resurrect up to 'amount' dead heroes
        to_resurrect = dead_heroes[:amount]
        for hero in to_resurrect:
            state = self._states[hero]
            # Reset to clean state but preserve death count
            self._update_state(hero,
                hp=state.max_hp,  # Full HP
                shield=0,
                spiky=0,
                self_heal=False,
                damage_blocked=0,
                keep_shields=False,
                stone_hp=0
                # deaths_this_fight is preserved by _update_state
            )

    def get_dead_heroes(self) -> list[Entity]:
        """Get list of dead heroes."""
        dead = []
        for hero in self.heroes:
            state = self._states[hero]
            if state.is_dead:
                dead.append(hero)
        return dead

    def apply_heal_vitality(self, target: Entity, amount: int):
        """Apply heal with Vitality keyword - heals AND increases max HP.

        Vitality:
        - Heals by N
        - Increases max HP by N
        - HP capped at new max (can exceed old max, up to new max)

        Example: 2/4 + healVitality(3) = 5/7
        """
        self._record_action()
        state = self._states[target]

        # Increase max HP first
        new_max_hp = state.max_hp + amount

        # Heal by amount, capped at new max
        new_hp = min(state.hp + amount, new_max_hp)

        self._update_state(target, hp=new_hp, max_hp=new_max_hp)

    def count_damaged_enemies(self) -> int:
        """Count enemies (monsters) that are damaged but not dead.

        An enemy is "damaged" if HP < maxHP but HP > 0.
        Dead enemies don't count as damaged.
        Used by Bloodlust keyword to calculate bonus damage.
        """
        count = 0
        for monster in self.monsters:
            state = self._states[monster]
            if not state.is_dead and state.hp < state.max_hp:
                count += 1
        return count

    def count_dead_allies(self, entity: Entity) -> int:
        """Count dead allies of the given entity.

        For heroes, counts dead heroes.
        For monsters, counts dead monsters.
        Used by Vigil keyword to calculate bonus.
        """
        count = 0
        team = self.heroes if entity.team == Team.HERO else self.monsters
        for ally in team:
            if ally != entity:  # Don't count self
                state = self._states.get(ally)
                if state and state.is_dead:
                    count += 1
        return count

    def _get_allies(self, entity: Entity) -> list[Entity]:
        """Get all allies of the given entity (excluding self).

        For heroes, returns other heroes.
        For monsters, returns other monsters.
        """
        team = self.heroes if entity.team == Team.HERO else self.monsters
        return [ally for ally in team if ally != entity]

    def apply_bloodlust_damage(self, source: Entity, target: Entity, base_damage: int, is_pending: bool = False):
        """Apply damage with Bloodlust keyword.

        Bloodlust gains +N bonus damage where N = number of currently damaged enemies.
        The count is evaluated at time of attack.
        Damaging a new enemy increases future bonus; killing an enemy decreases it.
        """
        # Calculate bonus BEFORE this attack (count of already-damaged enemies)
        bonus = self.count_damaged_enemies()
        total_damage = base_damage + bonus

        # Apply the damage (uses standard damage logic)
        self.apply_damage(source, target, total_damage, is_pending)

    def apply_poison_damage(self, source: Entity, target: Entity, amount: int):
        """Apply damage with Poison keyword.

        Poison deals immediate damage AND adds pending damage equal to the amount.
        Example: poison(1) deals 1 damage now and 1 more at turn end.

        The pending poison damage uses the source as its source, so it can be
        cancelled if source dies before turn end.
        """
        # Deal immediate damage
        self.apply_damage(source, target, amount, is_pending=False)

        # Add pending poison damage
        self._pending.append(PendingDamage(target, amount, source))

    def apply_engage_damage(self, source: Entity, target: Entity, base_damage: int, is_pending: bool = False):
        """Apply damage with Engage keyword.

        Engage deals x2 damage against targets at full HP.
        Once they're damaged (HP < maxHP), no multiplier applies.

        Note: Engage also works on heals/shields (x2 vs full HP ally),
        but this method is specifically for damage.
        """
        state = self._states[target]
        multiplier = 2 if state.hp == state.max_hp else 1
        total_damage = base_damage * multiplier

        self.apply_damage(source, target, total_damage, is_pending)

    def apply_cruel_damage(self, source: Entity, target: Entity, base_damage: int, is_pending: bool = False):
        """Apply damage with Cruel keyword.

        Cruel deals x2 damage against targets at half HP or less (HP <= maxHP/2).

        Note: Cruel also works on heals/shields, but this method is for damage.
        Not to be confused with Fierce (target flees if HP <= N).
        """
        state = self._states[target]
        half_hp = state.max_hp // 2
        multiplier = 2 if state.hp <= half_hp else 1
        total_damage = base_damage * multiplier

        self.apply_damage(source, target, total_damage, is_pending)

    def apply_cruel_heal(self, target: Entity, base_heal: int):
        """Apply heal with Cruel keyword.

        Cruel heals x2 when target is at half HP or less (HP <= maxHP/2).
        Same condition as cruel damage - applies to any effect type.

        Note: The cruel check uses target's current HP, not future HP.
        """
        self._record_action()
        state = self._states[target]

        # Calculate cruel multiplier based on target's current HP
        half_hp = state.max_hp / 2.0  # Use float division like Java
        multiplier = 2 if state.hp <= half_hp else 1
        total_heal = base_heal * multiplier

        # Heal capped at max HP
        new_hp = min(state.hp + total_heal, state.max_hp)
        self._update_state(target, hp=new_hp)

    def apply_weaken_damage(self, source: Entity, target: Entity, amount: int):
        """Apply damage with Weaken keyword.

        Weaken deals damage to target AND reduces target's outgoing pending damage by N.
        This affects pending damage that the target has already dealt.

        Example: If monster dealt 3 pending to hero, weaken(2) reduces that to 1 pending.
        """
        self._record_action()

        # 1. Deal damage to target
        state = self._states[target]

        # Stone HP caps damage to 1 per hit (0 damage stays 0)
        effective_amount = amount
        if state.stone_hp > 0 and amount > 0:
            effective_amount = 1

        # Shield blocks damage
        blocked = min(state.shield, effective_amount)
        actual_damage = effective_amount - blocked
        new_shield = state.shield - blocked
        new_hp = state.hp - actual_damage
        self._update_state(target,
            hp=new_hp,
            shield=new_shield,
            damage_blocked=state.damage_blocked + blocked
        )

        # Check if target died and triggers flee
        if new_hp <= 0 and target.team == Team.MONSTER:
            self._check_flee_triggers()

        # Check if something died and reinforcements can spawn
        self._try_spawn_reinforcements()

        # 2. Reduce pending damage from target by 'amount'
        remaining_reduction = amount
        for pending in self._pending:
            if pending.source == target and remaining_reduction > 0:
                reduction = min(remaining_reduction, pending.amount)
                pending.amount -= reduction
                remaining_reduction -= reduction

    def apply_drain_damage(self, source: Entity, target: Entity, amount: int):
        """Apply damage with Drain/SelfHeal keyword.

        Drain deals damage to target AND heals the attacker by the same amount.
        Example: drain(1) deals 1 damage to target and heals user by 1.

        Note: This is different from the selfHeal buff (which negates pain).
        This is an attack keyword that provides lifesteal.
        """
        # Deal damage to target
        self.apply_damage(source, target, amount, is_pending=False)

        # Heal the attacker
        source_state = self._states[source]
        new_hp = min(source_state.hp + amount, source_state.max_hp)
        self._update_state(source, hp=new_hp)

    def apply_dodge(self, target: Entity):
        """Apply Dodge buff to target - makes them invincible (immune to damage).

        While dodge is active, the entity takes no damage from any source.
        """
        self._record_action()
        self._update_state(target, dodge=True)

    def apply_heal_regen(self, target: Entity, amount: int):
        """Apply heal with Regen keyword.

        Regen heals N immediately AND applies a persistent regen buff.
        The buff heals N HP at the start of each subsequent turn.
        Healing is capped at max HP.

        Example: healRegen(1) heals 1 now, then 1 more each turn.
        """
        self._record_action()
        state = self._states[target]

        # Immediate heal (capped at max HP)
        new_hp = min(state.hp + amount, state.max_hp)

        # Apply/add regen buff
        new_regen = state.regen + amount

        self._update_state(target, hp=new_hp, regen=new_regen)

    def get_total_mana(self) -> int:
        """Get the current total mana in the fight."""
        return self._total_mana

    def add_mana(self, amount: int):
        """Add mana to the pool."""
        self._record_action()
        self._total_mana += amount

    def use_die(self, entity: Entity, side_index: int, target: Entity):
        """Use a die side against a target.

        Executes the side's effect and applies growth if the side has the growth keyword.
        Gets the side state (applies all buffs/triggers including meta-keywords like copycat).

        For shieldMana effects (SHIELD type with MANA keyword):
        - Grants shield equal to calculated_value
        - Grants mana equal to calculated_value
        - If has ENGAGE keyword and target at full HP, doubles the effect

        For copycat:
        - If side has COPYCAT keyword, it copies keywords from most recently used die
        - This means dmgCopycat can grant mana if the recent die had MANA keyword

        For growth keyword:
        - Increases side's base value by 1 after use
        """
        from .dice import Keyword

        # Get the entity's die (must be set up before calling this)
        die = getattr(entity, 'die', None)
        if die is None:
            raise ValueError(f"Entity {entity} has no die")

        # Get the side state (applies all buffs/triggers + meta-keywords like copycat)
        source_state = self._states[entity]
        side_state = source_state.get_side_state(side_index, self)
        calculated_side = side_state.get_calculated_effect()

        # Base value from calculated side
        value = calculated_side.calculated_value

        # Apply conditional keyword bonuses based on source and target state
        target_state = self._states[target]
        value = self._apply_conditional_keyword_bonuses(value, calculated_side, source_state, target_state, entity, target)

        # Check for death trigger keywords before effect
        target_was_alive = not target_state.is_dead
        target_was_dying = self.get_state(target, Temporality.FUTURE).is_dead

        # Apply the effect based on type
        if calculated_side.effect_type == EffectType.SHIELD:
            self.apply_shield(target, value)

            # If has MANA keyword, also grant mana (same value, with same multiplier)
            if calculated_side.has_keyword(Keyword.MANA):
                self.add_mana(value)

            # REPEL: N damage to all enemies attacking the target
            if calculated_side.has_keyword(Keyword.REPEL):
                attackers = self._get_entities_attacking(target)
                for attacker in attackers:
                    attacker_state = self.get_state(attacker, Temporality.PRESENT)
                    if not attacker_state.is_dead:
                        self.apply_damage(entity, attacker, value, is_pending=False)

            # SELF_REPEL: N damage to all enemies attacking me (source)
            if calculated_side.has_keyword(Keyword.SELF_REPEL):
                attackers = self._get_entities_attacking(entity)
                for attacker in attackers:
                    attacker_state = self.get_state(attacker, Temporality.PRESENT)
                    if not attacker_state.is_dead:
                        self.apply_damage(entity, attacker, value, is_pending=False)

            # EVIL: If shielding saved a dying hero, I die
            if calculated_side.has_keyword(Keyword.EVIL):
                now_surviving = not self.get_state(target, Temporality.FUTURE).is_dead
                if target_was_dying and now_surviving:
                    self.apply_damage(entity, entity, source_state.hp + 1000)

        elif calculated_side.effect_type == EffectType.DAMAGE:
            # Build target list based on CLEAVE/DESCEND keywords
            damage_targets = [target]
            if calculated_side.has_keyword(Keyword.CLEAVE):
                # Cleave hits both above and below
                damage_targets.extend(self._get_adjacent_entities(target, above=1, below=1))
            elif calculated_side.has_keyword(Keyword.DESCEND):
                # Descend hits only below
                damage_targets.extend(self._get_adjacent_entities(target, above=0, below=1))

            # Apply damage to all targets (with vulnerable bonus)
            for dmg_target in damage_targets:
                # Vulnerable bonus: extra damage from dice/spells
                vuln_bonus = self.get_vulnerable_bonus(dmg_target)
                final_damage = value + vuln_bonus
                self.apply_damage(entity, dmg_target, final_damage)

            # If has MANA keyword (e.g., from copycat), also grant mana
            if calculated_side.has_keyword(Keyword.MANA):
                self.add_mana(value)

            # GUILT: If damage was lethal, I die
            if calculated_side.has_keyword(Keyword.GUILT):
                target_now_dead = self.get_state(target, Temporality.PRESENT).is_dead
                if target_was_alive and target_now_dead:
                    self.apply_damage(entity, entity, source_state.hp + 1000)

            # FIERCE: target flees if HP <= N after damage (N = pip value)
            if calculated_side.has_keyword(Keyword.FIERCE):
                target_current_state = self.get_state(target, Temporality.PRESENT)
                if not target_current_state.is_dead and target_current_state.hp <= value:
                    # Target flees (remove from combat)
                    self._update_state(target, hp=0)

        elif calculated_side.effect_type == EffectType.HEAL:
            self.apply_heal(target, value)

            # If has MANA keyword (e.g., from copycat), also grant mana
            if calculated_side.has_keyword(Keyword.MANA):
                self.add_mana(value)

            # EVIL: If healing saved a dying hero, I die
            if calculated_side.has_keyword(Keyword.EVIL):
                now_surviving = not self.get_state(target, Temporality.FUTURE).is_dead
                if target_was_dying and now_surviving:
                    self.apply_damage(entity, entity, source_state.hp + 1000)

        elif calculated_side.effect_type == EffectType.MANA:
            # Pure mana effect - just grant mana, no other action
            self.add_mana(value)

        # Store this side state as the most recently used (for copycat)
        self._most_recent_die_effect = side_state
        self._die_effect_history.append(side_state)
        self._dice_used_this_turn += 1

        # Record targeting for duel/focus keywords
        # Only count cross-team targeting (enemy targeting me)
        if entity.team != target.team:
            # Initialize set if needed
            if target not in self._targeters_this_turn:
                self._targeters_this_turn[target] = set()
            self._targeters_this_turn[target].add(entity)

        # Track last target for focus keyword
        self._last_die_target = target
        self._last_die_source = entity
        self._last_die_was_self_targeting = (entity == target)

        # Track first enemy attack this turn (for spy keyword)
        # Only count enemy attacks (monster team, damage effect type)
        if (entity.team == Team.MONSTER and
            calculated_side.effect_type == EffectType.DAMAGE and
            self._first_enemy_attack_this_turn is None):
            self._first_enemy_attack_this_turn = side_state

        # Track sides used per entity per turn (for dejavu keyword)
        if self._turn not in self._sides_used_per_turn:
            self._sides_used_per_turn[self._turn] = {}
        if entity not in self._sides_used_per_turn[self._turn]:
            self._sides_used_per_turn[self._turn][entity] = []
        self._sides_used_per_turn[self._turn][entity].append(side_state)

        # Mark die as used (check for multi-use keywords)
        max_uses = 1
        if calculated_side.has_keyword(Keyword.QUAD_USE):
            max_uses = 4
        elif calculated_side.has_keyword(Keyword.DOUBLE_USE):
            max_uses = 2
        elif calculated_side.has_keyword(Keyword.HYPER_USE):
            max_uses = value  # N = calculated pip value
        self.mark_die_used(entity, max_uses)

        # RITE: mark all unused allies as used (side effect after bonus was applied)
        if calculated_side.has_keyword(Keyword.RITE):
            for ally in self._get_allies(entity):
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_used():
                    self.mark_die_used(ally)

        # Apply growth AFTER use (to the original side, not the calculated)
        original_side = die.get_side(side_index)
        if original_side.has_keyword(Keyword.GROWTH) or calculated_side.has_keyword(Keyword.GROWTH):
            original_side.apply_growth()

        # Apply growth variants
        # HYPER_GROWTH: gains +N pips where N = calculated value
        if original_side.has_keyword(Keyword.HYPER_GROWTH) or calculated_side.has_keyword(Keyword.HYPER_GROWTH):
            original_side.apply_growth_n(value)

        # UNDERGROWTH: opposite side gains +1 pip
        if original_side.has_keyword(Keyword.UNDERGROWTH) or calculated_side.has_keyword(Keyword.UNDERGROWTH):
            opposite_index = 5 - side_index
            opposite_side = die.get_side(opposite_index)
            opposite_side.apply_growth()

        # GROOOOOOWTH: all sides gain +1 pip
        if original_side.has_keyword(Keyword.GROOOOOOWTH) or calculated_side.has_keyword(Keyword.GROOOOOOWTH):
            for i in range(6):
                side_to_grow = die.get_side(i)
                side_to_grow.apply_growth()

        # DECAY: this side loses -1 pip
        if original_side.has_keyword(Keyword.DECAY) or calculated_side.has_keyword(Keyword.DECAY):
            original_side.apply_growth_n(-1)

        # DOUBLE_GROWTH: this side gains +2 pips
        if original_side.has_keyword(Keyword.DOUBLE_GROWTH) or calculated_side.has_keyword(Keyword.DOUBLE_GROWTH):
            original_side.apply_growth_n(2)

        # SELF_SHIELD: shield myself for N pips
        if calculated_side.has_keyword(Keyword.SELF_SHIELD):
            self.apply_shield(entity, value)

        # SELF_HEAL: heal myself for N pips
        if calculated_side.has_keyword(Keyword.SELF_HEAL):
            self.apply_heal(entity, value)

        # SELF_PETRIFY: petrify myself (one side)
        if calculated_side.has_keyword(Keyword.SELF_PETRIFY):
            self.apply_petrify(entity, 1)

        # === ADDITIONAL EFFECT KEYWORDS (apply extra effects to target) ===
        # HEAL: also heal target for N pips (in addition to main effect)
        if calculated_side.has_keyword(Keyword.HEAL):
            self.apply_heal(target, value)

        # SHIELD: also shield target for N pips (in addition to main effect)
        if calculated_side.has_keyword(Keyword.SHIELD):
            self.apply_shield(target, value)

        # DAMAGE: also damage target for N pips (in addition to main effect)
        if calculated_side.has_keyword(Keyword.DAMAGE):
            # Apply damage from source to target
            vuln_bonus = self.get_vulnerable_bonus(target)
            self.apply_damage(entity, target, value + vuln_bonus)

        # === STATUS EFFECT KEYWORDS (apply status effects) ===
        # POISON: apply N poison to target (damage at end of turn)
        if calculated_side.has_keyword(Keyword.POISON):
            self.apply_poison(target, value)

        # REGEN: apply N regen to target (heal at end of turn)
        if calculated_side.has_keyword(Keyword.REGEN):
            self.apply_regen(target, value)

        # CLEANSE: remove N points of negative effects from target
        if calculated_side.has_keyword(Keyword.CLEANSE):
            self.apply_cleanse(target, value)

        # SELF_POISON: apply N poison to myself
        if calculated_side.has_keyword(Keyword.SELF_POISON):
            self.apply_poison(entity, value)

        # SELF_REGEN: apply N regen to myself
        if calculated_side.has_keyword(Keyword.SELF_REGEN):
            self.apply_regen(entity, value)

        # SELF_CLEANSE: remove N points of negative effects from myself
        if calculated_side.has_keyword(Keyword.SELF_CLEANSE):
            self.apply_cleanse(entity, value)

        # === BUFF KEYWORDS (apply temporary modifiers) ===
        # WEAKEN: target gets -N to all pips for one turn
        if calculated_side.has_keyword(Keyword.WEAKEN):
            self.apply_weaken(target, value)

        # BOOST: target gets +N to all pips for one turn
        if calculated_side.has_keyword(Keyword.BOOST):
            self.apply_boost(target, value)

        # VULNERABLE: target takes +N damage from dice/spells for one turn
        if calculated_side.has_keyword(Keyword.VULNERABLE):
            self.apply_vulnerable(target, value)

        # SMITH: target gets +N to damage and shield sides for one turn
        if calculated_side.has_keyword(Keyword.SMITH):
            self.apply_smith(target, value)

        # PERMA_BOOST: target gets +N to all pips for the fight
        if calculated_side.has_keyword(Keyword.PERMA_BOOST):
            self.apply_perma_boost(target, value)

        # SELF_VULNERABLE: apply N vulnerable to myself
        if calculated_side.has_keyword(Keyword.SELF_VULNERABLE):
            self.apply_vulnerable(entity, value)

        # HYPNOTISE: set target's DAMAGE sides to 0 for one turn
        if calculated_side.has_keyword(Keyword.HYPNOTISE):
            self.apply_hypnotise(target)

        # === MAX HP MODIFICATION KEYWORDS ===
        # VITALITY: grant target +N max HP (as empty HP) this fight
        # "Empty HP" means max HP increases but current HP does not
        if calculated_side.has_keyword(Keyword.VITALITY):
            self.apply_vitality(target, value)

        # WITHER: grant target -N max HP this fight
        # Reduces max HP (min of 1), current HP is also reduced if above new max
        if calculated_side.has_keyword(Keyword.WITHER):
            self.apply_wither(target, value)

        # === COST KEYWORDS (applied after main effect) ===
        # PAIN: I take N damage (N = pip value)
        if calculated_side.has_keyword(Keyword.PAIN):
            # Pain damage is dealt to the source (can be blocked by shields)
            self.apply_damage(entity, entity, value, is_pending=False)

        # MANACOST: costs N mana to use (N = pip value)
        if calculated_side.has_keyword(Keyword.MANACOST):
            self._total_mana -= value  # Deduct mana after use

        # DEATH: I die after using this side
        if calculated_side.has_keyword(Keyword.DEATH):
            source_state = self.get_state(entity, Temporality.PRESENT)
            source_state.hp = 0  # is_dead property will return True when hp <= 0

        # EXERT: replace all sides with blanks until end of next turn
        if calculated_side.has_keyword(Keyword.EXERT):
            source_state = self.get_state(entity, Temporality.PRESENT)
            source_state.is_exerted = True

        # === ENTITY SUMMONING KEYWORDS ===
        # BONED: summon 1 Bones monster
        if calculated_side.has_keyword(Keyword.BONED):
            self.summon_entity(BONES, 1)

        # HYPER_BONED: summon N Bones monsters (N = pip value)
        if calculated_side.has_keyword(Keyword.HYPER_BONED):
            self.summon_entity(BONES, value)

        # SINGLE_USE: replace this side with a blank (for this fight)
        if original_side.has_keyword(Keyword.SINGLE_USE) or calculated_side.has_keyword(Keyword.SINGLE_USE):
            from .dice import single_use_blank
            die.set_side(side_index, single_use_blank())

        # === GROUP KEYWORDS (apply base effect to all alive allies) ===
        team = self.heroes if entity.team == Team.HERO else self.monsters

        # GROUP_GROWTH: all allies' sides at same index gain +1 pip
        if original_side.has_keyword(Keyword.GROUP_GROWTH) or calculated_side.has_keyword(Keyword.GROUP_GROWTH):
            for ally in team:
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_dead and ally.die:
                    ally_side = ally.die.get_side(side_index)
                    ally_side.apply_growth()

        # GROUP_DECAY: all allies' sides at same index lose -1 pip
        if original_side.has_keyword(Keyword.GROUP_DECAY) or calculated_side.has_keyword(Keyword.GROUP_DECAY):
            for ally in team:
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_dead and ally.die:
                    ally_side = ally.die.get_side(side_index)
                    ally_side.apply_growth_n(-1)

        # GROUP_SINGLE_USE: all allies' sides at same index become blank
        if original_side.has_keyword(Keyword.GROUP_SINGLE_USE) or calculated_side.has_keyword(Keyword.GROUP_SINGLE_USE):
            from .dice import single_use_blank
            for ally in team:
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_dead and ally.die:
                    ally.die.set_side(side_index, single_use_blank())

        # GROUP_EXERT: all allies become exerted
        if calculated_side.has_keyword(Keyword.GROUP_EXERT):
            for ally in team:
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_dead:
                    ally_state.is_exerted = True

        # GROUP_GROOOOOOWTH: all allies' dice get +1 pip on all sides
        if original_side.has_keyword(Keyword.GROUP_GROOOOOOWTH) or calculated_side.has_keyword(Keyword.GROUP_GROOOOOOWTH):
            for ally in team:
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_dead and ally.die:
                    for i in range(6):
                        ally_side = ally.die.get_side(i)
                        ally_side.apply_growth()

        # === SIDE INJECTION KEYWORDS (add keywords to all target's sides) ===
        from .dice import INFLICT_KEYWORD_MAP
        for inflict_kw, target_kw in INFLICT_KEYWORD_MAP.items():
            if calculated_side.has_keyword(inflict_kw):
                self.apply_inflicted(target, target_kw)

        # === ADVANCED COPY KEYWORDS ===
        # SHARE: targets gain all my keywords this turn (except share)
        if calculated_side.has_keyword(Keyword.SHARE):
            keywords_to_share = [kw for kw in calculated_side.keywords if kw != Keyword.SHARE]
            self.apply_share(target, keywords_to_share)

        # ANNUL: targets lose all keywords this turn
        if calculated_side.has_keyword(Keyword.ANNUL):
            self.apply_annul(target)

        # Note: POSSESSED is handled at the effect type determination level.
        # It inverts the "friendly" flag, affecting targeting in the full game.
        # In our simplified system where targets are explicit, possessed allows
        # targeting the "opposite" team with effects that normally target same team.

    def apply_inflicted(self, entity: Entity, keyword: "Keyword"):
        """Apply an Inflicted debuff to an entity, adding keyword to all their sides.

        Args:
            entity: The entity to apply the Inflicted debuff to
            keyword: The keyword that will be added to all the entity's sides
        """
        from .triggers import Buff, Inflicted

        state = self._states[entity]
        inflicted = Inflicted(keyword)
        buff = Buff(personal=inflicted, turns_remaining=None)  # Permanent until cleansed
        state.add_buff(buff)

    def apply_share(self, entity: Entity, keywords: list["Keyword"]):
        """Apply share buff to an entity - all their sides gain the specified keywords.

        The buff lasts for 1 turn. Used by the SHARE keyword which shares
        all keywords (except SHARE itself) from the used side to the target.

        Args:
            entity: The entity to apply the share buff to
            keywords: The keywords to add to all the entity's sides
        """
        from .triggers import Buff, AffectSides, AddKeyword

        if not keywords:
            return  # Nothing to share

        state = self._states[entity]
        affect_sides = AffectSides(
            conditions=None,
            effects=AddKeyword(*keywords)
        )
        buff = Buff(personal=affect_sides, turns_remaining=1)
        state.add_buff(buff)

    def apply_annul(self, entity: Entity):
        """Apply annul buff to an entity - all their sides lose all keywords.

        The buff lasts for 1 turn. Used by the ANNUL keyword.

        Args:
            entity: The entity to apply the annul buff to
        """
        from .triggers import Buff, AffectSides, RemoveAllKeywords

        state = self._states[entity]
        affect_sides = AffectSides(
            conditions=None,
            effects=RemoveAllKeywords()
        )
        buff = Buff(personal=affect_sides, turns_remaining=1)
        state.add_buff(buff)

    def get_most_recent_die_effect(self) -> Optional[SideState]:
        """Get the most recently used die's side state (for copycat keyword)."""
        return self._most_recent_die_effect

    def get_last_n_die_effects(self, n: int) -> list[SideState]:
        """Get the last N die effects (for trio/quin/sept).

        Returns a list of the last N die effects, most recent first.
        If fewer than N effects exist, returns all available effects.
        """
        if n <= 0:
            return []
        return list(reversed(self._die_effect_history[-n:]))

    def get_first_enemy_attack(self) -> Optional[SideState]:
        """Get the first enemy attack this turn (for spy keyword).

        Returns the first damage side used by a monster this turn,
        or None if no enemy has attacked yet.
        """
        return self._first_enemy_attack_this_turn

    def get_sides_used_last_turn(self, entity: Entity) -> list[SideState]:
        """Get the sides used by this entity last turn (for dejavu keyword).

        Returns a list of SideState objects representing the sides
        this entity used last turn, or empty list if none.
        """
        last_turn = self._turn - 1
        if last_turn < 0:
            return []
        if last_turn not in self._sides_used_per_turn:
            return []
        if entity not in self._sides_used_per_turn[last_turn]:
            return []
        return self._sides_used_per_turn[last_turn][entity]

    def _get_multiplier(self, side: "Side") -> int:
        """Get the conditional bonus multiplier (2 normally, 3 if treble present)."""
        from .dice import Keyword
        return 3 if side.has_keyword(Keyword.TREBLE) else 2

    def _apply_conditional_keyword_bonuses(
        self, value: int, side: "Side", source_state: EntityState,
        target_state: EntityState, source_entity: Entity, target_entity: Entity
    ) -> int:
        """Apply conditional bonuses based on keywords, source, and target state.

        Target-check keywords (check target):
        - ENGAGE: x2 if target is at full HP (hp == max_hp)
        - CRUEL: x2 if target is at half HP or less (hp <= max_hp/2)
        - WHAM: x2 if target has shields
        - SQUISH: x2 if target has the least HP of all living entities
        - UPPERCUT: x2 if target has the most HP of all living entities
        - TERMINAL: x2 if target has exactly 1 HP
        - CENTURY: x2 if target has 100+ HP

        Source-check keywords (check self):
        - PRISTINE: x2 if I have full HP
        - DEATHWISH: x2 if I am dying this turn (future HP <= 0)
        - ARMOURED: x2 if I have shields
        - MOXIE: x2 if I have the least HP of all living entities
        - BULLY: x2 if I have the most HP of all living entities
        - REBORN: x2 if I died this fight

        Self-target keywords:
        - EGO: x2 if targeting myself

        Note: If TREBLE keyword is present, all x2 multipliers become x3.

        Returns the modified value after applying all applicable bonuses.
        """
        from .dice import Keyword

        # Get multiplier (2 normally, 3 with treble)
        mult = self._get_multiplier(side)

        # +N pip bonus keywords (applied before x2 multipliers)
        # BLOODLUST: +N where N = damaged enemies
        if side.has_keyword(Keyword.BLOODLUST):
            value += self.count_damaged_enemies()

        # CHARGED: +N where N = current mana
        if side.has_keyword(Keyword.CHARGED):
            value += self.get_total_mana()

        # STEEL: +N where N = my shields
        if side.has_keyword(Keyword.STEEL):
            value += source_state.shield

        # FLESH: +N where N = my current HP
        if side.has_keyword(Keyword.FLESH):
            value += source_state.hp

        # RAINBOW: +N where N = number of keywords on this side
        if side.has_keyword(Keyword.RAINBOW):
            # Count keywords excluding RAINBOW itself to avoid infinite bonus
            keyword_count = len(side.keywords) - 1
            value += keyword_count

        # FLURRY: +N where N = times I've been used this turn
        if side.has_keyword(Keyword.FLURRY):
            value += source_state.times_used_this_turn

        # VIGIL: +N where N = defeated allies
        if side.has_keyword(Keyword.VIGIL):
            value += self.count_dead_allies(source_entity)

        # FIZZ: +N where N = abilities used this turn (before this one)
        if side.has_keyword(Keyword.FIZZ):
            value += self._dice_used_this_turn

        # DEFY: +N where N = pending damage to source
        if side.has_keyword(Keyword.DEFY):
            value += self._get_pending_damage_to(source_entity)

        # PLAGUE: +N where N = total poison on all characters
        if side.has_keyword(Keyword.PLAGUE):
            value += self.get_total_poison_all()

        # ACIDIC: +N where N = poison on me
        if side.has_keyword(Keyword.ACIDIC):
            value += self.get_poison_on_entity(source_entity)

        # BUFFED: +N where N = number of buffs on me
        if side.has_keyword(Keyword.BUFFED):
            value += self.get_buff_count(source_entity)

        # AFFECTED: +N where N = number of triggers affecting me
        if side.has_keyword(Keyword.AFFECTED):
            value += self.get_trigger_count(source_entity)

        # SKILL: +N where N = my level/tier
        if side.has_keyword(Keyword.SKILL):
            value += self.get_entity_tier(source_entity)

        # RITE: +1 per unused ally (excluding self)
        if side.has_keyword(Keyword.RITE):
            unused_count = 0
            for ally in self._get_allies(source_entity):
                ally_state = self.get_state(ally, Temporality.PRESENT)
                if not ally_state.is_used():
                    unused_count += 1
            value += unused_count

        # TRILL: trio + skill - add tier bonus (multiplier handled below)
        if side.has_keyword(Keyword.TRILL):
            value += self.get_entity_tier(source_entity)

        # ERA: +N where N = turns elapsed
        if side.has_keyword(Keyword.ERA):
            value += source_state.turns_elapsed

        # MINUS_ERA: -N where N = turns elapsed (inverted era)
        if side.has_keyword(Keyword.MINUS_ERA):
            value -= source_state.turns_elapsed

        # x2 multiplier keywords (applied after +N bonuses)
        # Note: with TREBLE keyword, these become x3 instead of x2 (mult variable)

        # ENGAGE: x2 vs full HP targets
        if side.has_keyword(Keyword.ENGAGE):
            if target_state.hp == target_state.max_hp:
                value *= mult

        # CRUEL: x2 vs targets at half HP or less
        if side.has_keyword(Keyword.CRUEL):
            if target_state.hp <= target_state.max_hp // 2:
                value *= mult

        # WHAM: x2 vs targets with shields
        if side.has_keyword(Keyword.WHAM):
            if target_state.shield > 0:
                value *= mult

        # SQUISH: x2 vs targets with least HP of all
        if side.has_keyword(Keyword.SQUISH):
            min_hp = self._get_min_hp_of_all()
            if target_state.hp == min_hp:
                value *= mult

        # UPPERCUT: x2 vs targets with most HP of all
        if side.has_keyword(Keyword.UPPERCUT):
            max_hp = self._get_max_hp_of_all()
            if target_state.hp == max_hp:
                value *= mult

        # TERMINAL: x2 vs targets on exactly 1 HP
        if side.has_keyword(Keyword.TERMINAL):
            if target_state.hp == 1:
                value *= mult

        # CENTURY: x2 vs targets with 100+ HP
        if side.has_keyword(Keyword.CENTURY):
            if target_state.hp >= 100:
                value *= mult

        # PRISTINE: x2 if I have full HP
        if side.has_keyword(Keyword.PRISTINE):
            if source_state.hp == source_state.max_hp:
                value *= mult

        # DEATHWISH: x2 if I am dying this turn
        if side.has_keyword(Keyword.DEATHWISH):
            future_state = self.get_state(source_entity, Temporality.FUTURE)
            if future_state.hp <= 0:
                value *= mult

        # ARMOURED: x2 if I have shields
        if side.has_keyword(Keyword.ARMOURED):
            if source_state.shield > 0:
                value *= mult

        # MOXIE: x2 if I have the least HP of all living entities
        if side.has_keyword(Keyword.MOXIE):
            min_hp = self._get_min_hp_of_all()
            if source_state.hp == min_hp:
                value *= mult

        # BULLY: x2 if I have the most HP of all living entities
        if side.has_keyword(Keyword.BULLY):
            max_hp = self._get_max_hp_of_all()
            if source_state.hp == max_hp:
                value *= mult

        # REBORN: x2 if I died this fight
        if side.has_keyword(Keyword.REBORN):
            if source_state.deaths_this_fight > 0:
                value *= mult

        # PATIENT: x2 if I was not used last turn (and not on first turn)
        if side.has_keyword(Keyword.PATIENT):
            # Must be past the first turn and die must not have been used last turn
            if source_state.turns_elapsed >= 1 and not source_state.used_last_turn:
                value *= mult

        # EGO: x2 if targeting myself
        if side.has_keyword(Keyword.EGO):
            if source_entity == target_entity:
                value *= mult

        # SERRATED: x2 if target gained no shields this turn
        # (has no shields AND hasn't blocked any damage with shields)
        if side.has_keyword(Keyword.SERRATED):
            if target_state.shield == 0 and target_state.damage_blocked == 0:
                value *= mult

        # UNDERDOG: x2 if source HP < target HP
        if side.has_keyword(Keyword.UNDERDOG):
            if source_state.hp < target_state.hp:
                value *= mult

        # OVERDOG: x2 if source HP > target HP
        if side.has_keyword(Keyword.OVERDOG):
            if source_state.hp > target_state.hp:
                value *= mult

        # DOG: x2 if source HP == target HP
        if side.has_keyword(Keyword.DOG):
            if source_state.hp == target_state.hp:
                value *= mult

        # HYENA: x2 if source HP and target HP are coprime (GCD == 1)
        if side.has_keyword(Keyword.HYENA):
            if gcd(source_state.hp, target_state.hp) == 1:
                value *= mult

        # TALL: x2 if target is topmost (index 0 in their team)
        if side.has_keyword(Keyword.TALL):
            team_list = self.heroes if target_entity.team == Team.HERO else self.monsters
            if team_list and team_list[0] == target_entity:
                value *= mult

        # CHAIN: x2 if previous die shares a keyword with this side
        if side.has_keyword(Keyword.CHAIN):
            previous = self.get_most_recent_die_effect()
            if previous is not None:
                prev_keywords = previous.calculated_effect.keywords
                # Check if any keyword is shared (excluding CHAIN itself)
                current_keywords = side.keywords - {Keyword.CHAIN}
                if current_keywords & prev_keywords:  # Set intersection
                    value *= mult

        # INSPIRED: x2 if previous die had more pips than this side (using visible value)
        if side.has_keyword(Keyword.INSPIRED):
            previous = self.get_most_recent_die_effect()
            if previous is not None:
                if previous.calculated_effect.get_visible_value() > side.calculated_value:
                    value *= mult

        # FIRST: x2 if no dice used this turn (before this one)
        if side.has_keyword(Keyword.FIRST):
            if self._dice_used_this_turn == 0:
                value *= mult

        # SIXTH: x2 if this is the 6th die used this turn
        if side.has_keyword(Keyword.SIXTH):
            if self._dice_used_this_turn == 5:
                value *= mult

        # STEP: x2 if previous 2 dice values form a consecutive run
        if side.has_keyword(Keyword.STEP):
            if self._is_consecutive_run(2, side.calculated_value):
                value *= mult

        # RUN: x2 if previous 3 dice values form a consecutive run
        if side.has_keyword(Keyword.RUN):
            if self._is_consecutive_run(3, side.calculated_value):
                value *= mult

        # SPRINT: x2 if previous 5 dice values form a consecutive run
        if side.has_keyword(Keyword.SPRINT):
            if self._is_consecutive_run(5, side.calculated_value):
                value *= mult

        # SLOTH: x2 if source has more blank sides than target
        if side.has_keyword(Keyword.SLOTH):
            source_blanks = self._count_blank_sides(source_entity)
            target_blanks = self._count_blank_sides(target_entity)
            if source_blanks > target_blanks:
                value *= mult

        # === ANTI* VARIANTS (inverted condition) ===
        # ANTI_ENGAGE: x2 if target NOT at full HP
        if side.has_keyword(Keyword.ANTI_ENGAGE):
            if target_state.hp != target_state.max_hp:
                value *= mult

        # ANTI_PRISTINE: x2 if source NOT at full HP
        if side.has_keyword(Keyword.ANTI_PRISTINE):
            if source_state.hp != source_state.max_hp:
                value *= mult

        # ANTI_DEATHWISH: x2 if source NOT at 1HP (not dying)
        if side.has_keyword(Keyword.ANTI_DEATHWISH):
            future_state = self.get_state(source_entity, Temporality.FUTURE)
            if future_state.hp > 0:
                value *= mult

        # ANTI_DOG: x2 if source HP != target HP (inverted dog)
        if side.has_keyword(Keyword.ANTI_DOG):
            if source_state.hp != target_state.hp:
                value *= mult

        # ANTI_PAIR: x2 if previous die had DIFFERENT pip value (using visible value)
        if side.has_keyword(Keyword.ANTI_PAIR):
            previous = self.get_most_recent_die_effect()
            if previous is not None:
                if side.calculated_value != previous.calculated_effect.get_visible_value():
                    value *= mult

        # === SWAP* VARIANTS (swap source/target check) ===
        # SWAP_ENGAGE: x2 if SOURCE at full HP (instead of target)
        if side.has_keyword(Keyword.SWAP_ENGAGE):
            if source_state.hp == source_state.max_hp:
                value *= mult

        # SWAP_CRUEL: x2 if SOURCE at or below half HP (instead of target)
        if side.has_keyword(Keyword.SWAP_CRUEL):
            if source_state.hp <= source_state.max_hp // 2:
                value *= mult

        # SWAP_DEATHWISH: x2 if TARGET at 1HP (instead of source)
        if side.has_keyword(Keyword.SWAP_DEATHWISH):
            future_state = self.get_state(target_entity, Temporality.FUTURE)
            if future_state.hp <= 0:
                value *= mult

        # SWAP_TERMINAL: x2 if TARGET at exactly 1HP (same check as swapDeathwish in Java)
        if side.has_keyword(Keyword.SWAP_TERMINAL):
            if target_state.hp == 1:
                value *= mult

        # === HALVE* VARIANTS (x0.5 instead of x2) ===
        # HALVE_ENGAGE: x0.5 if target at full HP
        if side.has_keyword(Keyword.HALVE_ENGAGE):
            if target_state.hp == target_state.max_hp:
                value //= 2

        # HALVE_DEATHWISH: x0.5 if source dying
        if side.has_keyword(Keyword.HALVE_DEATHWISH):
            future_state = self.get_state(source_entity, Temporality.FUTURE)
            if future_state.hp <= 0:
                value //= 2

        # === COMBINED KEYWORDS (TC4X - both conditions = x4) ===
        # ENGINE: engage + pristine = x4 if target full HP AND source full HP
        if side.has_keyword(Keyword.ENGINE):
            engage_met = target_state.hp == target_state.max_hp
            pristine_met = source_state.hp == source_state.max_hp
            if engage_met and pristine_met:
                value *= 4

        # PRISWISH: pristine + deathwish = x4 if source full HP AND source dying
        # (Only possible if max_hp == 1)
        if side.has_keyword(Keyword.PRISWISH):
            pristine_met = source_state.hp == source_state.max_hp
            future_state = self.get_state(source_entity, Temporality.FUTURE)
            deathwish_met = future_state.hp <= 0
            if pristine_met and deathwish_met:
                value *= 4

        # === COMBINED KEYWORDS (XOR) ===
        # PAXIN: pair XOR chain = x3 if exactly one condition met (not affected by treble)
        if side.has_keyword(Keyword.PAXIN):
            # Check pair condition (using visible value for previous die)
            pair_met = False
            recent = self.get_most_recent_die_effect()
            if recent is not None:
                current_value = side.calculated_value
                prev_value = recent.calculated_effect.get_visible_value()
                pair_met = current_value == prev_value

            # Check chain condition
            chain_met = False
            if recent is not None:
                prev_keywords = recent.calculated_effect.keywords
                current_keywords = side.keywords - {Keyword.PAXIN}
                chain_met = bool(current_keywords & prev_keywords)

            # XOR: exactly one must be true (x3 per Java XOR implementation)
            if pair_met != chain_met:
                value *= 3

        # === COMBINED KEYWORDS (ConditionBonus - condition + pip bonus) ===
        # ENGARGED: engage + charged = x2 if target full HP, already has +N mana from charged
        if side.has_keyword(Keyword.ENGARGED):
            # +N mana pips (charged component)
            value += self.get_total_mana()
            # x2 if target full HP (engage component) - affected by treble
            if target_state.hp == target_state.max_hp:
                value *= mult

        # CRUESH: cruel + flesh = x2 if target at half HP or less, already has +N HP
        if side.has_keyword(Keyword.CRUESH):
            # +N HP pips (flesh component)
            value += source_state.hp
            # x2 if target at half HP or less (cruel component) - affected by treble
            if target_state.hp <= target_state.max_hp // 2:
                value *= mult

        # PRISTEEL: pristine + steel = x2 if source full HP, already has +N shields
        if side.has_keyword(Keyword.PRISTEEL):
            # +N shield pips (steel component)
            value += source_state.shield
            # x2 if source full HP (pristine component) - affected by treble
            if source_state.hp == source_state.max_hp:
                value *= mult

        # DEATHLUST: deathwish + bloodlust = x2 if source dying, +N damaged enemies
        if side.has_keyword(Keyword.DEATHLUST):
            # +N damaged enemies (bloodlust component)
            value += self.count_damaged_enemies()
            # x2 if source dying (deathwish component) - affected by treble
            future_state = self.get_state(source_entity, Temporality.FUTURE)
            if future_state.hp <= 0:
                value *= mult

        # === TARGET TRACKING KEYWORDS ===
        # DUEL: x2 vs enemies who have targeted me this turn
        if side.has_keyword(Keyword.DUEL):
            targeters = self._get_entities_that_targeted_me(source_entity)
            if target_entity in targeters:
                value *= mult

        # FOCUS: x2 if targeting same entity as previous die
        if side.has_keyword(Keyword.FOCUS):
            if self._target_matches_previous(target_entity):
                value *= mult

        # HALVE_DUEL: x0.5 vs enemies who have targeted me this turn
        if side.has_keyword(Keyword.HALVE_DUEL):
            targeters = self._get_entities_that_targeted_me(source_entity)
            if target_entity in targeters:
                value //= 2

        # DUEGUE: duel + plague = x2 vs enemies who targeted me + total poison bonus
        if side.has_keyword(Keyword.DUEGUE):
            # +N pips where N = total poison (plague component)
            value += self.get_total_poison_all()
            # x2 vs enemies who targeted me (duel component)
            targeters = self._get_entities_that_targeted_me(source_entity)
            if target_entity in targeters:
                value *= mult

        # UNDEROCUS: underdog + focus = x2 if my HP < target HP AND same target as previous die
        if side.has_keyword(Keyword.UNDEROCUS):
            underdog_met = source_state.hp < target_state.hp
            focus_met = self._target_matches_previous(target_entity)
            if underdog_met and focus_met:
                value *= 4  # TC4X - both conditions = x4

        # === MINUS VARIANTS ===
        # MINUS_FLESH: -N pips where N = my current HP
        if side.has_keyword(Keyword.MINUS_FLESH):
            value -= source_state.hp

        return value

    def _get_min_hp_of_all(self) -> int:
        """Get the minimum HP among all living entities (heroes and monsters)."""
        min_hp = 5000  # Large sentinel value like Java
        for entity in self.heroes + self.monsters:
            state = self._states.get(entity)
            if state and not state.is_dead:
                min_hp = min(min_hp, state.hp)
        return min_hp

    def _get_max_hp_of_all(self) -> int:
        """Get the maximum HP among all living entities (heroes and monsters)."""
        max_hp = 0
        for entity in self.heroes + self.monsters:
            state = self._states.get(entity)
            if state and not state.is_dead:
                max_hp = max(max_hp, state.hp)
        return max_hp

    def _get_pending_damage_to(self, entity: Entity) -> int:
        """Get total pending damage targeting an entity.

        Used by DEFY keyword to get +N bonus where N = incoming damage.
        """
        total = 0
        for pending in self._pending:
            if pending.target == entity:
                total += pending.amount
        return total

    def _get_adjacent_entities(self, target: Entity, above: int, below: int) -> list[Entity]:
        """Get entities adjacent to target.

        Args:
            target: The target entity
            above: Number of entities above to include (0 for none)
            below: Number of entities below to include (0 for none)

        Returns:
            List of adjacent entities (not including target itself)
        """
        team_list = self.heroes if target.team == Team.HERO else self.monsters
        target_pos = target.position
        result = []

        # Get alive entities sorted by position
        alive = [e for e in team_list if not self._states[e].is_dead]
        alive.sort(key=lambda e: e.position)

        # Find target index in sorted list
        try:
            target_idx = alive.index(target)
        except ValueError:
            return result

        # Get entities above (lower index = above)
        for i in range(1, above + 1):
            idx = target_idx - i
            if idx >= 0:
                result.append(alive[idx])

        # Get entities below (higher index = below)
        for i in range(1, below + 1):
            idx = target_idx + i
            if idx < len(alive):
                result.append(alive[idx])

        return result

    def _get_entities_attacking(self, target: Entity) -> list[Entity]:
        """Get all entities with pending damage targeting the given entity.

        Used by REPEL keyword to deal damage to attackers.

        Returns:
            List of unique entities that have pending damage on target
        """
        attackers = set()
        for pending in self._pending:
            if pending.target == target and pending.source is not None:
                attackers.add(pending.source)
        return list(attackers)

    def _get_entities_that_targeted_me(self, me: Entity) -> set[Entity]:
        """Get all enemies that have targeted this entity this turn.

        Used by DUEL keyword for x2 bonus against enemies who targeted me.
        Only returns living enemies.

        Returns:
            Set of enemy entities that targeted me this turn
        """
        targeters = self._targeters_this_turn.get(me, set())
        # Filter to only living enemies
        living_targeters = set()
        for targeter in targeters:
            state = self._states.get(targeter)
            if state and not state.is_dead:
                living_targeters.add(targeter)
        return living_targeters

    def _target_matches_previous(self, target: Entity) -> bool:
        """Check if target matches the target of the previous die command.

        Used by FOCUS keyword for x2 bonus if targeting same entity.
        Also handles edge case where previous die was self-targeting:
        if previous was self-targeting and current target == previous source, it counts.

        Returns:
            True if target matches previous die's target
        """
        if self._last_die_target is None:
            return False

        # Direct match: current target == last target
        if target == self._last_die_target:
            return True

        # Edge case: if last die was self-targeting, focus triggers
        # if current target equals the source of that self-targeting die
        if self._last_die_was_self_targeting and target == self._last_die_source:
            return True

        return False

    def _is_consecutive_run(self, n: int, current_value: int) -> bool:
        """Check if previous n-1 dice plus current form a consecutive run.

        A consecutive run is a sequence of values that are all consecutive
        (e.g., 1-2-3 or 3-2-1 both count as runs).

        Args:
            n: Total length of run to check (including current die)
            current_value: The value of the current die being used

        Returns:
            True if the last n-1 dice values plus current form a consecutive run.
        """
        if n <= 1:
            return True

        # Get last n-1 die effects
        previous = self.get_last_n_die_effects(n - 1)
        if len(previous) < n - 1:
            return False  # Not enough previous dice

        # Build list of values: previous dice + current
        values = [p.calculated_effect.calculated_value for p in previous]
        values.append(current_value)

        # Sort and check if consecutive
        sorted_vals = sorted(values)
        for i in range(1, len(sorted_vals)):
            if sorted_vals[i] != sorted_vals[i - 1] + 1:
                return False
        return True

    def _count_blank_sides(self, entity: Entity) -> int:
        """Count the number of blank sides on an entity's die.

        A blank side has EffectType.BLANK.
        """
        from .effects import EffectType

        if entity.die is None:
            return 0

        count = 0
        for i in range(6):
            side = entity.die.get_side(i)
            if side and side.effect_type == EffectType.BLANK:
                count += 1
        return count

    def is_valid_target(self, source: Entity, target: Entity, side: "Side") -> bool:
        """Check if target is valid given the side's targeting restriction keywords.

        Targeting keywords:
        - ELIMINATE: can only target enemy with least HP
        - HEAVY: can only target enemies with 5+ HP
        - GENEROUS: cannot target self
        - SCARED: target must have N or less HP (N = side value)
        - PICKY: target must have exactly N HP (N = side value)

        Returns True if target is valid, False otherwise.
        """
        from .dice import Keyword

        target_state = self._states.get(target)
        if not target_state or target_state.is_dead:
            return False

        # ELIMINATE: can only target entity with least HP among enemies
        if side.has_keyword(Keyword.ELIMINATE):
            min_hp = self._get_min_hp_of_all()
            if target_state.hp != min_hp:
                return False

        # HEAVY: can only target entities with 5+ HP
        if side.has_keyword(Keyword.HEAVY):
            if target_state.hp < 5:
                return False

        # GENEROUS: cannot target self
        if side.has_keyword(Keyword.GENEROUS):
            if source == target:
                return False

        # SCARED: target must have N or less HP (N = side value)
        if side.has_keyword(Keyword.SCARED):
            if target_state.hp > side.calculated_value:
                return False

        # PICKY: target must have exactly N HP (N = side value)
        if side.has_keyword(Keyword.PICKY):
            if target_state.hp != side.calculated_value:
                return False

        return True

    def is_side_usable(self, side: "Side", is_cantrip: bool = False) -> bool:
        """Check if a side can be used at all.

        UNUSABLE: Cannot be used manually (cantrip is still allowed)

        Args:
            side: The side to check
            is_cantrip: Whether this is being triggered by a cantrip effect

        Returns True if side can be used, False otherwise.
        """
        from .dice import Keyword

        # UNUSABLE: Cannot be used manually (cantrip still allowed)
        if side.has_keyword(Keyword.UNUSABLE):
            if not is_cantrip:
                return False

        return True

    def apply_petrify(self, target: Entity, amount: int):
        """Apply petrification to target's die sides.

        Petrifies sides in order: Top, Left, Middle, Right, Rightmost, Bottom
        (indices: 0, 2, 4, 3, 5, 1)

        If trying to petrify more than 6 sides, excess is stored as None.
        This caps effective petrification at 6 sides.
        """
        from .dice import PETRIFY_ORDER

        self._record_action()

        state = self._states[target]
        existing = list(state.petrified_sides)

        # Find sides to petrify
        sides_to_petrify = []
        for i in range(len(PETRIFY_ORDER)):
            if len(sides_to_petrify) >= amount:
                break
            side_index = PETRIFY_ORDER[i]
            if side_index not in existing:
                sides_to_petrify.append(side_index)

        # If we need more than available sides, add None for overflow
        while len(sides_to_petrify) < amount:
            sides_to_petrify.append(None)

        # Add new petrified sides
        new_petrified = existing + sides_to_petrify

        self._update_state(target, petrified_sides=new_petrified)

    def apply_cleanse_petrify(self, target: Entity, amount: int):
        """Remove petrification from target's die sides.

        Removes petrification in reverse order (last petrified first).
        """
        self._record_action()

        state = self._states[target]
        petrified = list(state.petrified_sides)

        # Remove from end (reverse order of petrification)
        for _ in range(min(amount, len(petrified))):
            if petrified:
                petrified.pop()

        self._update_state(target, petrified_sides=petrified)

    def apply_poison(self, target: Entity, amount: int):
        """Apply poison to target. Stacks with existing poison.

        Poison deals damage equal to stacks at end of each turn (direct, bypasses shields).
        Poison can be removed by cleanse effects.
        """
        from .triggers import Poison, Buff

        self._record_action()

        state = self._states[target]

        # Check for poison immunity
        for personal in state.get_active_personals():
            if hasattr(personal, 'poison_specific_immunity') and personal.poison_specific_immunity():
                return  # Immune to poison

        # Add poison buff (will merge with existing poison)
        new_buffs = list(state.buffs)
        buff = Buff(personal=Poison(amount))
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_regen(self, target: Entity, amount: int):
        """Apply regen to target. Stacks with existing regen.

        Regen heals amount at end of each turn (capped at max HP).
        """
        from .triggers import Regen, Buff

        self._record_action()

        state = self._states[target]

        # Check for healing immunity
        for personal in state.get_active_personals():
            if hasattr(personal, 'immune_to_healing') and personal.immune_to_healing():
                return  # Immune to healing

        # Add regen buff (will merge with existing regen)
        new_buffs = list(state.buffs)
        buff = Buff(personal=Regen(amount))
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_cleanse(self, target: Entity, amount: int):
        """Apply cleanse to target, removing negative effects.

        Cleanse removes points of negative effects (poison, weaken, petrify, inflict).
        The cleanse budget is tracked per cleanse type.
        """
        from .triggers import Cleansed, Buff, CleanseType

        self._record_action()

        state = self._states[target]

        # Add cleansed buff to provide cleanse budget
        new_buffs = list(state.buffs)
        buff = Buff(personal=Cleansed(amount), turns_remaining=1)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)

        # Now cleanse existing debuffs using the budget
        # Iterate in reverse to safely remove
        for i in range(len(state_copy.buffs) - 1, -1, -1):
            b = state_copy.buffs[i]
            fully_cleansed = state_copy._attempt_cleanse_new_buff(b)
            if fully_cleansed:
                state_copy.buffs.pop(i)

        self._states[target] = state_copy

    # =========================================================================
    # Buff system methods (weaken, boost, vulnerable, smith, permaBoost)
    # =========================================================================

    def apply_weaken(self, target: Entity, amount: int):
        """Apply weaken to target. Stacks with existing weaken.

        Weaken reduces all side values by N for one turn.
        Weaken can be removed by cleanse effects.
        """
        from .triggers import Weaken, Buff

        self._record_action()

        state = self._states[target]

        # Add weaken buff (will merge with existing weaken)
        new_buffs = list(state.buffs)
        buff = Buff(personal=Weaken(amount), turns_remaining=1)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_boost(self, target: Entity, amount: int):
        """Apply boost to target. Stacks with existing boost.

        Boost increases all side values by N for one turn.
        """
        from .triggers import AffectSides, FlatBonus, Buff

        self._record_action()

        state = self._states[target]

        # Add boost buff (AffectSides with FlatBonus)
        new_buffs = list(state.buffs)
        buff = Buff(personal=AffectSides(effects=FlatBonus(amount)), turns_remaining=1)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_vulnerable(self, target: Entity, amount: int):
        """Apply vulnerable to target. Stacks with existing vulnerable.

        Vulnerable increases damage taken from dice/spells by N for one turn.
        Does NOT affect damage from poison, pain, or other sources.
        """
        from .triggers import Vulnerable, Buff

        self._record_action()

        state = self._states[target]

        # Add vulnerable buff (will merge with existing vulnerable)
        new_buffs = list(state.buffs)
        buff = Buff(personal=Vulnerable(amount), turns_remaining=1)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_smith(self, target: Entity, amount: int):
        """Apply smith to target.

        Smith increases damage and shield side values by N for one turn.
        Only affects sides with EffectType.DAMAGE or EffectType.SHIELD.
        """
        from .triggers import AffectSides, FlatBonus, TypeCondition, Buff
        from .effects import EffectType

        self._record_action()

        state = self._states[target]

        # Add smith buff (AffectSides with TypeCondition and FlatBonus)
        new_buffs = list(state.buffs)
        buff = Buff(
            personal=AffectSides(
                conditions=TypeCondition(EffectType.DAMAGE, EffectType.SHIELD),
                effects=FlatBonus(amount)
            ),
            turns_remaining=1
        )
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_perma_boost(self, target: Entity, amount: int):
        """Apply permanent boost to target. Stacks with existing boost.

        PermaBoost increases all side values by N for the entire fight.
        """
        from .triggers import AffectSides, FlatBonus, Buff

        self._record_action()

        state = self._states[target]

        # Add permaBoost buff (AffectSides with FlatBonus, no turn limit)
        new_buffs = list(state.buffs)
        buff = Buff(personal=AffectSides(effects=FlatBonus(amount)), turns_remaining=None)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def apply_vitality(self, target: Entity, amount: int):
        """Apply vitality to target - increase max HP by N (as empty HP).

        Vitality increases max HP but does NOT increase current HP.
        This creates "empty" HP slots that can be healed into.
        This lasts for the entire fight.
        """
        self._record_action()

        state = self._states[target]
        # Increase max HP only, leave current HP unchanged
        new_max_hp = state.max_hp + amount
        self._update_state(target, max_hp=new_max_hp)

    def apply_wither(self, target: Entity, amount: int):
        """Apply wither to target - decrease max HP by N.

        Wither decreases max HP (minimum 1).
        If current HP is higher than new max HP, current HP is reduced to new max.
        This lasts for the entire fight.
        """
        self._record_action()

        state = self._states[target]
        # Decrease max HP (floor at 1)
        new_max_hp = max(1, state.max_hp - amount)
        # Cap current HP at new max HP
        new_hp = min(state.hp, new_max_hp)
        self._update_state(target, hp=new_hp, max_hp=new_max_hp)

    def apply_hypnotise(self, target: Entity):
        """Apply hypnotise to target - set all DAMAGE sides to 0 for one turn.

        Hypnotise creates a buff that modifies all DAMAGE-type sides on the
        target's die to have 0 pips. This is a 1-turn debuff.

        Java: Buff(1, AffectSides(TypeCondition(EffType.Damage), SetValue(0)))
        """
        from .triggers import AffectSides, TypeCondition, SetValue, Buff

        self._record_action()

        state = self._states[target]

        # Create AffectSides that matches DAMAGE sides and sets value to 0
        affect_sides = AffectSides(
            conditions=TypeCondition(EffectType.DAMAGE),
            effects=SetValue(0)
        )

        # Add buff (1 turn duration)
        new_buffs = list(state.buffs)
        buff = Buff(personal=affect_sides, turns_remaining=1)
        state_copy = replace(state, buffs=new_buffs)
        state_copy.add_buff(buff)
        self._states[target] = state_copy

    def get_vulnerable_bonus(self, target: Entity) -> int:
        """Get total vulnerable bonus on an entity.

        Returns how much extra damage this entity takes from dice/spells.
        """
        state = self._states.get(target)
        if state is None:
            return 0

        total = 0
        for buff in state.buffs:
            if hasattr(buff.personal, 'get_vulnerable_bonus'):
                total += buff.personal.get_vulnerable_bonus()
        return total

    def get_buff_count(self, entity: Entity) -> int:
        """Get number of buffs on an entity (for buffed keyword)."""
        state = self._states.get(entity)
        if state is None:
            return 0
        return len(state.buffs)

    def get_trigger_count(self, entity: Entity) -> int:
        """Get number of triggers affecting an entity (for affected keyword)."""
        state = self._states.get(entity)
        if state is None:
            return 0
        return len(state.get_active_personals())

    def get_entity_tier(self, entity: Entity) -> int:
        """Get entity's tier/level (for skill keyword)."""
        # TODO: Implement proper tier tracking when hero system is added
        # For now, return 1 as default tier
        return getattr(entity.entity_type, 'tier', 1)

    def get_side_state(self, entity: Entity, side_index: int) -> SideState:
        """Get the calculated state for a side of an entity's die.

        This applies all active triggers (buffs, items, etc.) to the side.

        Args:
            entity: The entity whose die to check
            side_index: The side index (0-5)

        Returns:
            SideState with the calculated effect after all triggers applied.
        """
        state = self._states.get(entity)
        if state is None:
            raise ValueError(f"Entity {entity} not found in fight")
        return state.get_side_state(side_index, fight_log=self)

    def get_total_poison_all(self) -> int:
        """Get total poison on all characters (for plague keyword)."""
        total = 0
        for entity, state in self._states.items():
            total += state.get_poison_damage_taken()
        return total

    def get_poison_on_entity(self, entity: Entity) -> int:
        """Get poison on a specific entity (for acidic keyword)."""
        state = self._states.get(entity)
        if state is None:
            return 0
        return state.get_poison_damage_taken()

    def mark_die_used(self, entity: Entity, max_uses: int = 1):
        """Mark entity's die as used (or partially used for multi-use keywords).

        Args:
            entity: The entity whose die was used
            max_uses: How many times the die can be used before being fully used.
                      Default 1 for normal dies, higher for doubleUse/quadUse keywords.
        """
        self._record_action()
        state = self._states[entity]
        times = state.times_used_this_turn + 1
        is_used = times >= max_uses

        self._update_state(entity, used_die=is_used, times_used_this_turn=times)

    def recharge_die(self, entity: Entity):
        """Recharge entity's die - allows it to be used again.

        Called by RESCUE keyword (when healing saves a dying hero)
        and RAMPAGE keyword (when attack kills an enemy).
        """
        self._record_action()
        self._update_state(entity, used_die=False, times_used_this_turn=0)

    def apply_heal_rescue(self, source: Entity, target: Entity, amount: int):
        """Apply heal with RESCUE keyword.

        If the heal saves a dying hero (future HP was <= 0, now > 0),
        the source's die is recharged and can be used again.

        Flow:
        1. Check if target was dying before heal (future HP <= 0)
        2. Apply heal
        3. Mark source's die as used
        4. Check if target is now surviving (future HP > 0)
        5. If was dying and now surviving = rescue, recharge die
        """
        # Check if target was dying before heal
        was_dying = self.get_state(target, Temporality.FUTURE).is_dead

        # Apply heal
        self.apply_heal(target, amount)

        # Mark die as used
        self.mark_die_used(source)

        # Check for rescue
        now_surviving = not self.get_state(target, Temporality.FUTURE).is_dead
        if was_dying and now_surviving:
            # Rescue! Recharge the die
            self.recharge_die(source)

    def apply_rampage_damage_all(self, source: Entity, amount: int):
        """Apply damage to ALL entities with RAMPAGE keyword.

        burningFlail-style: hits all entities for N damage.
        If any entity dies, the source's die is recharged.

        Flow:
        1. Record which entities were alive before
        2. Apply damage to all present entities (heroes and monsters)
        3. Mark source's die as used
        4. Check if any entity died
        5. If any died = kill, recharge die
        """
        # Record alive entities before damage
        alive_before = set()
        for entity in self.heroes + self.monsters:
            state = self.get_state(entity, Temporality.PRESENT)
            if not state.is_dead:
                alive_before.add(entity)

        # Apply damage to all present entities
        for entity in list(alive_before):
            if entity != source:  # Don't hit yourself
                self.apply_damage(source, entity, amount, is_pending=False)

        # Mark die as used
        self.mark_die_used(source)

        # Check for kills
        killed = False
        for entity in alive_before:
            state = self.get_state(entity, Temporality.PRESENT)
            if state.is_dead:
                killed = True
                break

        if killed:
            # Kill! Recharge the die
            self.recharge_die(source)

    def add_trigger(self, target: Entity, personal):
        """Add a trigger (Personal) to an entity as a permanent buff.

        This is a helper method equivalent to Java's TestUtils.addTrigger.
        """
        from .triggers import Buff

        self._record_action()
        state = self._states[target]
        state.add_buff(Buff(personal=personal, turns_remaining=None))

    def turn_into(self, target: Entity, new_side):
        """Replace ALL sides of target's die with copies of new_side.

        This is a helper method equivalent to Java's TestUtils.turnInto.
        Used to set up test scenarios where all sides are the same.
        """
        die = getattr(target, 'die', None)
        if die is None:
            raise ValueError(f"Entity {target} has no die")

        die.set_all_sides(new_side)

    def apply_poison(self, target: Entity, amount: int):
        """Apply poison to target.

        Poison:
        - Creates a Poison trigger that deals damage at end of each turn
        - Multiple poison applications merge into one trigger (values add)
        - Poison persists until cleansed
        """
        from .triggers import Buff, Poison

        self._record_action()
        state = self._states[target]
        state.add_buff(Buff(personal=Poison(amount), turns_remaining=None))

    def apply_cleanse(self, target: Entity, amount: int):
        """Apply cleanse to target.

        Cleanse removes debuffs (like poison) up to the cleanse amount.
        The cleanse budget is tracked per CleanseType via cleansed_map.
        """
        from .triggers import Buff, Cleansed

        self._record_action()
        state = self._states[target]

        # Add cleanse trigger (duration 1 turn)
        state.add_buff(Buff(personal=Cleansed(amount), turns_remaining=1))

        # Iterate existing buffs in reverse and cleanse them
        i = len(state.buffs) - 1
        while i >= 0:
            buff = state.buffs[i]
            fully_cleansed = self._attempt_cleanse_buff(state, buff)
            if fully_cleansed:
                state.buffs.pop(i)
            i -= 1

    def _attempt_cleanse_buff(self, state: EntityState, buff) -> bool:
        """Attempt to cleanse a buff using available cleanse budget.

        Returns True if fully cleansed (should be removed).
        """
        total_cleanse = state.get_total_cleanse_amt()
        if total_cleanse == 0:
            return False

        cleanse_type = buff.get_cleanse_type()
        if cleanse_type is None:
            return False

        already_cleansed = state.cleansed_map.get(cleanse_type, 0)
        remaining = total_cleanse - already_cleansed
        if remaining <= 0:
            return False

        used, fully_cleansed = buff.cleanse_by(remaining)
        state.cleansed_map[cleanse_type] = already_cleansed + used
        return fully_cleansed

    def apply_shield_cleanse(self, target: Entity, amount: int):
        """Apply shield with cleanse keyword.

        shieldCleanse(N) grants N shields AND cleanses N poison stacks.
        This is how cleanse is typically delivered in the game.
        This is a single atomic action (one undo point).
        """
        from .triggers import Buff, Cleansed
        from .effects import EffectType

        self._record_action()

        state = self._states[target]

        # Add incoming shield bonus
        bonus = self._get_incoming_bonus(target, EffectType.SHIELD)
        total_shield = amount + bonus

        # Update shield
        self._states[target] = self._update_state(target, shield=state.shield + total_shield)
        state = self._states[target]

        # Add cleanse trigger (duration 1 turn)
        state.add_buff(Buff(personal=Cleansed(amount), turns_remaining=1))

        # Iterate existing buffs in reverse and cleanse them
        i = len(state.buffs) - 1
        while i >= 0:
            buff = state.buffs[i]
            fully_cleansed = self._attempt_cleanse_buff(state, buff)
            if fully_cleansed:
                state.buffs.pop(i)
            i -= 1
