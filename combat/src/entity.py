"""Entity classes for heroes and monsters."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from .triggers import Personal


class Team(Enum):
    HERO = auto()
    MONSTER = auto()


class EntitySize(Enum):
    """Field size units for entities. Field capacity is 165 units."""
    TINY = 16
    HERO = 24
    BIG = 30
    HUGE = 64


# Field capacity in units
FIELD_CAPACITY = 165


@dataclass
class EntityType:
    """Template for an entity (e.g., Fighter, Goblin)."""
    name: str
    hp: int
    size: EntitySize = EntitySize.HERO
    flees_on_ally_death: bool = False  # Goblins flee when an ally dies


@dataclass
class Entity:
    """A combatant in a fight."""
    entity_type: EntityType
    team: Team
    position: int = 0
    traits: list["Personal"] = field(default_factory=list)  # Traits are permanent Personal triggers

    # Runtime state managed by FightLog, not here
    def __hash__(self):
        return id(self)


# Common entity types - heroes are always HERO sized
FIGHTER = EntityType("Fighter", 5, EntitySize.HERO)
HEALER = EntityType("Healer", 6, EntitySize.HERO)

# Monsters with various sizes
GOBLIN = EntityType("Goblin", 3, EntitySize.HERO)  # Hero-sized
TEST_GOBLIN = EntityType("testGoblin", 3, EntitySize.HERO)  # For tests
FLEEING_GOBLIN = EntityType("goblin", 3, EntitySize.HERO, flees_on_ally_death=True)  # Flees when alone
DRAGON = EntityType("Dragon", 20, EntitySize.HUGE)
BONES = EntityType("Bones", 4, EntitySize.TINY)  # Summoned skeleton - 4 HP, tiny size
