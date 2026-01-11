"""FightLog - central combat state manager."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional, TYPE_CHECKING
from copy import deepcopy

from .entity import Entity, Team, FIELD_CAPACITY
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

    def get_side_state(self, index: int) -> "SideState":
        """Get the calculated state for a side, after applying all buffs.

        If the side is petrified, returns a blank petrified side.
        Otherwise returns the original side from the entity's die.
        """
        from .dice import Side, petrified_blank

        # Check if entity has a die
        die = getattr(self.entity, 'die', None)
        if die is None:
            raise ValueError(f"Entity {self.entity} has no die")

        # Check if this side is petrified
        if index in self.petrified_sides:
            return SideState(petrified_blank(), index, is_petrified=True)

        # Return original side
        original_side = die.get_side(index)
        return SideState(original_side, index, is_petrified=False)

    def get_total_petrification(self) -> int:
        """Count the number of petrified sides.

        Only counts actual petrified sides (not None overflow markers).
        """
        return sum(1 for s in self.petrified_sides if s is not None)


@dataclass
class SideState:
    """Calculated state of a die side after applying all buffs."""
    side: "Side"  # The calculated side (may be petrified blank)
    index: int    # Original side index
    is_petrified: bool = False  # True if this side was petrified

    @property
    def effect_type(self) -> EffectType:
        """Get the effect type of this side."""
        return self.side.effect_type

    @property
    def value(self) -> int:
        """Get the calculated value of this side."""
        return self.side.calculated_value


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
                state = self._states[remaining]
                self._states[remaining] = EntityState(
                    remaining, state.hp, state.max_hp,
                    state.shield, state.spiky, state.self_heal, state.damage_blocked,
                    state.keep_shields, state.stone_hp, fled=True
                )

    def _snapshot_states(self) -> dict[Entity, EntityState]:
        """Deep copy current states."""
        return {e: EntityState(e, s.hp, s.max_hp, s.shield, s.spiky, s.self_heal, s.damage_blocked, s.keep_shields, s.stone_hp, s.fled, s.dodge, s.regen, list(s.petrified_sides), s.used_die, s.times_used_this_turn) for e, s in self._states.items()}

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

        return EntityState(entity, future_hp, base.max_hp, base.shield, base.spiky, base.self_heal, base.damage_blocked, base.keep_shields, base.stone_hp, base.fled, base.dodge, base.regen)

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
            self._states[target] = EntityState(
                target, new_hp, state.max_hp,
                new_shield, state.spiky, state.self_heal, state.damage_blocked + blocked,
                state.keep_shields, state.stone_hp, state.fled, state.dodge
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
                self._states[t] = EntityState(
                    t, state.hp - actual_damage, state.max_hp,
                    new_shield, state.spiky, state.self_heal, state.damage_blocked + blocked
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
        self._states[source] = EntityState(
            source, new_hp, source_state.max_hp,
            source_state.shield, source_state.spiky, source_state.self_heal, source_state.damage_blocked
        )

        # Damage to target (also immediate for this test's behavior)
        target_state = self._states[target]
        self._states[target] = EntityState(
            target, target_state.hp - damage, target_state.max_hp,
            target_state.shield, target_state.spiky, target_state.self_heal, target_state.damage_blocked
        )

    def modify_max_hp(self, target: Entity, amount: int):
        """Modify an entity's max HP by amount (can be positive or negative).

        Max HP changes are cumulative during combat.
        Max HP has a floor of 1 - it can never go to 0 or below.
        """
        self._record_action()

        state = self._states[target]
        new_max_hp = max(1, state.max_hp + amount)  # Floor at 1
        self._states[target] = EntityState(
            target, state.hp, new_max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked
        )

    def apply_buff_spiky(self, target: Entity, amount: int):
        """Apply Spiky buff to target. Spiky deals damage back to attackers."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky + amount, state.self_heal, state.damage_blocked
        )

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
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield + total_shield, state.spiky, state.self_heal, state.damage_blocked
        )

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
        self._states[target] = EntityState(
            target, new_hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked
        )

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
        self._states[target] = EntityState(
            target, new_hp, state.max_hp,
            state.shield + total_shield, state.spiky, state.self_heal, state.damage_blocked
        )

        # Check for rescue and fire triggers
        self._check_and_fire_rescue(target, was_dying)

    def apply_buff_self_heal(self, target: Entity):
        """Apply selfHeal buff to target. SelfHeal negates pain damage."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, True, state.damage_blocked
        )

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
                self._states[pending.source] = EntityState(
                    pending.source, new_source_hp, source_state.max_hp,
                    source_state.shield, source_state.spiky, source_state.self_heal, source_state.damage_blocked
                )

                # If source has Spiky, it triggers and damages user
                # But user's shield absorbs it
                if source_state.spiky > 0:
                    spiky_damage = source_state.spiky
                    absorbed = min(remaining_shield, spiky_damage)
                    remaining_shield -= absorbed
                    actual_spiky_damage = spiky_damage - absorbed
                    if actual_spiky_damage > 0:
                        user_state = self._states[user]
                        self._states[user] = EntityState(
                            user, user_state.hp - actual_spiky_damage, user_state.max_hp,
                            user_state.shield, user_state.spiky, user_state.self_heal, user_state.damage_blocked
                        )

                # Reduce pending damage by repel amount
                remaining_damage = pending.amount - reflect_amount
                if remaining_damage > 0:
                    new_pending.append(PendingDamage(pending.target, remaining_damage, pending.source))
            else:
                new_pending.append(pending)

        self._pending = new_pending

        # Apply remaining shield to user's state
        user_state = self._states[user]
        self._states[user] = EntityState(
            user, user_state.hp, user_state.max_hp,
            user_state.shield + remaining_shield, user_state.spiky, user_state.self_heal, user_state.damage_blocked
        )

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
                self._states[target] = EntityState(
                    target, state.hp - actual_damage, state.max_hp,
                    new_shield, state.spiky, state.self_heal, state.damage_blocked + blocked
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
            self._states[user] = EntityState(
                user, state.hp, state.max_hp,
                state.shield + self_shield, state.spiky, state.self_heal, state.damage_blocked,
                state.keep_shields
            )

    def apply_keep_shields(self, target: Entity):
        """Apply KeepShields buff - shields persist across turns."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            keep_shields=True, stone_hp=state.stone_hp
        )

    def apply_stone_hp(self, target: Entity, amount: int):
        """Apply Stone HP buff - caps all incoming damage to 1 per hit.

        Stone HP represents hardened HP pips that can only take 1 damage at a time.
        Amount is the number of stone HP pips (affects how much total damage can be absorbed).
        """
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, stone_hp=amount
        )

    def next_turn(self):
        """Advance to next turn - clears shields (unless keep_shields), pending damage, etc.

        Turn transition:
        - Regen healing is applied (capped at max HP)
        - Shields are cleared unless entity has keep_shields buff
        - Pending damage is cleared (already resolved)
        - Other turn-based buffs may be cleared (spiky, etc.)
        """
        self._record_action()

        # Clear pending damage
        self._pending = []

        # Process each entity's turn transition
        for entity, state in list(self._states.items()):
            new_shield = state.shield if state.keep_shields else 0

            # Apply regen healing (capped at max HP)
            new_hp = min(state.hp + state.regen, state.max_hp)

            self._states[entity] = EntityState(
                entity, new_hp, state.max_hp,
                new_shield, state.spiky, state.self_heal, 0,  # Reset damage_blocked
                state.keep_shields, state.stone_hp, state.fled, state.dodge, state.regen
            )

    def apply_kill(self, target: Entity):
        """Instantly kill an entity (set HP to 0)."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, 0, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp
        )

    def apply_resurrect(self, amount: int):
        """Resurrect up to N dead heroes with full HP.

        Resurrects dead heroes in order (by position).
        Capped at number of dead heroes.
        Resurrected heroes come back with full HP.
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
            self._states[hero] = EntityState(
                hero, state.max_hp, state.max_hp,  # Full HP
                0, 0, False, 0,  # Reset buffs
                False, 0  # Reset keep_shields and stone_hp
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

        self._states[target] = EntityState(
            target, new_hp, new_max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp
        )

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
        self._states[target] = EntityState(
            target, new_hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled,
            state.dodge, state.regen
        )

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
        self._states[target] = EntityState(
            target, new_hp, state.max_hp,
            new_shield, state.spiky, state.self_heal, state.damage_blocked + blocked,
            state.keep_shields, state.stone_hp, state.fled
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
        self._states[source] = EntityState(
            source, new_hp, source_state.max_hp,
            source_state.shield, source_state.spiky, source_state.self_heal,
            source_state.damage_blocked, source_state.keep_shields,
            source_state.stone_hp, source_state.fled, source_state.dodge
        )

    def apply_dodge(self, target: Entity):
        """Apply Dodge buff to target - makes them invincible (immune to damage).

        While dodge is active, the entity takes no damage from any source.
        """
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, dodge=True, regen=state.regen
        )

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

        self._states[target] = EntityState(
            target, new_hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, state.dodge, new_regen
        )

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
        The side's calculated_value is used (includes growth bonus from previous uses).
        Marks the die as used after applying the effect.

        For shieldMana effects (SHIELD type with MANA keyword):
        - Grants shield equal to calculated_value
        - Grants mana equal to calculated_value
        - If has GROWTH keyword, increases side's value by 1 after use
        """
        from .dice import Keyword

        # Get the entity's die (must be set up before calling this)
        die = getattr(entity, 'die', None)
        if die is None:
            raise ValueError(f"Entity {entity} has no die")

        side = die.get_side(side_index)
        value = side.calculated_value

        # Apply the effect based on type
        if side.effect_type == EffectType.SHIELD:
            self.apply_shield(target, value)

            # If has MANA keyword, also grant mana
            if side.has_keyword(Keyword.MANA):
                self.add_mana(value)

        elif side.effect_type == EffectType.DAMAGE:
            self.apply_damage(entity, target, value)

        elif side.effect_type == EffectType.HEAL:
            self.apply_heal(target, value)

        # Mark die as used
        self.mark_die_used(entity)

        # Apply growth AFTER use
        if side.has_keyword(Keyword.GROWTH):
            side.apply_growth()

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

        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, state.dodge, state.regen,
            new_petrified
        )

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

        self._states[target] = EntityState(
            target, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, state.dodge, state.regen,
            petrified
        )

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

        self._states[entity] = EntityState(
            entity, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, state.dodge, state.regen,
            list(state.petrified_sides), is_used, times
        )

    def recharge_die(self, entity: Entity):
        """Recharge entity's die - allows it to be used again.

        Called by RESCUE keyword (when healing saves a dying hero)
        and RAMPAGE keyword (when attack kills an enemy).
        """
        self._record_action()
        state = self._states[entity]

        self._states[entity] = EntityState(
            entity, state.hp, state.max_hp,
            state.shield, state.spiky, state.self_heal, state.damage_blocked,
            state.keep_shields, state.stone_hp, state.fled, state.dodge, state.regen,
            list(state.petrified_sides), False, 0  # Reset used_die and times_used
        )

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
