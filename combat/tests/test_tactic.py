"""Tests for tactic system infrastructure."""

import pytest
from src.entity import Entity, EntityType, EntitySize, Team
from src.effects import EffectType
from src.dice import Keyword, Side, Die
from src.tactic import Tactic, TacticCost, TacticCostType
from src.spell import SpellEffect
from src.fight import FightLog, Temporality


def make_hero(name: str, hp: int = 5) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)


def make_monster(name: str, hp: int = 4) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)


def make_die_with_side(effect_type: EffectType, value: int, keywords: set = None) -> Die:
    """Create a die with all sides set to the specified side."""
    die = Die()
    side = Side(effect_type, value, keywords or set())
    die.set_all_sides(side)
    return die


def make_damage_tactic(name: str, costs: list[TacticCostType], damage: int) -> Tactic:
    """Create a damage tactic for testing."""
    return Tactic(
        name=name,
        cost=TacticCost(costs),
        effect=SpellEffect(
            effect_type=EffectType.DAMAGE,
            value=damage,
            target_friendly=False
        )
    )


def make_heal_tactic(name: str, costs: list[TacticCostType], heal: int) -> Tactic:
    """Create a heal tactic for testing."""
    return Tactic(
        name=name,
        cost=TacticCost(costs),
        effect=SpellEffect(
            effect_type=EffectType.HEAL,
            value=heal,
            target_friendly=True
        )
    )


class TestTacticCostType:
    """Test TacticCostType enum and validation."""

    def test_basic_sword_matches_damage(self):
        """BASIC_SWORD matches damage effect type."""
        side = Side(EffectType.DAMAGE, 2)
        assert TacticCostType.BASIC_SWORD.is_valid(side)

    def test_basic_sword_matches_damage_keyword(self):
        """BASIC_SWORD matches side with damage keyword."""
        side = Side(EffectType.HEAL, 2, {Keyword.DAMAGE})
        assert TacticCostType.BASIC_SWORD.is_valid(side)

    def test_basic_sword_no_match_heal(self):
        """BASIC_SWORD doesn't match heal side."""
        side = Side(EffectType.HEAL, 2)
        assert not TacticCostType.BASIC_SWORD.is_valid(side)

    def test_basic_shield_matches_shield(self):
        """BASIC_SHIELD matches shield effect type."""
        side = Side(EffectType.SHIELD, 2)
        assert TacticCostType.BASIC_SHIELD.is_valid(side)

    def test_basic_shield_matches_heal_shield(self):
        """BASIC_SHIELD matches heal_shield effect type."""
        side = Side(EffectType.HEAL_SHIELD, 2)
        assert TacticCostType.BASIC_SHIELD.is_valid(side)

    def test_basic_shield_matches_self_shield_keyword(self):
        """BASIC_SHIELD matches side with selfShield keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.SELF_SHIELD})
        assert TacticCostType.BASIC_SHIELD.is_valid(side)

    def test_basic_shield_matches_shield_keyword(self):
        """BASIC_SHIELD matches side with shield keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.SHIELD})
        assert TacticCostType.BASIC_SHIELD.is_valid(side)

    def test_basic_heal_matches_heal(self):
        """BASIC_HEAL matches heal effect type."""
        side = Side(EffectType.HEAL, 2)
        assert TacticCostType.BASIC_HEAL.is_valid(side)

    def test_basic_heal_matches_heal_shield(self):
        """BASIC_HEAL matches heal_shield effect type."""
        side = Side(EffectType.HEAL_SHIELD, 2)
        assert TacticCostType.BASIC_HEAL.is_valid(side)

    def test_basic_heal_matches_self_heal_keyword(self):
        """BASIC_HEAL matches side with selfHeal keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.SELF_HEAL})
        assert TacticCostType.BASIC_HEAL.is_valid(side)

    def test_basic_heal_matches_heal_keyword(self):
        """BASIC_HEAL matches side with heal keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.HEAL})
        assert TacticCostType.BASIC_HEAL.is_valid(side)

    def test_basic_mana_matches_mana(self):
        """BASIC_MANA matches mana effect type."""
        side = Side(EffectType.MANA, 2)
        assert TacticCostType.BASIC_MANA.is_valid(side)

    def test_basic_mana_matches_mana_keyword(self):
        """BASIC_MANA matches side with manaGain keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.MANA})
        assert TacticCostType.BASIC_MANA.is_valid(side)

    def test_wild_matches_any_pip(self):
        """WILD matches any side with a value."""
        assert TacticCostType.WILD.is_valid(Side(EffectType.DAMAGE, 2))
        assert TacticCostType.WILD.is_valid(Side(EffectType.HEAL, 1))
        assert TacticCostType.WILD.is_valid(Side(EffectType.SHIELD, 3))
        assert TacticCostType.WILD.is_valid(Side(EffectType.MANA, 4))

    def test_wild_no_match_blank(self):
        """WILD doesn't match blank side."""
        side = Side(EffectType.BLANK, 0)
        assert not TacticCostType.WILD.is_valid(side)

    def test_blank_matches_blank(self):
        """BLANK matches blank effect type."""
        side = Side(EffectType.BLANK, 0)
        assert TacticCostType.BLANK.is_valid(side)

    def test_blank_no_match_damage(self):
        """BLANK doesn't match damage side."""
        side = Side(EffectType.DAMAGE, 2)
        assert not TacticCostType.BLANK.is_valid(side)

    def test_pips_1_matches_1_pip(self):
        """PIPS_1 matches sides with exactly 1 pip."""
        side = Side(EffectType.DAMAGE, 1)
        assert TacticCostType.PIPS_1.is_valid(side)

    def test_pips_1_no_match_2_pip(self):
        """PIPS_1 doesn't match 2-pip side."""
        side = Side(EffectType.DAMAGE, 2)
        assert not TacticCostType.PIPS_1.is_valid(side)

    def test_pips_2_matches_2_pip(self):
        """PIPS_2 matches sides with exactly 2 pips."""
        side = Side(EffectType.DAMAGE, 2)
        assert TacticCostType.PIPS_2.is_valid(side)

    def test_pips_3_matches_3_pip(self):
        """PIPS_3 matches sides with exactly 3 pips."""
        side = Side(EffectType.DAMAGE, 3)
        assert TacticCostType.PIPS_3.is_valid(side)

    def test_pips_4_matches_4_pip(self):
        """PIPS_4 matches sides with exactly 4 pips."""
        side = Side(EffectType.DAMAGE, 4)
        assert TacticCostType.PIPS_4.is_valid(side)

    def test_keyword_matches_1_keyword(self):
        """KEYWORD matches sides with exactly 1 keyword."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.GROWTH})
        assert TacticCostType.KEYWORD.is_valid(side)

    def test_keyword_no_match_2_keywords(self):
        """KEYWORD doesn't match sides with 2 keywords."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.GROWTH, Keyword.ENGAGE})
        assert not TacticCostType.KEYWORD.is_valid(side)

    def test_two_keywords_matches_2_keywords(self):
        """TWO_KEYWORDS matches sides with exactly 2 keywords."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.GROWTH, Keyword.ENGAGE})
        assert TacticCostType.TWO_KEYWORDS.is_valid(side)

    def test_four_keywords_matches_4_keywords(self):
        """FOUR_KEYWORDS matches sides with exactly 4 keywords."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.GROWTH, Keyword.ENGAGE, Keyword.PAIR, Keyword.CHAIN})
        assert TacticCostType.FOUR_KEYWORDS.is_valid(side)

    def test_pippy_returns_true_for_basic_types(self):
        """pippy is True for basic cost types."""
        assert TacticCostType.BASIC_SWORD.pippy
        assert TacticCostType.BASIC_SHIELD.pippy
        assert TacticCostType.BASIC_HEAL.pippy
        assert TacticCostType.BASIC_MANA.pippy
        assert TacticCostType.WILD.pippy

    def test_pippy_returns_false_for_other_types(self):
        """pippy is False for non-basic cost types."""
        assert not TacticCostType.BLANK.pippy
        assert not TacticCostType.PIPS_1.pippy
        assert not TacticCostType.KEYWORD.pippy

    def test_get_valid_types(self):
        """get_valid_types returns all matching cost types."""
        side = Side(EffectType.DAMAGE, 2, {Keyword.GROWTH})
        valid_types = TacticCostType.get_valid_types(side)

        assert TacticCostType.BASIC_SWORD in valid_types
        assert TacticCostType.WILD in valid_types
        assert TacticCostType.PIPS_2 in valid_types
        assert TacticCostType.KEYWORD in valid_types


class TestTacticCost:
    """Test TacticCost fulfillment logic."""

    def test_empty_cost_always_usable(self):
        """Empty cost is always usable."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        cost = TacticCost([])
        assert cost.is_usable(fight)

    def test_single_sword_cost_fulfilled(self):
        """Single sword cost can be fulfilled by damage die."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero], [])

        cost = TacticCost([TacticCostType.BASIC_SWORD])
        assert cost.is_usable(fight)

    def test_single_sword_cost_not_fulfilled_by_heal(self):
        """Single sword cost cannot be fulfilled by heal die."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.HEAL, 2)
        fight = FightLog([hero], [])

        cost = TacticCost([TacticCostType.BASIC_SWORD])
        assert not cost.is_usable(fight)

    def test_pippy_cost_accumulates_by_value(self):
        """Pippy cost types accumulate by die value."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 3)  # 3 pips
        fight = FightLog([hero], [])

        # Cost of 3 swords should be fulfilled by one 3-pip damage die
        cost = TacticCost([TacticCostType.BASIC_SWORD] * 3)
        assert cost.is_usable(fight)

    def test_pippy_cost_not_enough_pips(self):
        """Pippy cost fails when not enough pips."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)  # 2 pips
        fight = FightLog([hero], [])

        # Cost of 3 swords cannot be fulfilled by one 2-pip damage die
        cost = TacticCost([TacticCostType.BASIC_SWORD] * 3)
        assert not cost.is_usable(fight)

    def test_multiple_dice_fulfill_cost(self):
        """Multiple dice can fulfill larger costs."""
        hero1 = make_hero("Fighter", hp=10)
        hero2 = make_hero("Cleric", hp=10)
        hero1.die = make_die_with_side(EffectType.DAMAGE, 2)  # 2 pips
        hero2.die = make_die_with_side(EffectType.DAMAGE, 2)  # 2 pips
        fight = FightLog([hero1, hero2], [])

        # Cost of 4 swords fulfilled by two 2-pip damage dice
        cost = TacticCost([TacticCostType.BASIC_SWORD] * 4)
        assert cost.is_usable(fight)

    def test_used_die_not_counted(self):
        """Used dice are not counted for tactic costs."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero], [])

        # Mark die as used
        fight.mark_die_used(hero)

        cost = TacticCost([TacticCostType.BASIC_SWORD])
        assert not cost.is_usable(fight)

    def test_contributing_entities(self):
        """get_contributing_entities returns correct entities."""
        hero1 = make_hero("Fighter", hp=10)
        hero2 = make_hero("Cleric", hp=10)
        hero1.die = make_die_with_side(EffectType.DAMAGE, 2)
        hero2.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero1, hero2], [])

        # Cost that only needs one die
        cost = TacticCost([TacticCostType.BASIC_SWORD])
        contributors = cost.get_contributing_entities(fight)

        # Should only include first hero (greedy algorithm)
        assert len(contributors) == 1
        assert contributors[0] == hero1


class TestTactical:
    """Test tactical keyword - counts twice for tactic costs."""

    def test_tactical_doubles_contribution(self):
        """tactical keyword doubles the pip contribution."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2, {Keyword.TACTICAL})  # 2 pips * 2 = 4
        fight = FightLog([hero], [])

        # Cost of 4 swords should be fulfilled by one 2-pip tactical damage die
        cost = TacticCost([TacticCostType.BASIC_SWORD] * 4)
        assert cost.is_usable(fight)

    def test_tactical_doubles_non_pippy_too(self):
        """tactical keyword also doubles non-pippy contributions."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.BLANK, 0, {Keyword.TACTICAL})  # 1 * 2 = 2
        fight = FightLog([hero], [])

        # Cost of 2 blanks should be fulfilled by one tactical blank die
        cost = TacticCost([TacticCostType.BLANK] * 2)
        assert cost.is_usable(fight)

    def test_tactical_without_keyword_normal_contribution(self):
        """Without tactical, contribution is normal."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)  # 2 pips, no tactical
        fight = FightLog([hero], [])

        # Cost of 4 swords cannot be fulfilled by 2-pip damage die without tactical
        cost = TacticCost([TacticCostType.BASIC_SWORD] * 4)
        assert not cost.is_usable(fight)


class TestFightLogTacticMethods:
    """Test FightLog tactic infrastructure."""

    def test_is_tactic_usable(self):
        """is_tactic_usable returns True when dice can fulfill costs."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero], [monster])

        tactic = make_damage_tactic("Strike", [TacticCostType.BASIC_SWORD], 3)
        assert fight.is_tactic_usable(tactic)

    def test_is_tactic_usable_false_when_not_enough(self):
        """is_tactic_usable returns False when dice can't fulfill costs."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.HEAL, 2)  # Wrong type
        fight = FightLog([hero], [monster])

        tactic = make_damage_tactic("Strike", [TacticCostType.BASIC_SWORD], 3)
        assert not fight.is_tactic_usable(tactic)

    def test_use_tactic_marks_dice_used(self):
        """use_tactic marks contributing dice as used."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero], [monster])

        tactic = make_damage_tactic("Strike", [TacticCostType.BASIC_SWORD], 3)
        result = fight.use_tactic(tactic, monster)

        assert result is True
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.is_used()

    def test_use_tactic_deals_damage(self):
        """use_tactic applies damage effect."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero], [monster])

        tactic = make_damage_tactic("Strike", [TacticCostType.BASIC_SWORD], 3)
        fight.use_tactic(tactic, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 2  # 5 - 3 = 2

    def test_use_tactic_heals(self):
        """use_tactic applies heal effect."""
        hero = make_hero("Fighter", hp=10)
        hero.die = make_die_with_side(EffectType.HEAL, 2)
        fight = FightLog([hero], [])

        # Damage hero first
        fight.apply_damage(hero, hero, 4)

        tactic = make_heal_tactic("Restore", [TacticCostType.BASIC_HEAL], 3)
        fight.use_tactic(tactic, hero)

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 9  # 10 - 4 + 3 = 9

    def test_use_tactic_fails_when_not_usable(self):
        """use_tactic returns False when tactic cannot be used."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.HEAL, 2)  # Wrong type
        fight = FightLog([hero], [monster])

        tactic = make_damage_tactic("Strike", [TacticCostType.BASIC_SWORD], 3)
        result = fight.use_tactic(tactic, monster)

        assert result is False
        # Die should not be marked as used
        state = fight.get_state(hero, Temporality.PRESENT)
        assert not state.is_used()

    def test_use_tactic_with_tactical_keyword(self):
        """use_tactic with tactical keyword correctly doubles contribution."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        hero.die = make_die_with_side(EffectType.DAMAGE, 2, {Keyword.TACTICAL})
        fight = FightLog([hero], [monster])

        # Cost of 4 swords - needs 4 pips, but tactical die gives 4 (2*2)
        tactic = make_damage_tactic("Mighty Strike", [TacticCostType.BASIC_SWORD] * 4, 5)
        result = fight.use_tactic(tactic, monster)

        assert result is True
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 0  # 5 - 5 = 0

    def test_use_tactic_multiple_dice(self):
        """use_tactic can consume multiple dice for larger costs."""
        hero1 = make_hero("Fighter", hp=10)
        hero2 = make_hero("Cleric", hp=10)
        monster = make_monster("Goblin", hp=10)
        hero1.die = make_die_with_side(EffectType.DAMAGE, 2)
        hero2.die = make_die_with_side(EffectType.DAMAGE, 2)
        fight = FightLog([hero1, hero2], [monster])

        # Cost of 4 swords - needs both dice
        tactic = make_damage_tactic("Team Strike", [TacticCostType.BASIC_SWORD] * 4, 6)
        result = fight.use_tactic(tactic, monster)

        assert result is True
        # Both dice should be used
        state1 = fight.get_state(hero1, Temporality.PRESENT)
        state2 = fight.get_state(hero2, Temporality.PRESENT)
        assert state1.is_used()
        assert state2.is_used()


class TestTacticCostFromType:
    """Test TacticCost factory methods."""

    def test_from_types(self):
        """from_types creates cost from list of types."""
        cost = TacticCost.from_types(TacticCostType.BASIC_SWORD, TacticCostType.BASIC_HEAL)
        assert len(cost.costs) == 2
        assert cost.costs[0] == TacticCostType.BASIC_SWORD
        assert cost.costs[1] == TacticCostType.BASIC_HEAL

    def test_from_type_count(self):
        """from_type_count creates cost with repeated type."""
        cost = TacticCost.from_type_count(TacticCostType.BASIC_SWORD, 3)
        assert len(cost.costs) == 3
        assert all(c == TacticCostType.BASIC_SWORD for c in cost.costs)
