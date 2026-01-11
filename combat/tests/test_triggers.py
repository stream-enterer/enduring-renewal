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


class TestStasis:
    """Tests for STASIS keyword - blocks all side modifications."""

    def test_stasis_blocks_flat_bonus(self):
        """Side with STASIS blocks FlatBonus trigger."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to have STASIS
        hero.die.set_side(0, Side(EffectType.DAMAGE, 2, {Keyword.STASIS}))

        # Try to add +10 to all sides
        fight.add_trigger(hero, AffectSides([], [FlatBonus(10)]))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Side 0 should be unchanged (stasis blocked the bonus)
        side_state_0 = state.get_side_state(0)
        assert side_state_0.value == 2, f"Stasis side should be unchanged, got {side_state_0.value}"

        # Other sides should get +10
        side_state_1 = state.get_side_state(1)
        original_1 = hero.die.get_side(1).calculated_value
        assert side_state_1.value == original_1 + 10, f"Non-stasis side should get +10"

    def test_stasis_blocks_add_keyword(self):
        """Side with STASIS blocks AddKeyword trigger."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to have STASIS
        hero.die.set_side(0, Side(EffectType.DAMAGE, 2, {Keyword.STASIS}))

        # Try to add ENGAGE to all sides
        fight.add_trigger(hero, AffectSides([], [AddKeyword(Keyword.ENGAGE)]))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Side 0 should NOT have ENGAGE (only STASIS)
        side_state_0 = state.get_side_state(0)
        assert not side_state_0.has_keyword(Keyword.ENGAGE), "Stasis side should not get ENGAGE"
        assert side_state_0.has_keyword(Keyword.STASIS), "Stasis side should still have STASIS"

        # Other sides SHOULD have ENGAGE
        side_state_1 = state.get_side_state(1)
        assert side_state_1.has_keyword(Keyword.ENGAGE), "Non-stasis side should get ENGAGE"

    def test_stasis_blocks_replacement(self):
        """Side with STASIS blocks ReplaceWith trigger."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 5 (rightmost) to have STASIS
        hero.die.set_side(5, Side(EffectType.DAMAGE, 2, {Keyword.STASIS}))

        # Try to replace rightmost with arrow(5)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(5))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # Should still be original 2 damage with STASIS, not arrow(5)
        assert side_state.value == 2, f"Stasis side should be unchanged, got {side_state.value}"
        assert side_state.has_keyword(Keyword.STASIS), "Should still have STASIS"
        assert not side_state.has_keyword(Keyword.RANGED), "Should NOT have RANGED from arrow"

    def test_stasis_blocks_subsequent_triggers(self):
        """If a trigger adds STASIS, subsequent triggers are blocked."""
        fight, hero, monster = setup_fight_with_die()

        # First trigger: Add STASIS to side 0
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            AddKeyword(Keyword.STASIS)
        ))

        # Second trigger: Try to add +10 to all sides (should be blocked for side 0)
        fight.add_trigger(hero, AffectSides([], [FlatBonus(10)]))

        state = fight.get_state(hero, Temporality.PRESENT)

        # Side 0 got STASIS from first trigger, so second trigger blocked
        side_state_0 = state.get_side_state(0)
        original_0 = hero.die.get_side(0).calculated_value
        assert side_state_0.value == original_0, f"Stasis side should not get bonus"
        assert side_state_0.has_keyword(Keyword.STASIS), "Should have STASIS"

        # Other sides should get +10
        side_state_1 = state.get_side_state(1)
        original_1 = hero.die.get_side(1).calculated_value
        assert side_state_1.value == original_1 + 10, f"Non-stasis side should get +10"


class TestEnduring:
    """Tests for ENDURING keyword - when replaced, keeps keywords."""

    def test_enduring_keeps_keywords_on_replace(self):
        """Side with ENDURING keeps its keywords when replaced."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 5 (rightmost) to have ENDURING and ENGAGE
        hero.die.set_side(5, Side(EffectType.DAMAGE, 2, {Keyword.ENDURING, Keyword.ENGAGE}))

        # Replace rightmost with arrow(5) which has RANGED
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(5))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # Value should be from the new side (5)
        assert side_state.value == 5, f"Value should be from arrow, got {side_state.value}"

        # Effect type should be from the new side (DAMAGE)
        assert side_state.effect_type == EffectType.DAMAGE, "Effect type should be DAMAGE"

        # Should have BOTH original keywords AND new side's keywords
        assert side_state.has_keyword(Keyword.ENDURING), "Should keep ENDURING"
        assert side_state.has_keyword(Keyword.ENGAGE), "Should keep ENGAGE"
        assert side_state.has_keyword(Keyword.RANGED), "Should get RANGED from arrow"

    def test_enduring_with_heal_to_damage(self):
        """ENDURING side changing from heal to damage keeps keywords."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to be heal with ENDURING and RESCUE
        hero.die.set_side(0, Side(EffectType.HEAL, 3, {Keyword.ENDURING, Keyword.RESCUE}))

        # Replace with damage
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            ReplaceWith(Side(EffectType.DAMAGE, 2, set()))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # Effect type changes, value changes
        assert side_state.effect_type == EffectType.DAMAGE, "Effect type should change"
        assert side_state.value == 2, "Value should change"

        # But keywords are preserved
        assert side_state.has_keyword(Keyword.ENDURING), "Should keep ENDURING"
        assert side_state.has_keyword(Keyword.RESCUE), "Should keep RESCUE"


class TestDogma:
    """Tests for DOGMA keyword - when replaced, only pips change."""

    def test_dogma_only_changes_pips(self):
        """Side with DOGMA only has its pips changed on replace."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 5 (rightmost) to be damage with DOGMA and ENGAGE
        hero.die.set_side(5, Side(EffectType.DAMAGE, 2, {Keyword.DOGMA, Keyword.ENGAGE}))

        # Replace rightmost with heal(5)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(Side(EffectType.HEAL, 5, {Keyword.RESCUE}))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # Only pips should change to 5
        assert side_state.value == 5, f"Pips should change to 5, got {side_state.value}"

        # Effect type should stay DAMAGE (not change to HEAL)
        assert side_state.effect_type == EffectType.DAMAGE, "Effect type should stay DAMAGE"

        # Keywords should stay (DOGMA, ENGAGE), not get RESCUE from new side
        assert side_state.has_keyword(Keyword.DOGMA), "Should keep DOGMA"
        assert side_state.has_keyword(Keyword.ENGAGE), "Should keep ENGAGE"
        assert not side_state.has_keyword(Keyword.RESCUE), "Should NOT get RESCUE"

    def test_dogma_with_zero_value_replacement(self):
        """DOGMA side can be set to 0 pips."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to be damage with DOGMA
        hero.die.set_side(0, Side(EffectType.DAMAGE, 5, {Keyword.DOGMA}))

        # Replace with blank (0 value)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            ReplaceWith(Side(EffectType.BLANK, 0, set()))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # Pips should be 0
        assert side_state.value == 0, f"Pips should be 0, got {side_state.value}"

        # Effect type should stay DAMAGE
        assert side_state.effect_type == EffectType.DAMAGE, "Effect type should stay DAMAGE"

        # DOGMA keyword should be preserved
        assert side_state.has_keyword(Keyword.DOGMA), "Should keep DOGMA"


class TestResilient:
    """Tests for RESILIENT keyword - when replaced, keeps pips."""

    def test_resilient_keeps_pips(self):
        """Side with RESILIENT keeps its pip value when replaced."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 5 (rightmost) to be damage 10 with RESILIENT
        hero.die.set_side(5, Side(EffectType.DAMAGE, 10, {Keyword.RESILIENT}))

        # Replace rightmost with arrow(1)
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.RIGHTMOST),
            ReplaceWith(arrow(1))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(5)

        # Pips should stay at 10 (from original)
        assert side_state.value == 10, f"Pips should stay at 10, got {side_state.value}"

        # Effect type changes to new side's type
        assert side_state.effect_type == EffectType.DAMAGE, "Effect type should be DAMAGE"

        # Gets new side's keywords BUT also keeps RESILIENT
        assert side_state.has_keyword(Keyword.RANGED), "Should get RANGED from arrow"
        assert side_state.has_keyword(Keyword.RESILIENT), "Should keep RESILIENT"

    def test_resilient_loses_other_keywords(self):
        """RESILIENT side loses other keywords (except RESILIENT) when replaced."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to be damage with RESILIENT and ENGAGE
        hero.die.set_side(0, Side(EffectType.DAMAGE, 5, {Keyword.RESILIENT, Keyword.ENGAGE}))

        # Replace with heal that has RESCUE
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            ReplaceWith(Side(EffectType.HEAL, 2, {Keyword.RESCUE}))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # Pips stay at 5
        assert side_state.value == 5, f"Pips should stay at 5, got {side_state.value}"

        # Effect type changes to HEAL
        assert side_state.effect_type == EffectType.HEAL, "Effect type should change to HEAL"

        # Loses ENGAGE, gets RESCUE, keeps RESILIENT
        assert not side_state.has_keyword(Keyword.ENGAGE), "Should lose ENGAGE"
        assert side_state.has_keyword(Keyword.RESCUE), "Should get RESCUE"
        assert side_state.has_keyword(Keyword.RESILIENT), "Should keep RESILIENT"


class TestCombinedReplacementKeywords:
    """Test combinations of enduring/dogma/resilient keywords."""

    def test_dogma_with_resilient_resilient_wins(self):
        """When DOGMA + RESILIENT both present, RESILIENT restores pips after DOGMA.

        Processing order in Java:
        1. DOGMA: changes pips to new value, keeps keywords and effect type
        2. RESILIENT: restores original pips

        So RESILIENT "wins" for pips, but DOGMA still prevents effect type change.
        """
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to have DOGMA and RESILIENT
        hero.die.set_side(0, Side(EffectType.SHIELD, 3, {Keyword.DOGMA, Keyword.RESILIENT}))

        # Replace with damage 10
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            ReplaceWith(Side(EffectType.DAMAGE, 10, {Keyword.ENGAGE}))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # RESILIENT restores original pips after DOGMA
        assert side_state.value == 3, f"Pips should be restored to 3 by RESILIENT, got {side_state.value}"

        # Effect type stays SHIELD (from DOGMA blocking replacement)
        assert side_state.effect_type == EffectType.SHIELD, "Effect type should stay SHIELD (from DOGMA)"

        # Original keywords preserved (DOGMA blocked replacement)
        assert side_state.has_keyword(Keyword.DOGMA), "Should keep DOGMA"
        assert side_state.has_keyword(Keyword.RESILIENT), "Should keep RESILIENT"

        # Should NOT get ENGAGE (dogma blocked the replacement)
        assert not side_state.has_keyword(Keyword.ENGAGE), "Should NOT get ENGAGE"

    def test_enduring_and_resilient_together(self):
        """ENDURING + RESILIENT: keeps keywords AND pips, gets new effect type."""
        fight, hero, monster = setup_fight_with_die()

        # Set side 0 to have ENDURING, RESILIENT, and ENGAGE
        hero.die.set_side(0, Side(EffectType.DAMAGE, 5, {Keyword.ENDURING, Keyword.RESILIENT, Keyword.ENGAGE}))

        # Replace with heal 2 that has RESCUE
        fight.add_trigger(hero, AffectSides(
            SpecificSidesCondition(SpecificSidesType.TOP),
            ReplaceWith(Side(EffectType.HEAL, 2, {Keyword.RESCUE}))
        ))

        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)

        # Pips stay at 5 (RESILIENT)
        assert side_state.value == 5, f"Pips should stay at 5, got {side_state.value}"

        # Effect type changes to HEAL (normal replacement)
        assert side_state.effect_type == EffectType.HEAL, "Effect type should change to HEAL"

        # Keeps original keywords (ENDURING) + gets new keywords (RESCUE) + keeps RESILIENT
        assert side_state.has_keyword(Keyword.ENDURING), "Should keep ENDURING"
        assert side_state.has_keyword(Keyword.RESILIENT), "Should keep RESILIENT"
        assert side_state.has_keyword(Keyword.ENGAGE), "Should keep ENGAGE (from enduring)"
        assert side_state.has_keyword(Keyword.RESCUE), "Should get RESCUE"
