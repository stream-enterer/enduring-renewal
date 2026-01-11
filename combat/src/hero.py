"""Hero class with die and items."""

from dataclasses import dataclass, field
from typing import Optional

from .entity import Entity, EntityType, Team, EntitySize
from .dice import Die, Side, create_fighter_die
from .item import Item, TriggerType, ItemTrigger
from .effects import EffectType


@dataclass
class HeroType:
    """Template for a hero type with die configuration."""
    name: str
    hp: int
    die_factory: callable  # Function that returns a Die


# Hero types
FIGHTER_TYPE = HeroType("Fighter", 5, create_fighter_die)


class Hero:
    """A hero entity with die and items."""

    def __init__(self, hero_type: HeroType, position: int = 0):
        self.hero_type = hero_type
        self.entity = Entity(
            EntityType(hero_type.name, hero_type.hp, EntitySize.HERO),
            Team.HERO,
            position
        )
        self._die = hero_type.die_factory()
        self._items: list[Item] = []

    @property
    def position(self) -> int:
        return self.entity.position

    @position.setter
    def position(self, value: int):
        self.entity.position = value

    def get_die(self) -> Die:
        """Get the hero's die."""
        return self._die

    def add_item(self, item: Item):
        """Add an item to the hero."""
        self._items.append(item)

    def get_items(self) -> list[Item]:
        """Get all items on this hero."""
        return self._items

    def get_effective_side(self, index: int) -> Side:
        """Get a side with all item modifiers applied."""
        side = self._die.get_side(index)
        for item in self._items:
            side = item.modify_side(side)
        return side

    def get_triggers(self, trigger_type: TriggerType) -> list[ItemTrigger]:
        """Get all triggers of a specific type from all items."""
        triggers = []
        for item in self._items:
            triggers.extend(item.get_triggers(trigger_type))
        return triggers

    def get_incoming_bonus(self, effect_type: EffectType) -> int:
        """Get total incoming bonus for an effect type from all items."""
        total = 0
        for item in self._items:
            total += item.get_incoming_bonus(effect_type)
        return total

    def __hash__(self):
        return hash(self.entity)

    def __eq__(self, other):
        if isinstance(other, Hero):
            return self.entity == other.entity
        return False
