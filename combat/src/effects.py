"""Effect utilities and constants."""

# Default maximum value for any effect (damage, healing, shield, etc.)
DEFAULT_NUMBER_LIMIT = 999


def clamp_effect_value(value: int, limit: int = DEFAULT_NUMBER_LIMIT) -> int:
    """Clamp an effect value to the global number limit.

    All effect values (damage, healing, shield, etc.) are capped at this limit.
    Default is 999, but can be customized via game modifiers.
    """
    return min(value, limit)
