"""Item system with side modifiers and triggers."""

from dataclasses import dataclass, field
from typing import Optional, Protocol, TYPE_CHECKING
from abc import ABC, abstractmethod
from enum import Enum, auto

from .effects import EffectType
from .dice import Side

if TYPE_CHECKING:
    from .fight import FightLog
    from .entity import Entity


class TriggerType(Enum):
    """Types of events that can trigger item effects."""
    ON_RESCUE = auto()  # When wearer is rescued from dying


class TriggerEffect(ABC):
    """Base class for effects triggered by item triggers."""

    @abstractmethod
    def apply(self, fight: "FightLog", target: "Entity") -> None:
        """Apply this effect to the target."""
        pass


@dataclass
class MaxHPBuff(TriggerEffect):
    """Increases target's max HP."""
    amount: int

    def apply(self, fight: "FightLog", target: "Entity") -> None:
        fight.modify_max_hp(target, self.amount)


@dataclass
class ItemTrigger:
    """A trigger that fires on specific events."""
    trigger_type: TriggerType
    effect: TriggerEffect
    target_self: bool = True  # If True, applies effect to item wearer


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
    """Adds a flat bonus to matching sides.

    Uses effect type containment matching: a HEAL_SHIELD side matches
    both HEAL and SHIELD type conditions.
    """
    effect_type: EffectType
    bonus: int

    def applies_to(self, side: Side) -> bool:
        # Use containment check: HEAL_SHIELD contains both HEAL and SHIELD
        return side.effect_type.contains(self.effect_type)

    def modify(self, side: Side) -> Side:
        if self.applies_to(side):
            return side.with_bonus(self.bonus)
        return side


@dataclass
class Item:
    """An item that can be equipped to a hero."""
    name: str
    modifiers: list[SideModifier] = field(default_factory=list)
    triggers: list[ItemTrigger] = field(default_factory=list)

    def modify_side(self, side: Side) -> Side:
        """Apply all modifiers to a side and return the modified version."""
        result = side
        for modifier in self.modifiers:
            result = modifier.modify(result)
        return result

    def get_triggers(self, trigger_type: TriggerType) -> list[ItemTrigger]:
        """Get all triggers of a specific type."""
        return [t for t in self.triggers if t.trigger_type == trigger_type]


# Item library
GAUNTLET = Item("Gauntlet", modifiers=[FlatBonus(EffectType.DAMAGE, 1)])
FAINT_HALO = Item(
    "Faint Halo",
    triggers=[ItemTrigger(TriggerType.ON_RESCUE, MaxHPBuff(1), target_self=True)]
)
DRAGON_PIPE = Item("Dragon Pipe", modifiers=[FlatBonus(EffectType.HEAL, 1)])
METAL_STUDS = Item("Metal Studs", modifiers=[FlatBonus(EffectType.SHIELD, 1)])


def item_by_name(name: str) -> Item:
    """Get an item by name."""
    items = {
        "Gauntlet": GAUNTLET,
        "Faint Halo": FAINT_HALO,
        "Dragon Pipe": DRAGON_PIPE,
        "Metal Studs": METAL_STUDS,
    }
    return items[name]
