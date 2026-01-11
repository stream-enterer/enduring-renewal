"""Tests for the trigger system (AffectSides, conditions, effects).

These tests correspond to blocked tests from the Java decompilation:
- TestTriggerOrdering.testBuffReplacedSides
- TestTriggerOrdering.testBrainGrips
- TestKeyword.precisePlusMagic (partial)
"""

import pytest

from src.entity import Entity, EntityType, Team, EntitySize
from src.dice import (
    Die, Side, Keyword, arrow, wand_self_heal, shield_mana,
    create_fighter_die
)
from src.effects import EffectType
from src.fight import FightLog, Temporality
from src.triggers import (
    AffectSides, Buff, Personal,
    HasKeyword, SpecificSidesCondition, SpecificSidesType,
    FlatBonus, AddKeyword, RemoveKeyword, ReplaceWith
)


# Test entity types
TEST_HERO = EntityType("TestHero", 10, EntitySize.HERO)
TEST_MONSTER = EntityType("TestMonster", 5, EntitySize.HERO)


def setup_fight_with_die():
    """Create a basic fight with a hero that has a die."""
    hero_entity = Entity(TEST_HERO, Team.HERO)
    hero_entity.die = create_fighter_die()

    monster_entity = Entity(TEST_MONSTER, Team.MONSTER)

    fight = FightLog([hero_entity], [monster_entity])
    return fight, hero_entity, monster_entity


class TestAffectSidesBasics:
    """Test basic AffectSides functionality."""

    def test_flat_bonus_all_sides(self):
        """AffectSides with no conditions applies FlatBonus to all sides."""
        fight, hero, monster = setup_fight_with_die()

        # Add +1 to all sides
        fight.add_trigger(hero, AffectSides([], [FlatBonus(1)]))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Check all 6 sides have +1 bonus
        for i in range(6):
            side_state = state.get_side_state(i)
            original = hero.die.get_side(i)
            assert side_state.value == original.calculated_value + 1, \
                f"Side {i}: expected {original.calculated_value + 1}, got {side_state.value}"

    def test_add_keyword_all_sides(self):
        """AffectSides with no conditions adds keyword to all sides."""
        fight, hero, monster = setup_fight_with_die()

        # Add ENGAGE keyword to all sides
        fight.add_trigger(hero, AffectSides([], [AddKeyword(Keyword.ENGAGE)]))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Check all 6 sides have ENGAGE keyword
        for i in range(6):
            side_state = state.get_side_state(i)
            assert side_state.has_keyword(Keyword.ENGAGE), \
                f"Side {i} should have ENGAGE keyword"


class TestBuffReplacedSides:
    """Tests corresponding to Java TestTriggerOrdering.testBuffReplacedSides.

    Tests that:
    1. ReplaceWith can change a side's effect
    2. FlatBonus stacks with replaced sides
    3. HasKeyword condition works after replacement
    """

    def test_replace_rightmost_with_arrow(self):
        """Replace rightmost side (index 5) with arrow(1)."""
        fight, hero, monster = setup_fight_with_die()

        # Replace rightmost side with arrow(1)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(1))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)  # Rightmost

        assert side_state.effect_type == EffectType.DAMAGE
        assert side_state.value == 1
        assert side_state.has_keyword(Keyword.RANGED)

    def test_replace_then_flat_bonus(self):
        """Replace rightmost with arrow(1), then add +1 = 2 damage."""
        fight, hero, monster = setup_fight_with_die()

        # Replace rightmost with arrow(1)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(1))
        ))

        # Add +1 to rightmost
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            FlatBonus(1)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # arrow(1) + FlatBonus(1) = 2
        assert side_state.value == 2, f"Expected 2, got {side_state.value}"

    def test_has_keyword_after_replacement(self):
        """HasKeyword condition sees keywords from replaced sides."""
        fight, hero, monster = setup_fight_with_die()

        # Replace rightmost with arrow(1) - has RANGED
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(1))
        ))

        # Add +1 to rightmost (already has arrow)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            FlatBonus(1)
        ))

        # Add +1 to all RANGED sides
        fight.add_trigger(hero, AffectSides(
            HasKeyword(Keyword.RANGED),
            FlatBonus(1)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # arrow(1) + FlatBonus(1) + FlatBonus(1 for ranged) = 3
        assert side_state.value == 3, f"Expected 3, got {side_state.value}"

    def test_full_buff_replaced_sides_scenario(self):
        """Full test matching Java TestTriggerOrdering.testBuffReplacedSides."""
        fight, hero, monster = setup_fight_with_die()

        # Step 1: Replace rightmost with arrow(1)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(1))
        ))

        # Step 2: Add +1 to rightmost
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            FlatBonus(1)
        ))

        # Check: rightmost should be 2 damage
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)
        assert side_state.value == 2, "2 dmg should be dealt"

        # Step 3: Add +1 to all ranged sides
        fight.add_trigger(hero, AffectSides(
            HasKeyword(Keyword.RANGED),
            FlatBonus(1)
        ))

        # Check: rightmost should now be 3 damage
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)
        assert side_state.value == 3, "+3 dmg should be dealt (from 2 to 5 total)"


class TestBrainGrips:
    """Tests corresponding to Java TestTriggerOrdering.testBrainGrips.

    Tests that:
    1. FlatBonus only applies when HasKeyword condition is met
    2. RemoveKeyword prevents future HasKeyword matches
    3. But existing bonuses from before RemoveKeyword are preserved
    """

    def test_has_keyword_condition(self):
        """FlatBonus only applies when side has the keyword."""
        fight, hero, monster = setup_fight_with_die()

        # Replace all sides with wand(1) which has SINGLE_USE
        fight.turn_into(hero, wand_self_heal(1))

        # Add +1 to sides with SINGLE_USE
        fight.add_trigger(hero, AffectSides(
            HasKeyword(Keyword.SINGLE_USE),
            FlatBonus(1)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # wand(1) + FlatBonus(1) = 2
        assert side_state.value == 2, f"Expected 2, got {side_state.value}"

    def test_remove_keyword_then_flat_bonus(self):
        """After RemoveKeyword, HasKeyword condition no longer matches."""
        fight, hero, monster = setup_fight_with_die()

        # Replace all sides with wand(1) which has SINGLE_USE
        fight.turn_into(hero, wand_self_heal(1))

        # Remove SINGLE_USE keyword from all sides
        fight.add_trigger(hero, AffectSides([], [RemoveKeyword(Keyword.SINGLE_USE)]))

        # Try to add +1 to sides with SINGLE_USE (should not match now)
        fight.add_trigger(hero, AffectSides(
            HasKeyword(Keyword.SINGLE_USE),
            FlatBonus(1)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # wand(1) - SINGLE_USE removed, so FlatBonus doesn't apply = 1
        assert side_state.value == 1, f"Expected 1, got {side_state.value}"

    def test_flat_bonus_before_remove_keyword_preserved(self):
        """Test matching Java TestTriggerOrdering.testBrainGrips.

        Key behavior: triggers run in order. A FlatBonus that applies when
        a keyword exists will still apply even if a later trigger removes
        that keyword.
        """
        fight, hero, monster = setup_fight_with_die()

        # Replace all sides with wand(1) which has SINGLE_USE
        fight.turn_into(hero, wand_self_heal(1))

        # First trigger: +1 to SINGLE_USE sides
        fight.add_trigger(hero, AffectSides(
            HasKeyword(Keyword.SINGLE_USE),
            FlatBonus(1)
        ))

        # Check: side should have value 2 (wand 1 + bonus 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)
        assert side_state.value == 2, "Should be 2 damage"

        # Second trigger: remove SINGLE_USE
        fight.add_trigger(hero, AffectSides([], [RemoveKeyword(Keyword.SINGLE_USE)]))

        # Check: value should STILL be 2 (bonus was already applied)
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)
        assert side_state.value == 2, "Should still be 2 damage after removing keyword"

        # The keyword should be gone
        assert not side_state.has_keyword(Keyword.SINGLE_USE), \
            "SINGLE_USE keyword should be removed"


class TestGetActivePersonals:
    """Test EntityState.get_active_personals() method."""

    def test_empty_by_default(self):
        """No personals by default."""
        fight, hero, monster = setup_fight_with_die()
        state = fight.get_state(hero, Temporality.PRESENT)

        personals = state.get_active_personals()
        assert len(personals) == 0

    def test_counts_added_triggers(self):
        """Adding triggers increases personal count."""
        fight, hero, monster = setup_fight_with_die()

        # Add 2 triggers
        fight.add_trigger(hero, AffectSides([], [FlatBonus(1)]))
        fight.add_trigger(hero, AffectSides([], [AddKeyword(Keyword.ENGAGE)]))

        state = fight.get_state(hero, Temporality.PRESENT)
        personals = state.get_active_personals()
        assert len(personals) == 2


class TestSpecificSidesTypes:
    """Test different SpecificSidesType values."""

    def test_left_side(self):
        """LEFT matches index 2."""
        fight, hero, monster = setup_fight_with_die()

        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.LEFT),
            FlatBonus(10)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Only side 2 should have +10
        for i in range(6):
            side_state = state.get_side_state(i)
            original = hero.die.get_side(i).calculated_value
            if i == 2:
                assert side_state.value == original + 10, f"Side 2 should have +10"
            else:
                assert side_state.value == original, f"Side {i} should be unchanged"

    def test_right_two(self):
        """RIGHT_TWO matches indices 3 and 5."""
        fight, hero, monster = setup_fight_with_die()

        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHT_TWO),
            FlatBonus(10)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Sides 3 and 5 should have +10
        for i in range(6):
            side_state = state.get_side_state(i)
            original = hero.die.get_side(i).calculated_value
            if i in [3, 5]:
                assert side_state.value == original + 10, f"Side {i} should have +10"
            else:
                assert side_state.value == original, f"Side {i} should be unchanged"

    def test_all_sides(self):
        """ALL matches all indices 0-5."""
        fight, hero, monster = setup_fight_with_die()

        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.ALL),
            FlatBonus(10)
        ))

        state = fight.get_state(hero, Temporality.PRESENT)

        # All sides should have +10
        for i in range(6):
            side_state = state.get_side_state(i)
            original = hero.die.get_side(i).calculated_value
            assert side_state.value == original + 10, f"Side {i} should have +10"
