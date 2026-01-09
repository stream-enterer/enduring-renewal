"""FightLog - central combat state manager."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional
from copy import deepcopy

from .entity import Entity, Team


class Temporality(Enum):
    PRESENT = auto()  # Current confirmed state
    FUTURE = auto()   # State after pending effects resolve


@dataclass
class EntityState:
    """Snapshot of an entity's state at a point in time."""
    entity: Entity
    hp: int
    max_hp: int
    shield: int = 0  # Temporary damage block
    spiky: int = 0   # Damage dealt back to attackers

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
        self.monsters = monsters

        # Current HP state for all entities
        self._states: dict[Entity, EntityState] = {}
        for i, h in enumerate(heroes):
            h.position = i
            self._states[h] = EntityState(h, h.entity_type.hp, h.entity_type.hp, 0, 0)
        for i, m in enumerate(monsters):
            m.position = i
            self._states[m] = EntityState(m, m.entity_type.hp, m.entity_type.hp, 0, 0)

        # Pending damage (applies in future, cancelled if source dies)
        self._pending: list[PendingDamage] = []

        # Undo stack
        self._history: list[Action] = []

    def _snapshot_states(self) -> dict[Entity, EntityState]:
        """Deep copy current states."""
        return {e: EntityState(e, s.hp, s.max_hp, s.shield, s.spiky) for e, s in self._states.items()}

    def _record_action(self):
        """Record state before an action for undo."""
        self._history.append(Action(
            states_before=self._snapshot_states(),
            pending_before=list(self._pending)
        ))

    def get_state(self, entity: Entity, temporality: Temporality) -> EntityState:
        """Get entity state at given temporality."""
        base = self._states[entity]

        if temporality == Temporality.PRESENT:
            return base

        # FUTURE: apply pending damage
        future_hp = base.hp
        for pending in self._pending:
            if pending.target == entity:
                # Only apply if source is alive
                source_state = self._states[pending.source]
                if not source_state.is_dead:
                    future_hp -= pending.amount

        return EntityState(entity, future_hp, base.max_hp, base.shield, base.spiky)

    def apply_damage(self, source: Entity, target: Entity, amount: int, is_pending: bool = False):
        """Apply damage to target. If is_pending, damage goes to future state."""
        self._record_action()

        if is_pending:
            self._pending.append(PendingDamage(target, amount, source))
        else:
            state = self._states[target]
            self._states[target] = EntityState(target, state.hp - amount, state.max_hp, state.shield, state.spiky)

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
                self._states[t] = EntityState(t, state.hp - amount, state.max_hp, state.shield, state.spiky)

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

    def apply_pain_damage(self, source: Entity, target: Entity, damage: int, pain: int):
        """Apply damage to target with pain (self-damage) to source.

        Pain is immediate self-damage to the attacker. Can kill the attacker.
        The damage to target and pain to source happen in the same action.
        """
        self._record_action()

        # Pain: immediate self-damage to attacker
        source_state = self._states[source]
        self._states[source] = EntityState(source, source_state.hp - pain, source_state.max_hp, source_state.shield, source_state.spiky)

        # Damage to target (also immediate for this test's behavior)
        target_state = self._states[target]
        self._states[target] = EntityState(target, target_state.hp - damage, target_state.max_hp, target_state.shield, target_state.spiky)

    def modify_max_hp(self, target: Entity, amount: int):
        """Modify an entity's max HP by amount (can be positive or negative).

        Max HP changes are cumulative during combat.
        Max HP has a floor of 1 - it can never go to 0 or below.
        """
        self._record_action()

        state = self._states[target]
        new_max_hp = max(1, state.max_hp + amount)  # Floor at 1
        self._states[target] = EntityState(target, state.hp, new_max_hp, state.shield, state.spiky)

    def apply_buff_spiky(self, target: Entity, amount: int):
        """Apply Spiky buff to target. Spiky deals damage back to attackers."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp, state.shield, state.spiky + amount
        )

    def apply_shield(self, target: Entity, amount: int):
        """Apply shield to target. Shield blocks incoming damage."""
        self._record_action()
        state = self._states[target]
        self._states[target] = EntityState(
            target, state.hp, state.max_hp, state.shield + amount, state.spiky
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
                    source_state.shield, source_state.spiky
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
                            user_state.shield, user_state.spiky
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
            user_state.shield + remaining_shield, user_state.spiky
        )
