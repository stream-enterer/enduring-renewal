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
            self._states[h] = EntityState(h, h.entity_type.hp, h.entity_type.hp)
        for i, m in enumerate(monsters):
            m.position = i
            self._states[m] = EntityState(m, m.entity_type.hp, m.entity_type.hp)

        # Pending damage (applies in future, cancelled if source dies)
        self._pending: list[PendingDamage] = []

        # Undo stack
        self._history: list[Action] = []

    def _snapshot_states(self) -> dict[Entity, EntityState]:
        """Deep copy current states."""
        return {e: EntityState(e, s.hp, s.max_hp) for e, s in self._states.items()}

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

        return EntityState(entity, future_hp, base.max_hp)

    def apply_damage(self, source: Entity, target: Entity, amount: int, is_pending: bool = False):
        """Apply damage to target. If is_pending, damage goes to future state."""
        self._record_action()

        if is_pending:
            self._pending.append(PendingDamage(target, amount, source))
        else:
            state = self._states[target]
            self._states[target] = EntityState(target, state.hp - amount, state.max_hp)

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
                self._states[t] = EntityState(t, state.hp - amount, state.max_hp)

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
