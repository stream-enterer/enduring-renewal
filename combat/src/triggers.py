"""Trigger system for modifying die sides based on conditions and effects.

The trigger system allows buffs, items, and traits to modify die sides dynamically.
Each trigger (Personal) can have conditions that determine which sides it affects,
and effects that determine how those sides are modified.

Key classes:
- Personal: Base class for entity-attached triggers
- AffectSides: A Personal that modifies sides based on conditions and effects
- AffectSideCondition: Base class for conditions (HasKeyword, SpecificSides, etc.)
- AffectSideEffect: Base class for effects (FlatBonus, AddKeyword, ReplaceWith, etc.)
- Poison: Personal that deals damage at end of turn, supports merging
- Cleansed: Personal that provides cleanse budget for removing debuffs
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from enum import Enum, auto
from typing import TYPE_CHECKING, Optional

if TYPE_CHECKING:
    from .dice import Side, Keyword
    from .fight import EntityState, SideState


class CleanseType(Enum):
    """Types of cleansable debuffs."""
    POISON = auto()
    PETRIFY = auto()
    WEAKEN = auto()
    INFLICT = auto()


class SpecificSidesType(Enum):
    """Predefined side index patterns for SpecificSidesCondition."""
    LEFT = ([2],)
    MIDDLE = ([4],)
    TOP = ([0],)
    BOTTOM = ([1],)
    RIGHT = ([3],)
    RIGHTMOST = ([5],)
    RIGHT_TWO = ([3, 5],)
    RIGHT_THREE = ([4, 3, 5],)
    LEFT_TWO = ([2, 4],)
    ROW = ([2, 4, 3, 5],)
    COLUMN = ([0, 4, 1],)
    ALL = ([0, 1, 2, 3, 4, 5],)

    def __init__(self, indices: list[int]):
        self.indices = indices


class Personal(ABC):
    """Base class for triggers attached to entities.

    Personals can modify die sides (via affect_side) and respond to various
    game events. They are collected from buffs, items, and traits.
    """

    def affect_side(self, side_state: "SideState", owner: "EntityState", trigger_index: int):
        """Modify a side's calculated effect. Called during side state calculation.

        Args:
            side_state: The side state being calculated (mutable)
            owner: The entity state that owns this side
            trigger_index: Index of this trigger in the active personals list
        """
        pass

    def get_priority(self) -> float:
        """Priority for ordering triggers. Lower values run first.

        Default priorities in Java:
        - mimicPriority/heroPassivePriority: -11
        - monsterPassivePriority: -9
        - combatPriority/buff: 0
        - passive (no buff): -10
        """
        return 0.0

    def get_poison_damage(self) -> int:
        """Return poison damage this trigger deals per turn."""
        return 0

    def get_regen(self) -> int:
        """Return regen amount this trigger provides per turn."""
        return 0

    def get_cleanse_type(self) -> Optional[CleanseType]:
        """Return the cleanse type if this is a cleansable debuff, else None."""
        return None

    def get_cleanse_amt(self) -> int:
        """Return how much cleanse budget this trigger provides."""
        return 0

    def can_merge(self, other: "Personal") -> bool:
        """Check if this personal can merge with another."""
        return False

    def merge(self, other: "Personal"):
        """Merge another personal into this one."""
        raise RuntimeError(f"Cannot merge {type(self).__name__} with {type(other).__name__}")

    def cleanse_by(self, amount: int) -> tuple[int, bool]:
        """Reduce this debuff by amount. Returns (used, fully_cleansed)."""
        return (0, False)

    def copy(self) -> "Personal":
        """Create a copy of this personal (for undo support)."""
        return self  # Default: immutable, return same instance


class AffectSideCondition(ABC):
    """Base class for conditions that determine which sides a trigger affects."""

    @abstractmethod
    def valid_for(self, side_state: "SideState", owner: "EntityState", trigger_index: int) -> bool:
        """Check if this condition is satisfied for the given side.

        Args:
            side_state: The side state being checked
            owner: The entity state that owns this side
            trigger_index: Index of the trigger in the active personals list

        Returns:
            True if the condition is satisfied
        """
        pass

    def get_index(self, side_state: "SideState", owner: "EntityState") -> int:
        """Get the index for indexed effects (like per-side bonuses).

        Returns -1 if not applicable.
        """
        return -1


class AffectSideEffect(ABC):
    """Base class for effects that modify sides when conditions are met."""

    @abstractmethod
    def affect(self, side_state: "SideState", owner: "EntityState", index: int, trigger_index: int):
        """Apply this effect to the side state.

        Args:
            side_state: The side state to modify (mutable)
            owner: The entity state that owns this side
            index: Index from condition (for indexed effects), or -1
            trigger_index: Index of the trigger in the active personals list
        """
        pass


class AffectSides(Personal):
    """Trigger that modifies sides based on conditions and effects.

    When calculating a side's state, AffectSides:
    1. Checks all conditions - if any fail, the trigger doesn't apply
    2. If all conditions pass, applies all effects in order

    Examples:
        # Add +1 to all sides
        AffectSides([], [FlatBonus(1)])

        # Add engage keyword to all sides
        AffectSides([], [AddKeyword(Keyword.ENGAGE)])

        # Add +1 to rightmost side only
        AffectSides([SpecificSidesCondition(SpecificSidesType.RIGHTMOST)], [FlatBonus(1)])

        # Add +1 to all ranged sides
        AffectSides([HasKeyword(Keyword.RANGED)], [FlatBonus(1)])
    """

    def __init__(
        self,
        conditions: list[AffectSideCondition] | AffectSideCondition | None = None,
        effects: list[AffectSideEffect] | AffectSideEffect | None = None
    ):
        # Normalize to lists
        if conditions is None:
            self.conditions = []
        elif isinstance(conditions, AffectSideCondition):
            self.conditions = [conditions]
        else:
            self.conditions = list(conditions)

        if effects is None:
            self.effects = []
        elif isinstance(effects, AffectSideEffect):
            self.effects = [effects]
        else:
            self.effects = list(effects)

    def affect_side(self, side_state: "SideState", owner: "EntityState", trigger_index: int):
        """Check conditions and apply effects if all conditions pass."""
        # Check all conditions
        index = -1
        for condition in self.conditions:
            if not condition.valid_for(side_state, owner, trigger_index):
                return  # Condition failed, don't apply effects

            # Get index from condition if applicable
            cond_index = condition.get_index(side_state, owner)
            if cond_index != -1:
                index = cond_index

        # All conditions passed, apply all effects
        for effect in self.effects:
            effect.affect(side_state, owner, index, trigger_index)


# ============================================================================
# Conditions
# ============================================================================

class HasKeyword(AffectSideCondition):
    """Condition that matches sides with any of the specified keywords."""

    def __init__(self, *keywords: "Keyword"):
        self.keywords = keywords

    def valid_for(self, side_state: "SideState", owner: "EntityState", trigger_index: int) -> bool:
        for keyword in self.keywords:
            if side_state.has_keyword(keyword):
                return True
        return False


class SpecificSidesCondition(AffectSideCondition):
    """Condition that matches sides at specific indices."""

    def __init__(self, side_type: SpecificSidesType):
        self.side_type = side_type

    def valid_for(self, side_state: "SideState", owner: "EntityState", trigger_index: int) -> bool:
        return side_state.index in self.side_type.indices

    def get_index(self, side_state: "SideState", owner: "EntityState") -> int:
        """Return the index within the side_type's indices list."""
        if side_state.index in self.side_type.indices:
            return self.side_type.indices.index(side_state.index)
        return -1


# ============================================================================
# Effects
# ============================================================================

class FlatBonus(AffectSideEffect):
    """Add a flat bonus to the side's value.

    Can be a single bonus (applied to all matching sides) or a list of bonuses
    (applied by index from condition).
    """

    def __init__(self, *bonus: int):
        self.bonuses = list(bonus) if bonus else [0]

    def affect(self, side_state: "SideState", owner: "EntityState", index: int, trigger_index: int):
        # Get the bonus for this index
        if len(self.bonuses) == 1:
            bonus = self.bonuses[0]
        elif 0 <= index < len(self.bonuses):
            bonus = self.bonuses[index]
        else:
            bonus = 0

        # Add to the calculated value
        side_state.add_value(bonus)


class AddKeyword(AffectSideEffect):
    """Add keywords to the side."""

    def __init__(self, *keywords: "Keyword"):
        self.keywords = list(keywords)

    def affect(self, side_state: "SideState", owner: "EntityState", index: int, trigger_index: int):
        for keyword in self.keywords:
            side_state.add_keyword(keyword)


class RemoveKeyword(AffectSideEffect):
    """Remove keywords from the side."""

    def __init__(self, *keywords: "Keyword"):
        self.keywords = list(keywords)

    def affect(self, side_state: "SideState", owner: "EntityState", index: int, trigger_index: int):
        for keyword in self.keywords:
            side_state.remove_keyword(keyword)


class ReplaceWith(AffectSideEffect):
    """Replace the side's calculated effect with a new side entirely."""

    def __init__(self, new_side: "Side"):
        self.new_side = new_side

    def affect(self, side_state: "SideState", owner: "EntityState", index: int, trigger_index: int):
        side_state.replace_with(self.new_side)


# ============================================================================
# Buff wrapper
# ============================================================================

@dataclass
class Buff:
    """A buff that wraps a Personal trigger with duration tracking.

    Buffs are the primary way triggers get attached to entities during combat.
    They can be permanent (turns=None) or temporary (turns=N).
    """
    personal: Personal
    turns_remaining: Optional[int] = None  # None = permanent
    skipped_first_tick: bool = False

    def is_expired(self) -> bool:
        """Check if this buff has expired."""
        return self.turns_remaining is not None and self.turns_remaining <= 0

    def tick(self):
        """Advance the buff by one turn."""
        if self.turns_remaining is not None:
            self.turns_remaining -= 1

    def copy(self) -> "Buff":
        """Create a copy of this buff, deep copying mutable personals."""
        # Deep copy the personal (for undo support with mutable personals like Poison)
        personal_copy = self.personal.copy()
        return Buff(
            personal=personal_copy,
            turns_remaining=self.turns_remaining,
            skipped_first_tick=self.skipped_first_tick
        )

    def get_cleanse_type(self) -> Optional[CleanseType]:
        """Get cleanse type if this buff is a cleansable debuff."""
        return self.personal.get_cleanse_type()

    def can_merge(self, other: "Buff") -> bool:
        """Check if this buff can merge with another.

        Merge requires:
        - Personal supports merging
        - Same turns_remaining
        - Same skipped_first_tick
        """
        if not self.personal.can_merge(other.personal):
            return False
        if self.turns_remaining != other.turns_remaining:
            return False
        if self.skipped_first_tick != other.skipped_first_tick:
            return False
        return True

    def merge(self, other: "Buff"):
        """Merge another buff into this one."""
        self.personal.merge(other.personal)

    def cleanse_by(self, amount: int) -> tuple[int, bool]:
        """Try to cleanse this buff. Returns (used, fully_cleansed)."""
        return self.personal.cleanse_by(amount)


# ============================================================================
# Mergeable debuffs (Poison, Regen, etc.)
# ============================================================================

class Poison(Personal):
    """Poison trigger - deals damage at end of turn, merges with other poison.

    Poison:
    - Deals damage = poison stacks at start of each turn (direct, bypasses shield)
    - Persists until cleansed
    - Multiple poison applications merge into one trigger (values add)
    """

    def __init__(self, value: int):
        self.value = value

    def get_poison_damage(self) -> int:
        """Return poison damage this trigger deals per turn."""
        return self.value

    def get_cleanse_type(self) -> CleanseType:
        """Poison is cleansable as POISON type."""
        return CleanseType.POISON

    def can_merge(self, other: "Personal") -> bool:
        """Can merge with other Poison triggers."""
        return isinstance(other, Poison)

    def merge(self, other: "Personal"):
        """Add the other poison's value to this one."""
        if isinstance(other, Poison):
            self.value += other.value

    def cleanse_by(self, amount: int) -> tuple[int, bool]:
        """Reduce poison by amount. Returns (used, fully_cleansed)."""
        used = min(amount, self.value)
        self.value -= used
        return (used, self.value <= 0)

    def copy(self) -> "Poison":
        """Create a copy (for undo support)."""
        return Poison(self.value)


class Cleansed(Personal):
    """Cleanse trigger - provides cleanse budget for removing debuffs.

    When cleanse is applied:
    1. A Cleansed trigger is added (usually with duration 1)
    2. Existing debuffs are iterated and cleansed using this budget
    3. The budget is tracked per CleanseType via cleansedMap
    """

    def __init__(self, amount: int):
        self.amount = amount

    def get_cleanse_amt(self) -> int:
        """Return how much cleanse budget this trigger provides."""
        return self.amount


class Regen(Personal):
    """Regen trigger - heals at end of turn, merges with other regen.

    Regen:
    - Heals = regen stacks at end of each turn (capped at max HP)
    - Persists until removed
    - Multiple regen applications merge into one trigger (values add)
    """

    def __init__(self, value: int):
        self.value = value

    def get_regen(self) -> int:
        """Return regen amount this trigger provides per turn."""
        return self.value

    def can_merge(self, other: "Personal") -> bool:
        """Can merge with other Regen triggers."""
        return isinstance(other, Regen)

    def merge(self, other: "Personal"):
        """Add the other regen's value to this one."""
        if isinstance(other, Regen):
            self.value += other.value

    def copy(self) -> "Regen":
        """Create a copy (for undo support)."""
        return Regen(self.value)


# ============================================================================
# Buff system triggers (Weaken, Boost, Vulnerable, Smith)
# ============================================================================

class Weaken(Personal):
    """Weaken trigger - reduces all side values by N.

    Weaken:
    - Reduces the calculated value of all sides by the weaken amount
    - Is cleansable via CleanseType.WEAKEN
    - Multiple weaken applications merge into one trigger (values add)
    """

    def __init__(self, amount: int):
        self.amount = amount

    def affect_side(self, side_state: "SideState", owner: "EntityState", trigger_index: int):
        """Reduce the side's value by the weaken amount."""
        side_state.add_value(-self.amount)

    def get_cleanse_type(self) -> CleanseType:
        """Weaken is cleansable."""
        return CleanseType.WEAKEN

    def can_merge(self, other: "Personal") -> bool:
        """Can merge with other Weaken triggers."""
        return isinstance(other, Weaken)

    def merge(self, other: "Personal"):
        """Add the other weaken's amount to this one."""
        if isinstance(other, Weaken):
            self.amount += other.amount

    def cleanse_by(self, amount: int) -> tuple[int, bool]:
        """Reduce weaken by amount. Returns (used, fully_cleansed)."""
        used = min(amount, self.amount)
        self.amount -= used
        return (used, self.amount <= 0)

    def copy(self) -> "Weaken":
        """Create a copy (for undo support)."""
        return Weaken(self.amount)


class Vulnerable(Personal):
    """Vulnerable trigger - increases incoming damage from dice/spells by N.

    Vulnerable:
    - Increases damage taken from dice and spells by the vulnerable amount
    - Does NOT affect damage from poison, pain, or other sources
    - Multiple vulnerable applications merge into one trigger (values add)
    """

    def __init__(self, bonus: int):
        self.bonus = bonus

    def get_vulnerable_bonus(self) -> int:
        """Return how much extra damage this entity takes."""
        return self.bonus

    def get_priority(self) -> float:
        """Vulnerable runs early to modify damage."""
        return -32.0

    def can_merge(self, other: "Personal") -> bool:
        """Can merge with other Vulnerable triggers."""
        return isinstance(other, Vulnerable)

    def merge(self, other: "Personal"):
        """Add the other vulnerable's bonus to this one."""
        if isinstance(other, Vulnerable):
            self.bonus += other.bonus

    def copy(self) -> "Vulnerable":
        """Create a copy (for undo support)."""
        return Vulnerable(self.bonus)


# ============================================================================
# TypeCondition for smith keyword
# ============================================================================

class TypeCondition(AffectSideCondition):
    """Condition that matches sides with specific effect types."""

    def __init__(self, *effect_types: "EffectType"):
        from .effects import EffectType
        self.effect_types = effect_types

    def valid_for(self, side_state: "SideState", owner: "EntityState", trigger_index: int) -> bool:
        """Check if the side's effect type matches any of the specified types."""
        for effect_type in self.effect_types:
            if side_state.effect_type == effect_type:
                return True
            # Also check if the effect type contains the side's type
            if effect_type.contains(side_state.effect_type):
                return True
        return False
