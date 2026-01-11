"""Tests for poison and cleanse mechanics.

Based on TestCleanse.java from the original test suite.
Tests poison application, merging, cleansing, and undo support.
"""

import pytest
from src.entity import Entity, Team, EntityType, EntitySize
from src.fight import FightLog, Temporality


class TestCleanse:
    """Tests for poison and cleanse mechanics."""

    @pytest.fixture
    def setup_fight(self):
        """Set up a basic fight with 1 hero vs 1 monster."""
        hero_type = EntityType("TestHero", 10, EntitySize.HERO)
        monster_type = EntityType("TestMonster", 5, EntitySize.HERO)
        hero = Entity(hero_type, Team.HERO)
        monster = Entity(monster_type, Team.MONSTER)
        fight = FightLog([hero], [monster])
        return fight, hero, monster

    def test_poison_combine(self, setup_fight):
        """Multiple poison applications merge into one trigger.

        Two dmgPoison(1) hits should:
        - Combine into one Poison(2) trigger
        - getPoisonDamageTaken() returns 2
        - After nextTurn, HP = max - 4 (2 damage this turn + 2 projected next turn)
        """
        f, h, m = setup_fight

        # Give hero 100 shields to absorb the damage component of dmgPoison
        f.apply_shield(h, 100)

        # Count initial buffs/personals
        initial_state = f.get_state(h, Temporality.PRESENT)
        num_personals = len(initial_state.get_active_personals())

        # First poison hit
        f.apply_poison(h, 1)

        # Check state after first poison (PRESENT state for HP, FUTURE for poison)
        present = f.get_state(h, Temporality.PRESENT)
        future = f.get_state(h, Temporality.FUTURE)
        assert present.hp == h.entity_type.hp, "Should be full hp (poison hasn't triggered yet)"
        assert future.get_poison_damage_taken() == 1, "Should be 1 incoming poison"
        assert len(present.get_active_personals()) == num_personals + 1, "Should be 1 extra trigger"

        # Second poison hit (should merge)
        f.apply_poison(h, 1)

        # Check state after second poison
        present = f.get_state(h, Temporality.PRESENT)
        future = f.get_state(h, Temporality.FUTURE)
        assert future.get_poison_damage_taken() == 2, "Should be 2 incoming poison"
        assert len(present.get_active_personals()) == num_personals + 1, "Should still only be 1 extra trigger"

        # Advance turn - poison triggers
        f.next_turn()

        # Check future state after nextTurn
        # HP = max - 2 (poison damage this turn) - 2 (projected next turn) = max - 4
        future = f.get_state(h, Temporality.FUTURE)
        assert future.hp == h.entity_type.hp - 4, "Should have taken 4 poison damage"

    def test_poison_cleanse(self, setup_fight):
        """shieldCleanse removes poison completely.

        Flow:
        1. Apply 100 shield
        2. Apply poison(1)
        3. Apply shieldCleanse(1) - removes poison
        4. After undo cycles, poison should stay cleansed
        5. After nextTurn, no poison damage
        """
        f, h, m = setup_fight

        # Shield and poison
        f.apply_shield(h, 100)
        f.apply_poison(h, 1)

        # Verify poison applied
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # Cleanse
        f.apply_shield_cleanse(h, 1)

        # Verify cleansed
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 0, "Should be 0 incoming poison"

        # Undo/redo cycle (test undo support)
        for _ in range(10):
            f.apply_shield(h, 100)
            f.undo()
            f.undo()

        # Should still be cleansed
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 0, "Should be 0 incoming poison"

        # Advance turn
        f.next_turn()

        # No poison damage
        state = f.get_state(h, Temporality.FUTURE)
        assert state.hp == h.entity_type.hp, "Should have taken 0 damage"

    def test_poison_cleanse_with_recalculate(self, setup_fight):
        """Cleanse works correctly even after state recalculation.

        Same as test_poison_cleanse but verifies cleanse persists through
        state recalculation (simulated by getting fresh state).
        """
        f, h, m = setup_fight

        # Shield and poison
        f.apply_shield(h, 100)
        f.apply_poison(h, 1)

        # Verify poison applied
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # Cleanse
        f.apply_shield_cleanse(h, 1)

        # Verify cleansed
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 0, "Should be 0 incoming poison"

        # Simulate recalculate by getting fresh state
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 0, "Should be 0 incoming poison after recalculate"

        # Advance turn
        f.next_turn()

        # No poison damage
        state = f.get_state(h, Temporality.FUTURE)
        assert state.hp == h.entity_type.hp, "Should have taken 0 damage"

    def test_poison_partial_cleanse(self, setup_fight):
        """Partial cleanse removes some but not all poison.

        2 poison - 1 cleanse = 1 poison remaining.
        After nextTurn, HP = max - 2 (1 damage + 1 projected).
        """
        f, h, m = setup_fight

        # Shield and poison(2)
        f.apply_shield(h, 100)
        f.apply_poison(h, 2)

        # Verify poison applied
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 2, "Should be 2 incoming poison"

        # Partial cleanse(1)
        f.apply_shield_cleanse(h, 1)

        # Verify partial cleanse
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # Undo/redo cycle - each iteration adds shield then undoes it
        # This tests that cleanse state remains stable during add/undo cycles
        for _ in range(10):
            f.apply_shield(h, 100)
            f.undo()

        # Should still have 1 poison
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # Advance turn
        f.next_turn()

        # HP = max - 2 (1 damage this turn + 1 projected)
        state = f.get_state(h, Temporality.FUTURE)
        assert state.hp == h.entity_type.hp - 2, "Should have taken 2 damage"

    def test_multi_poison_partial_cleanse(self, setup_fight):
        """Multiple poison applications with partial cleanse.

        2 poison + 2 poison = 4 poison (merged)
        4 poison - 1 cleanse = 3 poison
        After nextTurn, HP = max - 6 (3 damage + 3 projected)
        """
        f, h, m = setup_fight

        # Shield and multiple poison applications
        f.apply_shield(h, 100)
        f.apply_poison(h, 2)
        f.apply_poison(h, 2)

        # Verify combined poison
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 4, "Should be 4 incoming poison"

        # Partial cleanse(1)
        f.apply_shield_cleanse(h, 1)

        # Verify partial cleanse
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 3, "Should be 3 incoming poison"

        # Undo/redo cycle - each iteration adds shield then undoes it
        for _ in range(10):
            f.apply_shield(h, 100)
            f.undo()

        # Should still have 3 poison
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 3, "Should be 3 incoming poison"

        # Advance turn
        f.next_turn()

        # HP = max - 6 (3 damage this turn + 3 projected)
        state = f.get_state(h, Temporality.FUTURE)
        assert state.hp == h.entity_type.hp - 6, "Should have taken 6 damage"

    def test_multi_poison_partial_cleanse_next_turn(self, setup_fight):
        """Poison persists across turns and can be cleansed later.

        1. Apply poison(1)
        2. nextTurn - poison deals 1 damage
        3. Check poison still exists (1 stack)
        4. Cleanse removes poison
        5. Undo restores poison
        """
        f, h, m = setup_fight

        # Shield and poison
        f.apply_shield(h, 100)
        f.apply_poison(h, 1)

        # Verify poison applied
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # Advance turn
        f.next_turn()

        # Poison should still exist
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison"

        # HP = max - 2 (1 damage from Turn 1 + 1 projected from Turn 2)
        assert state.hp == h.entity_type.hp - 2, "Should be -2 hp total"

        # Cleanse on next turn
        f.apply_shield_cleanse(h, 1)

        # Verify cleansed
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 0, "Should be 0 incoming poison"

        # Undo cleanse (twice - once for shield, once for cleanse action recorded)
        f.undo()
        f.undo()

        # Poison should be back
        state = f.get_state(h, Temporality.FUTURE)
        assert state.get_poison_damage_taken() == 1, "Should be 1 incoming poison after undoing"
