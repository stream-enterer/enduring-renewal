"""Spell classes for the combat system."""

from dataclasses import dataclass, field
from typing import TYPE_CHECKING, Optional

from .effects import EffectType
from .dice import Keyword

if TYPE_CHECKING:
    from .entity import Entity
    from .fight import FightLog


@dataclass
class SpellEffect:
    """Effect applied by a spell (similar to Side but for spells)."""
    effect_type: EffectType
    value: int
    keywords: set[Keyword] = field(default_factory=set)
    target_friendly: bool = False  # True if targets allies, False if targets enemies

    def has_keyword(self, keyword: Keyword) -> bool:
        return keyword in self.keywords


@dataclass
class Spell:
    """A castable spell with a mana cost and effect."""
    name: str
    base_cost: int
    effect: SpellEffect

    def has_keyword(self, keyword: Keyword) -> bool:
        return self.effect.has_keyword(keyword)


@dataclass
class SpellState:
    """Tracks spell state during combat.

    Each spell owned by an entity has its own SpellState that tracks:
    - How many times it's been cast this fight (for singleCast)
    - How many times it's been cast this turn (for cooldown)
    - Cost modifications from deplete/channel
    """
    spell: Spell
    cast_count_this_fight: int = 0
    cast_count_this_turn: int = 0
    cost_modifier: int = 0  # Accumulated from deplete (+1) and channel (-1)

    def get_current_cost(self) -> int:
        """Get the current cost after all modifiers.

        Cost is base_cost + cost_modifier, with minimum of 1 (from channel).
        """
        return max(1, self.spell.base_cost + self.cost_modifier)

    def is_available(self) -> bool:
        """Check if this spell can be cast.

        Returns False if:
        - Spell has singleCast and was already cast this fight
        - Spell has cooldown and was already cast this turn
        """
        if self.spell.has_keyword(Keyword.SINGLE_CAST):
            if self.cast_count_this_fight > 0:
                return False

        if self.spell.has_keyword(Keyword.COOLDOWN):
            if self.cast_count_this_turn > 0:
                return False

        return True

    def on_cast(self):
        """Called after spell is successfully cast."""
        self.cast_count_this_fight += 1
        self.cast_count_this_turn += 1

        # Apply cost modifiers for deplete/channel
        if self.spell.has_keyword(Keyword.DEPLETE):
            self.cost_modifier += 1
        if self.spell.has_keyword(Keyword.CHANNEL):
            self.cost_modifier -= 1

    def start_turn(self):
        """Called at the start of each turn to reset per-turn tracking."""
        self.cast_count_this_turn = 0


@dataclass
class QueuedSpell:
    """A spell effect queued for future execution (for the future keyword)."""
    spell: Spell
    caster: "Entity"
    target: Optional["Entity"]
    turns_remaining: int = 1  # Executes when this reaches 0

    def tick(self) -> bool:
        """Decrement turn counter. Returns True if ready to execute."""
        self.turns_remaining -= 1
        return self.turns_remaining <= 0
