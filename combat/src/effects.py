"""Effect utilities and constants."""

from enum import Enum, auto


class EffectType(Enum):
    """Types of effects that can appear on die sides."""
    DAMAGE = auto()
    HEAL = auto()
    SHIELD = auto()
    HEAL_SHIELD = auto()  # Combined heal+shield effect
    BLANK = auto()  # Empty/petrified sides - no effect
    MANA = auto()  # Grants mana to the player

    def contains(self, other: "EffectType") -> bool:
        """Check if this effect type contains the other type.

        Combined types like HEAL_SHIELD contain both HEAL and SHIELD.
        """
        if self == other:
            return True
        if self == EffectType.HEAL_SHIELD:
            return other in (EffectType.HEAL, EffectType.SHIELD)
        return False


# Default maximum value for any effect (damage, healing, shield, etc.)
DEFAULT_NUMBER_LIMIT = 999


def clamp_effect_value(value: int, limit: int = DEFAULT_NUMBER_LIMIT) -> int:
    """Clamp an effect value to the global number limit.

    All effect values (damage, healing, shield, etc.) are capped at this limit.
    Default is 999, but can be customized via game modifiers.
    """
    return min(value, limit)
