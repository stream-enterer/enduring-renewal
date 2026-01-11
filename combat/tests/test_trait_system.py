"""Tests for the trait system and dispel keyword.

The trait system allows entities to have permanent Personal triggers (traits)
that are active unless disabled by a TraitsRemoved buff (from dispel).

Tests verify:
1. Traits are included in get_active_personals()
2. TraitsRemoved buff disables traits
3. Dispel keyword applies TraitsRemoved buff
4. Dispel has no effect on entities without traits
5. Multiple dispels don't stack (singular buff)
"""

import pytest

from src.entity import Entity, EntityType, Team, EntitySize
from src.dice import Die, Side, Keyword
from src.effects import EffectType
from src.fight import FightLog, Temporality
from src.triggers import (
    AffectSides, Buff, Personal, FlatBonus, AddKeyword, TraitsRemoved
)


# Test entity types
TEST_HERO = EntityType("TestHero", 10, EntitySize.HERO)
TEST_MONSTER = EntityType("TestMonster", 5, EntitySize.HERO)


def create_basic_die():
    """Create a basic die with 6 damage sides."""
    die = Die()
    # Use set_all_sides first to initialize, then can set individual sides
    die.set_all_sides(Side(EffectType.DAMAGE, 1))
    return die


def create_dispel_die():
    """Create a die with dispel keyword on first side."""
    die = Die()
    # Initialize all sides first
    die.set_all_sides(Side(EffectType.DAMAGE, 1))
    # First side: dispel (damage + dispel keyword)
    die.set_side(0, Side(EffectType.DAMAGE, 1, {Keyword.DISPEL}))
    return die


class SimpleBoostTrait(Personal):
    """A simple trait that adds +1 to all sides."""

    def __init__(self, bonus: int = 1):
        self.bonus = bonus

    def affect_side(self, side_state, owner, trigger_index):
        side_state.add_value(self.bonus)

    def get_priority(self) -> float:
        # Traits typically have lower priority (run earlier)
        return -10.0


class TestTraitBasics:
    """Test basic trait functionality."""

    def test_traits_included_in_personals(self):
        """Traits are included in get_active_personals()."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        trait = SimpleBoostTrait(2)
        hero.traits = [trait]

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        personals = state.get_active_personals()
        assert trait in personals

    def test_trait_modifies_sides(self):
        """Traits modify die sides."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        trait = SimpleBoostTrait(2)
        hero.traits = [trait]

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        # All sides should have +2 value from trait
        for i in range(6):
            side_state = state.get_side_state(i, fight)
            # Base 1 + trait bonus 2 = 3
            assert side_state.value == 3, f"Side {i} should have value 3"

    def test_entity_without_traits(self):
        """Entity without traits has empty traits list."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        # No traits assigned

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        # Should have no personals (no buffs, no traits)
        personals = state.get_active_personals()
        assert len(personals) == 0


class TestTraitsRemoved:
    """Test TraitsRemoved buff functionality."""

    def test_traits_removed_disables_traits(self):
        """TraitsRemoved buff disables entity traits."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        trait = SimpleBoostTrait(2)
        hero.traits = [trait]

        fight = FightLog([hero], [])

        # Add TraitsRemoved buff
        state = fight.get_state(hero, Temporality.PRESENT)
        traits_removed = TraitsRemoved()
        state.add_buff(Buff(personal=traits_removed))

        # Trait should not be in personals anymore
        personals = state.get_active_personals()
        assert trait not in personals
        # But TraitsRemoved should be there
        assert traits_removed in personals

    def test_traits_removed_sides_unmodified(self):
        """Sides are unmodified when traits are removed."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        trait = SimpleBoostTrait(2)
        hero.traits = [trait]

        fight = FightLog([hero], [])

        # Add TraitsRemoved buff
        state = fight.get_state(hero, Temporality.PRESENT)
        state.add_buff(Buff(personal=TraitsRemoved()))

        # Sides should have base value (no trait bonus)
        for i in range(6):
            side_state = state.get_side_state(i, fight)
            assert side_state.value == 1, f"Side {i} should have base value 1"

    def test_traits_removed_is_singular(self):
        """Multiple TraitsRemoved buffs merge into one."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        hero.traits = [SimpleBoostTrait()]

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        # Add TraitsRemoved twice
        state.add_buff(Buff(personal=TraitsRemoved()))
        state.add_buff(Buff(personal=TraitsRemoved()))

        # Should only have one TraitsRemoved buff
        traits_removed_count = sum(
            1 for buff in state.buffs
            if isinstance(buff.personal, TraitsRemoved)
        )
        assert traits_removed_count == 1

    def test_allow_traits_method(self):
        """_allow_traits() returns False when TraitsRemoved is present."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        hero.traits = [SimpleBoostTrait()]

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        # Before TraitsRemoved
        assert state._allow_traits() is True

        # After TraitsRemoved
        state.add_buff(Buff(personal=TraitsRemoved()))
        assert state._allow_traits() is False


class TestDispelKeyword:
    """Test the dispel keyword implementation."""

    def test_dispel_removes_traits(self):
        """Dispel keyword removes traits from target."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_dispel_die()

        # Monster with a trait
        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        trait = SimpleBoostTrait(3)
        monster.traits = [trait]

        fight = FightLog([hero], [monster])

        # Before dispel: monster's trait is active
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        side_state = monster_state.get_side_state(0, fight)
        assert side_state.value == 4  # base 1 + trait 3

        # Hero uses dispel on monster
        fight.use_die(hero, 0, monster)

        # After dispel: monster's trait is disabled
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        side_state = monster_state.get_side_state(0, fight)
        assert side_state.value == 1  # base 1, no trait

    def test_dispel_does_nothing_to_traitless_entity(self):
        """Dispel has no effect on entities without traits."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_dispel_die()

        # Monster without traits
        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        # No traits assigned

        fight = FightLog([hero], [monster])

        # Hero uses dispel on monster
        fight.use_die(hero, 0, monster)

        # Monster should have no TraitsRemoved buff (since it had no traits)
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        has_traits_removed = any(
            isinstance(buff.personal, TraitsRemoved)
            for buff in monster_state.buffs
        )
        assert not has_traits_removed

    def test_dispel_permanent_effect(self):
        """Dispel effect is permanent for the fight."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_dispel_die()

        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        monster.traits = [SimpleBoostTrait(2)]

        fight = FightLog([hero], [monster])
        fight.use_die(hero, 0, monster)

        # Check TraitsRemoved buff is permanent (turns_remaining is None)
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        for buff in monster_state.buffs:
            if isinstance(buff.personal, TraitsRemoved):
                assert buff.turns_remaining is None

        # Advance multiple turns - effect should persist
        for _ in range(3):
            fight.next_turn()

        # Trait should still be disabled
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        side_state = monster_state.get_side_state(0, fight)
        assert side_state.value == 1  # Base value, no trait bonus

    def test_multiple_dispels_dont_stack(self):
        """Multiple dispels on same target don't stack."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_dispel_die()

        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        monster.traits = [SimpleBoostTrait()]

        fight = FightLog([hero], [monster])

        # Reset hero die usage to use dispel multiple times
        fight.use_die(hero, 0, monster)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        # Mark die as unused to allow another use
        fight.next_turn()
        fight.use_die(hero, 0, monster)

        # Should still only have one TraitsRemoved
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        traits_removed_count = sum(
            1 for buff in monster_state.buffs
            if isinstance(buff.personal, TraitsRemoved)
        )
        assert traits_removed_count == 1

    def test_dispel_applies_damage_and_removes_traits(self):
        """Dispel side applies both damage and trait removal."""
        hero = Entity(TEST_HERO, Team.HERO)
        # 3 damage dispel
        dispel_side = Side(EffectType.DAMAGE, 3, {Keyword.DISPEL})
        hero.die = Die()
        hero.die.set_all_sides(Side(EffectType.BLANK, 0))
        hero.die.set_side(0, dispel_side)

        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        monster.traits = [SimpleBoostTrait(2)]

        fight = FightLog([hero], [monster])

        # Monster starts with 5 HP
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 5

        fight.use_die(hero, 0, monster)

        # Monster should have taken 3 damage AND lost traits
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 2  # 5 - 3
        assert not monster_state._allow_traits()


class TestTraitWithOtherBuffs:
    """Test trait interactions with other buffs."""

    def test_trait_and_buff_both_apply(self):
        """Both traits and buffs apply to sides."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_basic_die()
        trait = SimpleBoostTrait(1)
        hero.traits = [trait]

        fight = FightLog([hero], [])
        state = fight.get_state(hero, Temporality.PRESENT)

        # Add a buff that gives +2
        buff_personal = AffectSides([], [FlatBonus(2)])
        state.add_buff(Buff(personal=buff_personal))

        # Sides should have base + trait + buff = 1 + 1 + 2 = 4
        for i in range(6):
            side_state = state.get_side_state(i, fight)
            assert side_state.value == 4

    def test_dispel_removes_traits_but_not_buffs(self):
        """Dispel removes traits but not other buffs."""
        hero = Entity(TEST_HERO, Team.HERO)
        hero.die = create_dispel_die()

        monster = Entity(TEST_MONSTER, Team.MONSTER)
        monster.die = create_basic_die()
        monster.traits = [SimpleBoostTrait(3)]

        fight = FightLog([hero], [monster])

        # Add a buff to monster
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        buff_personal = AffectSides([], [FlatBonus(2)])
        monster_state.add_buff(Buff(personal=buff_personal))

        # Before dispel: trait + buff active
        side_state = monster_state.get_side_state(0, fight)
        assert side_state.value == 6  # base 1 + trait 3 + buff 2

        # Use dispel
        fight.use_die(hero, 0, monster)

        # After dispel: only buff active (trait disabled)
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        side_state = monster_state.get_side_state(0, fight)
        assert side_state.value == 3  # base 1 + buff 2 (no trait)
