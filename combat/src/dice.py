"""Dice and side classes for the combat system."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional

from .effects import EffectType


class Keyword(Enum):
    """Keywords that modify side effects."""
    GROWTH = auto()      # After use, side value increases by +1
    MANA = auto()        # Effect also grants mana equal to value


@dataclass
class Side:
    """A face of a die with an effect type, value, and keywords."""
    effect_type: EffectType
    value: int
    keywords: set[Keyword] = field(default_factory=set)
    growth_bonus: int = 0  # Accumulated bonus from growth keyword

    @property
    def calculated_value(self) -> int:
        """Get the final value after all bonuses."""
        return self.value + self.growth_bonus

    def with_bonus(self, bonus: int) -> "Side":
        """Return a new side with the bonus added to the value."""
        return Side(self.effect_type, self.value + bonus, set(self.keywords), self.growth_bonus)

    def copy(self) -> "Side":
        """Create a copy of this side."""
        return Side(
            effect_type=self.effect_type,
            value=self.value,
            keywords=set(self.keywords),
            growth_bonus=self.growth_bonus
        )

    def has_keyword(self, keyword: Keyword) -> bool:
        """Check if this side has a keyword."""
        return keyword in self.keywords

    def apply_growth(self):
        """Apply growth: increase the side's value by 1."""
        self.growth_bonus += 1


@dataclass
class Die:
    """A 6-sided die with effect sides."""
    sides: list[Side] = field(default_factory=list)
    locked_side: int = 0  # Which side is currently "up"
    is_used: bool = False  # Has this die been used this turn?

    def get_side(self, index: int) -> Side:
        """Get the side at the given index."""
        return self.sides[index]

    def get_current_side(self) -> Side:
        """Get the currently locked side."""
        return self.sides[self.locked_side]

    def __len__(self) -> int:
        return len(self.sides)

    def set_all_sides(self, side: Side):
        """Replace all sides with copies of the given side."""
        self.sides = [side.copy() for _ in range(6)]

    def add_keyword_to_all(self, keyword: Keyword):
        """Add a keyword to all sides."""
        for side in self.sides:
            side.keywords.add(keyword)

    def copy(self) -> "Die":
        """Create a deep copy of this die."""
        return Die(
            sides=[s.copy() for s in self.sides],
            locked_side=self.locked_side,
            is_used=self.is_used
        )


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


# Helper to create common effect types
def shield_mana(value: int) -> Side:
    """Create a shieldMana side: grants shield AND mana equal to value."""
    return Side(
        effect_type=EffectType.SHIELD,
        value=value,
        keywords={Keyword.MANA}
    )
