"""Dice and side classes for the combat system."""

from dataclasses import dataclass, field
from typing import Optional

from .effects import EffectType


@dataclass
class Side:
    """A face of a die with an effect type and value."""
    effect_type: EffectType
    value: int

    def with_bonus(self, bonus: int) -> "Side":
        """Return a new side with the bonus added to the value."""
        return Side(self.effect_type, self.value + bonus)


@dataclass
class Die:
    """A 6-sided die with effect sides."""
    sides: list[Side] = field(default_factory=list)

    def get_side(self, index: int) -> Side:
        """Get the side at the given index."""
        return self.sides[index]

    def __len__(self) -> int:
        return len(self.sides)


def create_fighter_die() -> Die:
    """Create the default Fighter die.

    Fighter has damage on most sides.
    Side 0 is Sword (damage 1).
    """
    return Die([
        Side(EffectType.DAMAGE, 1),  # Side 0: Sword 1
        Side(EffectType.DAMAGE, 1),  # Side 1: Sword 1
        Side(EffectType.DAMAGE, 2),  # Side 2: Sword 2
        Side(EffectType.DAMAGE, 2),  # Side 3: Sword 2
        Side(EffectType.SHIELD, 1),  # Side 4: Shield 1
        Side(EffectType.DAMAGE, 3),  # Side 5: Sword 3
    ])
