"""Entity classes for heroes and monsters."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional


class Team(Enum):
    HERO = auto()
    MONSTER = auto()


@dataclass
class EntityType:
    """Template for an entity (e.g., Fighter, Goblin)."""
    name: str
    hp: int


@dataclass
class Entity:
    """A combatant in a fight."""
    entity_type: EntityType
    team: Team
    position: int = 0

    # Runtime state managed by FightLog, not here
    def __hash__(self):
        return id(self)


# Common entity types
FIGHTER = EntityType("Fighter", 6)
GOBLIN = EntityType("Goblin", 3)
