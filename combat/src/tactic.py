"""Tactic classes for the combat system.

Tactics are abilities that consume dice instead of mana. Each tactic has
a TacticCost that specifies what dice sides are needed to use it.
"""

from dataclasses import dataclass, field
from enum import Enum, auto
from typing import TYPE_CHECKING, Optional

from .effects import EffectType
from .dice import Keyword, Side
from .spell import SpellEffect

if TYPE_CHECKING:
    from .entity import Entity
    from .fight import FightLog, EntityState


class TacticCostType(Enum):
    """Types of costs that can be required for tactics.

    Each cost type matches certain dice sides based on effect type,
    pip value, or keyword count. Some cost types are "pippy" - they
    accumulate by the pip value of the side.
    """
    BASIC_SWORD = auto()   # Damage sides (pippy)
    BASIC_SHIELD = auto()  # Shield sides (pippy)
    BASIC_HEAL = auto()    # Heal sides (pippy)
    BASIC_MANA = auto()    # Mana sides (pippy)
    WILD = auto()          # Any pip (pippy)
    BLANK = auto()         # Blank sides
    PIPS_1 = auto()        # Exactly 1-pip sides
    PIPS_2 = auto()        # Exactly 2-pip sides
    PIPS_3 = auto()        # Exactly 3-pip sides
    PIPS_4 = auto()        # Exactly 4-pip sides
    KEYWORD = auto()       # 1-keyword sides
    TWO_KEYWORDS = auto()  # 2-keyword sides
    FOUR_KEYWORDS = auto() # 4-keyword sides

    @property
    def pippy(self) -> bool:
        """Returns True if this cost type accumulates by pip value."""
        return self in (
            TacticCostType.BASIC_SWORD,
            TacticCostType.BASIC_SHIELD,
            TacticCostType.BASIC_HEAL,
            TacticCostType.BASIC_MANA,
            TacticCostType.WILD,
        )

    def is_valid(self, side: Side) -> bool:
        """Check if a side matches this cost type.

        Based on TacticCostType.java:84-116.
        """
        effect_type = side.effect_type
        value = side.calculated_value
        keywords = side.keywords

        if self == TacticCostType.PIPS_4:
            return value == 4
        elif self == TacticCostType.PIPS_3:
            return value == 3
        elif self == TacticCostType.PIPS_2:
            return value == 2
        elif self == TacticCostType.PIPS_1:
            return value == 1
        elif self == TacticCostType.WILD:
            return effect_type.has_value()
        elif self == TacticCostType.BLANK:
            return effect_type == EffectType.BLANK
        elif self == TacticCostType.BASIC_SWORD:
            return (effect_type == EffectType.DAMAGE or
                    Keyword.DAMAGE in keywords)
        elif self == TacticCostType.BASIC_SHIELD:
            return (effect_type == EffectType.SHIELD or
                    effect_type == EffectType.HEAL_SHIELD or
                    Keyword.SELF_SHIELD in keywords or
                    Keyword.SHIELD in keywords)
        elif self == TacticCostType.BASIC_HEAL:
            return (effect_type == EffectType.HEAL or
                    effect_type == EffectType.HEAL_SHIELD or
                    Keyword.SELF_HEAL in keywords or
                    Keyword.HEAL in keywords)
        elif self == TacticCostType.BASIC_MANA:
            return (effect_type == EffectType.MANA or
                    Keyword.MANA in keywords)
        elif self == TacticCostType.KEYWORD:
            return len(keywords) == 1
        elif self == TacticCostType.TWO_KEYWORDS:
            return len(keywords) == 2
        elif self == TacticCostType.FOUR_KEYWORDS:
            return len(keywords) == 4
        else:
            return False

    @staticmethod
    def get_valid_types(side: Side) -> list["TacticCostType"]:
        """Return all cost types that this side satisfies."""
        return [t for t in TacticCostType if t.is_valid(side)]


@dataclass
class TacticCost:
    """A list of cost types required to use a tactic.

    Costs are fulfilled by matching rolled, unused hero dice to the
    required cost types. Some costs are "pippy" - they accumulate by
    the pip value of the side (e.g., a 3-pip damage side contributes 3
    toward a BASIC_SWORD cost).

    The tactical keyword doubles the contribution amount.
    """
    costs: list[TacticCostType] = field(default_factory=list)

    @classmethod
    def from_types(cls, *types: TacticCostType) -> "TacticCost":
        """Create a TacticCost from a list of cost types."""
        return cls(list(types))

    @classmethod
    def from_type_count(cls, cost_type: TacticCostType, count: int) -> "TacticCost":
        """Create a TacticCost with multiple of the same cost type."""
        return cls([cost_type] * count)

    def is_usable(self, fight_log: "FightLog") -> bool:
        """Check if rolled, unused hero dice satisfy all costs."""
        return len(self._get_fulfilled_costs(fight_log)) == len(self.costs)

    def get_contributing_entities(self, fight_log: "FightLog") -> list["Entity"]:
        """Return entities whose dice would be consumed to fulfill costs.

        This must return the same entities that _get_fulfilled_costs matched,
        so it tracks them during fulfillment calculation.
        """
        contributors, _ = self._calculate_fulfillment(fight_log)
        return contributors

    def _get_fulfilled_costs(self, fight_log: "FightLog") -> list[TacticCostType]:
        """Return list of costs that are fulfilled by available dice.

        Based on TacticCost.java:109-138.
        """
        _, fulfilled = self._calculate_fulfillment(fight_log)
        return fulfilled

    def _calculate_fulfillment(
        self, fight_log: "FightLog"
    ) -> tuple[list["Entity"], list[TacticCostType]]:
        """Calculate cost fulfillment and track contributing entities.

        Returns:
            (contributors, fulfilled_costs): List of entities that contributed
            and list of cost types that were fulfilled.

        Based on TacticCost.java:109-138.
        """
        from .fight import Temporality

        # Copy costs list so we can track unfulfilled
        remaining_costs = list(self.costs)
        fulfilled = []
        contributors = []

        # Get all hero states
        for entity in fight_log.heroes:
            state = fight_log.get_state(entity, Temporality.PRESENT)

            # Skip if die is already used
            if state.is_used():
                continue

            # Get the current side of the die
            die = getattr(entity, 'die', None)
            if die is None:
                continue

            side = die.get_current_side()
            valid_types = TacticCostType.get_valid_types(side)

            entity_contributed = False

            # For each valid cost type this side matches
            for cost_type in valid_types:
                # Calculate contribution amount
                if cost_type.pippy and side.effect_type.has_value():
                    amt = side.calculated_value
                else:
                    amt = 1

                # tactical keyword doubles contribution
                if side.has_keyword(Keyword.TACTICAL):
                    amt *= 2

                # Try to fulfill remaining costs
                for _ in range(amt):
                    if cost_type in remaining_costs:
                        remaining_costs.remove(cost_type)
                        fulfilled.append(cost_type)
                        entity_contributed = True

            if entity_contributed:
                contributors.append(entity)

            # Early exit if all costs fulfilled
            if not remaining_costs:
                break

        return contributors, fulfilled


@dataclass
class Tactic:
    """A tactic ability that consumes dice to produce an effect.

    Similar to Spell, but costs dice instead of mana.
    """
    name: str
    cost: TacticCost
    effect: SpellEffect

    def has_keyword(self, keyword: Keyword) -> bool:
        return self.effect.has_keyword(keyword)

    def is_usable(self, fight_log: "FightLog") -> bool:
        """Check if this tactic can currently be used."""
        return self.cost.is_usable(fight_log)

    def get_contributing_entities(self, fight_log: "FightLog") -> list["Entity"]:
        """Get entities whose dice will be consumed."""
        return self.cost.get_contributing_entities(fight_log)
