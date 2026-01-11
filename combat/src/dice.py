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
    MOXIE = auto()       # x2 effect if I have least HP of all
    BULLY = auto()       # x2 effect if I have most HP of all
    REBORN = auto()      # x2 effect if I died this fight
    WHAM = auto()        # x2 effect vs targets with shields
    SQUISH = auto()      # x2 effect vs targets with least HP of all
    UPPERCUT = auto()    # x2 effect vs targets with most HP of all
    TERMINAL = auto()    # x2 effect vs targets on exactly 1 HP
    EGO = auto()         # x2 effect if targeting myself
    CENTURY = auto()     # x2 effect vs targets with 100+ HP
    SERRATED = auto()    # x2 effect vs targets who gained no shields this turn
    UNDERDOG = auto()    # x2 effect vs targets with more HP than me
    OVERDOG = auto()     # x2 effect vs targets with less HP than me
    DOG = auto()         # x2 effect vs targets with equal HP to me
    HYENA = auto()       # x2 effect vs targets whose HP is coprime with mine
    TALL = auto()        # x2 effect vs topmost target
    CHAIN = auto()       # x2 effect if previous die shares a keyword
    INSPIRED = auto()    # x2 effect if previous die had more pips
    BLOODLUST = auto()   # +N pips where N = damaged enemies
    CHARGED = auto()     # +N pips where N = current mana
    STEEL = auto()       # +N pips where N = my shields
    FLESH = auto()       # +N pips where N = my current HP
    RAINBOW = auto()     # +N pips where N = number of keywords on this side
    FLURRY = auto()      # +N pips where N = times I've been used this turn
    VIGIL = auto()       # +N pips where N = defeated allies
    GUILT = auto()       # If this is lethal, I die
    EVIL = auto()        # If this saves a hero, I die
    # Variant keywords - anti* (inverted condition)
    ANTI_ENGAGE = auto()     # x2 if target NOT at full HP
    ANTI_PRISTINE = auto()   # x2 if source NOT at full HP
    ANTI_DEATHWISH = auto()  # x2 if source NOT at 1HP
    # Variant keywords - swap* (swap source/target)
    SWAP_ENGAGE = auto()     # x2 if SOURCE at full HP
    SWAP_CRUEL = auto()      # x2 if SOURCE at or below half HP
    SWAP_DEATHWISH = auto()  # x2 if TARGET at 1HP
    SWAP_TERMINAL = auto()   # x2 if TARGET at exactly 1HP (same as swapDeathwish, see Java)
    # Variant keywords - halve* (x0.5 instead of x2)
    HALVE_ENGAGE = auto()    # x0.5 if target at full HP
    HALVE_DEATHWISH = auto() # x0.5 if source at 1HP
    # Pair family
    TRIO = auto()   # x3 if previous 2 dice had same calculated value
    QUIN = auto()   # x5 if previous 4 dice had same calculated value
    SEPT = auto()   # x7 if previous 6 dice had same calculated value
    # Combined keywords - TC4X (both conditions = x4)
    ENGINE = auto()   # engage + pristine: x4 if target full HP AND source full HP
    PRISWISH = auto() # pristine + deathwish: x4 if source full HP AND source at 1HP
    # Combined keywords - XOR
    PAXIN = auto()    # pair XOR chain: x2 if exactly one condition met
    # Combined keywords - ConditionBonus (condition + pip bonus)
    ENGARGED = auto()   # engage + charged: x2 if target full HP, +N mana pips
    CRUESH = auto()     # cruel + flesh: x2 if target at or below half HP, +N HP pips
    PRISTEEL = auto()   # pristine + steel: x2 if source full HP, +N shield pips
    DEATHLUST = auto()  # deathwish + bloodlust: x2 if source at 1HP, +N damaged enemy pips
    # Minus variants
    MINUS_FLESH = auto()  # -N pips where N = my current HP
    # Dice count conditionals
    FIRST = auto()    # x2 if no dice used this turn
    SIXTH = auto()    # x2 if this is the 6th die used this turn
    FIZZ = auto()     # +N pips where N = abilities used this turn
    # Consecutive value conditionals
    STEP = auto()     # x2 if previous 2 dice form a consecutive run
    RUN = auto()      # x2 if previous 3 dice form a consecutive run
    SPRINT = auto()   # x2 if previous 5 dice form a consecutive run
    # Blank side comparison
    SLOTH = auto()    # x2 if I have more blank sides than target
    # Growth variants
    HYPER_GROWTH = auto()  # After use, this side gets +N pips (N = value)
    UNDERGROWTH = auto()   # After use, the opposite side gets +1 pip
    GROOOOOOWTH = auto()   # After use, all my sides get +1 pip
    DECAY = auto()         # After use, this side gets -1 pip


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

    def apply_growth_n(self, n: int):
        """Apply growth of N pips (can be negative for decay)."""
        self.growth_bonus += n


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

    def set_side(self, index: int, side: Side):
        """Set a specific side by index."""
        self.sides[index] = side

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
