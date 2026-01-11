"""Item system with side modifiers."""

from dataclasses import dataclass, field
from typing import Optional, Protocol
from abc import ABC, abstractmethod

from .effects import EffectType
from .dice import Side


class SideModifier(ABC):
    """Base class for item modifiers that affect die sides."""

    @abstractmethod
    def applies_to(self, side: Side) -> bool:
        """Check if this modifier applies to the given side."""
        pass

    @abstractmethod
    def modify(self, side: Side) -> Side:
        """Return a modified copy of the side."""
        pass


@dataclass
class FlatBonus(SideModifier):
    """Adds a flat bonus to matching sides."""
    effect_type: EffectType
    bonus: int

    def applies_to(self, side: Side) -> bool:
        return side.effect_type == self.effect_type

    def modify(self, side: Side) -> Side:
        if self.applies_to(side):
            return side.with_bonus(self.bonus)
        return side


@dataclass
class Item:
    """An item that can be equipped to a hero."""
    name: str
    modifiers: list[SideModifier] = field(default_factory=list)

    def modify_side(self, side: Side) -> Side:
        """Apply all modifiers to a side and return the modified version."""
        result = side
        for modifier in self.modifiers:
            result = modifier.modify(result)
        return result


# Item library
GAUNTLET = Item("Gauntlet", [FlatBonus(EffectType.DAMAGE, 1)])


def item_by_name(name: str) -> Item:
    """Get an item by name."""
    items = {
        "Gauntlet": GAUNTLET,
    }
    return items[name]
