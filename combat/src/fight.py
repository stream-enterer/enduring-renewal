"""FightLog - central combat state manager."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional, TYPE_CHECKING
from copy import deepcopy

from .entity import Entity, Team, FIELD_CAPACITY

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

    @property
    def is_dead(self) -> bool:
        return self.hp <= 0


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

    def _snapshot_states(self) -> dict[Entity, EntityState]:
        """Deep copy current states."""
        return {e: EntityState(e, s.hp, s.max_hp, s.shield, s.spiky, s.self_heal, s.damage_blocked, s.keep_shields) for e, s in self._states.items()}

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

        return EntityState(entity, future_hp, base.max_hp, base.shield, base.spiky, base.self_heal, base.damage_blocked, base.keep_shields)

    def apply_damage(self, source: Entity, target: Entity, amount: int, is_pending: bool = False):
        """Apply damage to target. If is_pending, damage goes to future state.

        Immediate damage is reduced by shield. Blocked amount is tracked.
        """
        self._record_action()

        if is_pending:
            self._pending.append(PendingDamage(target, amount, source))
        else:
            state = self._states[target]
            # Shield blocks damage
            blocked = min(state.shield, amount)
            actual_damage = amount - blocked
            new_shield = state.shield - blocked
            self._states[target] = EntityState(
                target, state.hp - actual_damage, state.max_hp,
                new_shield, state.spiky, state.self_heal, state.damage_blocked + blocked
            )
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
        """Get monsters currently on the field (not dead, not reinforcement).

        This differs from get_alive_monsters in that it only counts monsters
        that have been spawned onto the field, not those waiting as reinforcements.
        """
        present = []
        for monster in self.monsters:
            state = self.get_state(monster, temporality)
            if not state.is_dead:
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
            keep_shields=True
        )

    def next_turn(self):
        """Advance to next turn - clears shields (unless keep_shields), pending damage, etc.

        Turn transition:
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
            self._states[entity] = EntityState(
                entity, state.hp, state.max_hp,
                new_shield, state.spiky, state.self_heal, 0,  # Reset damage_blocked
                state.keep_shields
            )
