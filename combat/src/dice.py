"""Dice and side classes for the combat system."""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional

from .effects import EffectType


class Keyword(Enum):
    """Keywords that modify side effects."""
    GROWTH = auto()      # After use, side value increases by +1
    MANA = auto()        # Effect also grants mana equal to value
    PETRIFY = auto()     # Turns target's sides to stone (Blank)
    RESCUE = auto()      # Die recharged if heal saves a dying hero
    RAMPAGE = auto()     # Die recharged if attack kills an enemy
    ENGAGE = auto()      # x2 effect vs full HP targets
    RANGED = auto()      # Arrow-type attacks (can hit back row)
    SINGLE_USE = auto()  # One use per turn (wands)
    COPYCAT = auto()     # Meta-keyword: copies keywords from most recently used die
    CRUEL = auto()       # x2 effect vs targets at half HP or less
    PAIR = auto()        # x2 effect if previous die had same calculated value
    PRISTINE = auto()    # x2 effect if I have full HP
    DEATHWISH = auto()   # x2 effect if I am dying this turn
    ARMOURED = auto()    # x2 effect if I have shields


# Order in which sides get petrified: Top, Left, Middle, Right, Rightmost, Bottom
# Maps to side indices: 0, 2, 4, 3, 5, 1
PETRIFY_ORDER = [0, 2, 4, 3, 5, 1]


@dataclass
class Side:
    """A face of a die with an effect type, value, and keywords."""
    effect_type: EffectType
    value: int
    keywords: set[Keyword] = field(default_factory=set)
    growth_bonus: int = 0  # Accumulated bonus from growth keyword
    is_petrified: bool = False  # True if this side was petrified (for texture tracking)

    @property
    def calculated_value(self) -> int:
        """Get the final value after all bonuses."""
        return self.value + self.growth_bonus

    def with_bonus(self, bonus: int) -> "Side":
        """Return a new side with the bonus added to the value."""
        return Side(self.effect_type, self.value + bonus, set(self.keywords), self.growth_bonus, self.is_petrified)

    def copy(self) -> "Side":
        """Create a copy of this side."""
        return Side(
            effect_type=self.effect_type,
            value=self.value,
            keywords=set(self.keywords),
            growth_bonus=self.growth_bonus,
            is_petrified=self.is_petrified
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


def petrified_blank() -> Side:
    """Create a petrified blank side - no effect, marked as petrified."""
    return Side(
        effect_type=EffectType.BLANK,
        value=0,
        keywords=set(),
        growth_bonus=0,
        is_petrified=True
    )


def heal_rescue(value: int) -> Side:
    """Create a healRescue side: heals with RESCUE keyword.

    If this heal saves a dying hero (was going to die, now survives),
    the die is recharged and can be used again.
    """
    return Side(
        effect_type=EffectType.HEAL,
        value=value,
        keywords={Keyword.RESCUE}
    )


def damage_rampage(value: int) -> Side:
    """Create a damage side with RAMPAGE keyword (like burningFlail).

    If this attack kills an enemy, the die is recharged and can be used again.
    """
    return Side(
        effect_type=EffectType.DAMAGE,
        value=value,
        keywords={Keyword.RAMPAGE}
    )


def arrow(value: int) -> Side:
    """Create an arrow (ranged damage) side.

    Arrow sides have the RANGED keyword which allows hitting back-row targets.
    """
    return Side(
        effect_type=EffectType.DAMAGE,
        value=value,
        keywords={Keyword.RANGED}
    )


def wand_self_heal(value: int) -> Side:
    """Create a wand side with SINGLE_USE keyword.

    Wand sides heal the user but can only be used once per turn.
    """
    return Side(
        effect_type=EffectType.HEAL,
        value=value,
        keywords={Keyword.SINGLE_USE}
    )


def mana_pair(value: int) -> Side:
    """Create a mana side with PAIR keyword.

    Grants mana equal to value. If previous die had the same calculated value,
    the effect is doubled (x2).

    Behavior:
    - First use: grants N mana
    - Second consecutive use (same value): grants 2N mana
    - After pairing, the final value becomes 2N, so next die needs 2N to pair
    """
    return Side(
        effect_type=EffectType.MANA,
        value=value,
        keywords={Keyword.MANA, Keyword.PAIR}
    )
