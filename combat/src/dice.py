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
    DOUBLE_GROWTH = auto() # After use, this side gets +2 pips
    # Anti variants (additional)
    ANTI_DOG = auto()   # x2 if source HP != target HP
    ANTI_PAIR = auto()  # x2 if previous die had different pip value
    # Self-targeting keywords
    SELF_SHIELD = auto()   # Shield myself for N pips
    SELF_HEAL = auto()     # Heal myself for N pips
    # Cost keywords
    PAIN = auto()          # I take N damage (N = pip value)
    DEATH = auto()         # I die after using this side
    EXERT = auto()         # Replace all sides with blanks until end of next turn
    # Value modifier keywords
    TREBLE = auto()        # Other keywords x2 -> x3
    # Group keywords - apply base keyword to all allies
    GROUP_GROWTH = auto()       # All allies' sides gain +1 pip
    GROUP_DECAY = auto()        # All allies' sides lose -1 pip
    GROUP_SINGLE_USE = auto()   # All allies' sides become blank
    GROUP_EXERT = auto()        # All allies become exerted
    GROUP_GROOOOOOWTH = auto()  # All allies' dice get +1 pip on all sides
    # Meta keywords - copy from previous die
    ECHO = auto()        # Copy pips (value) from previous die
    RESONATE = auto()    # Copy effect from previous die, retaining pips and resonate keyword
    # No-effect keywords
    NOTHING = auto()     # This keyword has no effect
    # Conditional bonus keywords
    DEFY = auto()        # +N pips where N = incoming damage to me
    # Targeting restriction keywords
    ELIMINATE = auto()   # Can only target enemies with least HP
    HEAVY = auto()       # Can only target enemies with 5+ HP
    GENEROUS = auto()    # Cannot target myself
    SCARED = auto()      # Target must have N or less HP (N = pips)
    PICKY = auto()       # Target must have exactly N HP (N = pips)
    UNUSABLE = auto()    # Cannot be used manually (cantrip still allowed)
    # Self-targeting keywords
    SELF_PETRIFY = auto()  # Petrify myself
    SELF_REPEL = auto()    # N damage to all enemies attacking me
    # Multi-target effect keywords
    CLEAVE = auto()        # Also hits both sides of the target
    DESCEND = auto()       # Also hits below the target
    REPEL = auto()         # N damage to all enemies attacking the target
    # Usage/cost keywords
    MANACOST = auto()      # Costs N mana to use
    MANDATORY = auto()     # Must be used if possible
    FIERCE = auto()        # Target flees if they have N or less HP after attack
    # Value visibility keywords - modify how pair/chain/echo see pip values
    FAULT = auto()         # Others see -1
    PLUS = auto()          # Others see N+1
    DOUBLED = auto()       # Others see 2*N
    SQUARED = auto()       # Others see N^2
    ONESIE = auto()        # Others see 1
    THREESY = auto()       # Others see 3
    ZEROED = auto()        # Others see 0
    # Pip delta modifiers - modify value based on delta from base
    REV_DIFF = auto()      # Inverted pip delta: adds -2 * (calculated - base)
    DOUB_DIFF = auto()     # Doubled pip delta: adds (calculated - base)
    # Status effect keywords - apply status effects to targets
    POISON = auto()        # Apply N poison to target (damage at end of turn)
    REGEN = auto()         # Apply N regen to target (heal at end of turn)
    CLEANSE = auto()       # Remove N points of negative effects from target
    SELF_POISON = auto()   # Apply N poison to myself
    SELF_REGEN = auto()    # Apply N regen to myself
    SELF_CLEANSE = auto()  # Remove N points of negative effects from myself
    # Status effect conditional bonuses
    PLAGUE = auto()        # +N pips where N = total poison on all characters
    ACIDIC = auto()        # +N pips where N = poison on me
    # Buff system keywords - apply temporary modifiers to targets
    WEAKEN = auto()        # Target gets -N to all pips for one turn
    BOOST = auto()         # Target gets +N to all pips for one turn
    VULNERABLE = auto()    # Target takes +N damage from dice/spells for one turn
    SMITH = auto()         # Target gets +N to damage and shield sides for one turn
    PERMA_BOOST = auto()   # Target gets +N to all pips for the fight
    SELF_VULNERABLE = auto()  # Apply vulnerable to myself
    # Buff-related conditional bonuses
    BUFFED = auto()        # +N pips where N = number of buffs on me
    AFFECTED = auto()      # +N pips where N = number of triggers affecting me
    SKILL = auto()         # +N pips where N = my level/tier
    # Meta keyword - copy to allies
    DUPLICATE = auto()     # Copy this side onto all allied sides for one turn
    # Usage tracking keywords - multiple uses per turn
    DOUBLE_USE = auto()    # Can be used twice per turn
    QUAD_USE = auto()      # Can be used 4 times per turn
    HYPER_USE = auto()     # Can be used N times per turn (N = pip value)
    RITE = auto()          # +1 per unused ally, marks them as used
    # Combined keywords
    TRILL = auto()         # trio + skill: x3 if 2 previous dice match + tier bonus
    # Turn-start processing keywords - modify side each turn
    SHIFTER = auto()       # Add a random extra keyword, changes each turn
    LUCKY = auto()         # Pips randomized to [0, current_pips] each turn
    CRITICAL = auto()      # 50% chance for +1 pip, rechecks each turn
    FLUCTUATE = auto()     # Change to random side type each turn, retaining keywords and pips
    FUMBLE = auto()        # 50% chance to be blank each turn
    # Turn tracking keywords - based on elapsed turns
    PATIENT = auto()       # x2 if I was not used last turn
    ERA = auto()           # +N pips where N = turns elapsed
    MINUS_ERA = auto()     # -N pips where N = turns elapsed
    # Additional effect keywords - apply extra effects to target
    HEAL = auto()          # Also heal target for N pips
    SHIELD = auto()        # Also shield target for N pips
    DAMAGE = auto()        # Also damage target for N pips
    # Target tracking keywords - based on who targeted whom
    DUEL = auto()          # x2 vs enemies who targeted me this turn
    FOCUS = auto()         # x2 if targeting same entity as previous die
    HALVE_DUEL = auto()    # x0.5 vs enemies who targeted me this turn
    # Combined keywords with duel
    DUEGUE = auto()        # duel + plague: x2 vs enemies who targeted me + total poison bonus
    UNDEROCUS = auto()     # underdog + focus: x2 vs higher HP targets who I focused
    # Max HP modification keywords
    VITALITY = auto()      # Grant target +N max HP (as empty HP) this fight
    WITHER = auto()        # Grant target -N max HP this fight
    # Side modification keywords
    HYPNOTISE = auto()     # Set target's DAMAGE sides to 0 for one turn
    # Entity summoning keywords
    BONED = auto()         # Summon 1 Bones
    HYPER_BONED = auto()   # Summon N Bones (N = pip value)
    # Side replacement keywords
    STASIS = auto()        # This side cannot change (blocks all trigger processing)
    ENDURING = auto()      # When replaced, keeps keywords (loses pips/effect)
    DOGMA = auto()         # When replaced, only pips change (keeps keywords/effect type)
    RESILIENT = auto()     # When replaced, keeps pips (loses keywords/effect)
    # Side injection keywords - add keywords to all target's sides
    INFLICT_SELF_SHIELD = auto()  # Add selfShield keyword to all target's sides
    INFLICT_BONED = auto()        # Add boned keyword to all target's sides
    INFLICT_EXERT = auto()        # Add exert keyword to all target's sides
    INFLICT_PAIN = auto()         # Add pain keyword to all target's sides
    INFLICT_DEATH = auto()        # Add death keyword to all target's sides
    INFLICT_SINGLE_USE = auto()   # Add singleUse keyword to all target's sides
    INFLICT_NOTHING = auto()      # Add nothing keyword to all target's sides
    INFLICT_INFLICT_NOTHING = auto()  # Add inflictNothing keyword to all target's sides
    INFLICT_INFLICT_DEATH = auto()    # Add inflictDeath keyword to all target's sides


# Map from inflict keyword to the keyword it inflicts
INFLICT_KEYWORD_MAP: dict["Keyword", "Keyword"] = {}


def _init_inflict_map():
    """Initialize the inflict keyword map after enum is fully defined."""
    global INFLICT_KEYWORD_MAP
    INFLICT_KEYWORD_MAP = {
        Keyword.INFLICT_SELF_SHIELD: Keyword.SELF_SHIELD,
        Keyword.INFLICT_BONED: Keyword.BONED,
        Keyword.INFLICT_EXERT: Keyword.EXERT,
        Keyword.INFLICT_PAIN: Keyword.PAIN,
        Keyword.INFLICT_DEATH: Keyword.DEATH,
        Keyword.INFLICT_SINGLE_USE: Keyword.SINGLE_USE,
        Keyword.INFLICT_NOTHING: Keyword.NOTHING,
        Keyword.INFLICT_INFLICT_NOTHING: Keyword.INFLICT_NOTHING,
        Keyword.INFLICT_INFLICT_DEATH: Keyword.INFLICT_DEATH,
    }


_init_inflict_map()


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

    def get_visible_value(self) -> int:
        """Get the value as seen by other keywords (pair, chain, echo).

        Visibility keywords modify how other keywords see this side's pip value:
        - FAULT: others see -1
        - PLUS: others see N+1
        - DOUBLED: others see 2*N
        - SQUARED: others see N^2
        - ONESIE: others see 1
        - THREESY: others see 3
        - ZEROED: others see 0

        If multiple visibility keywords are present, the last one takes precedence
        based on the order above (ZEROED wins over all).
        """
        base_value = self.calculated_value

        # Check visibility keywords in priority order (lowest to highest priority)
        # Later checks overwrite earlier ones
        visible = base_value

        if Keyword.FAULT in self.keywords:
            visible = -1
        if Keyword.PLUS in self.keywords:
            visible = base_value + 1
        if Keyword.DOUBLED in self.keywords:
            visible = base_value * 2
        if Keyword.SQUARED in self.keywords:
            visible = base_value * base_value
        if Keyword.ONESIE in self.keywords:
            visible = 1
        if Keyword.THREESY in self.keywords:
            visible = 3
        if Keyword.ZEROED in self.keywords:
            visible = 0

        return visible

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


def single_use_blank() -> Side:
    """Create a blank side for singleUse - no effect, not petrified."""
    return Side(
        effect_type=EffectType.BLANK,
        value=0,
        keywords=set(),
        growth_bonus=0,
        is_petrified=False
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
