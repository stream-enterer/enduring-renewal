"""Tests from TestItem.java - item effect tests."""

from src.entity import Entity, Team, GOBLIN
from src.fight import FightLog, Temporality
from src.hero import Hero, FIGHTER_TYPE
from src.item import item_by_name
from src.effects import EffectType


def setup_fight_with_hero() -> tuple[FightLog, Hero, Entity]:
    """Standard fight setup with Hero class (supports items/dice).

    Returns (fight, hero, monster) tuple.
    """
    hero = Hero(FIGHTER_TYPE, 0)
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog([hero.entity], monsters)
    return fight, hero, monsters[0]


def test_gauntlet():
    """Gauntlet adds +1 damage to all damage sides.

    From TestItem.testGauntlet - items with FlatBonus modify die sides.
    Fighter's Sword(1) becomes Sword(2) with Gauntlet equipped.
    """
    fight, hero, monster = setup_fight_with_hero()

    # Add Gauntlet item
    hero.add_item(item_by_name("Gauntlet"))

    # Get effective side 0 (should be damage 2 instead of 1)
    side = hero.get_effective_side(0)
    assert side.effect_type == EffectType.DAMAGE, "Side 0 should be damage"
    assert side.value == 2, f"With Gauntlet, damage should be 2, got {side.value}"

    # Use the side to attack monster
    fight.apply_damage(hero.entity, monster, amount=side.value, is_pending=False)

    # Monster should be damaged for 2
    monster_state = fight.get_state(monster, Temporality.FUTURE)
    assert monster_state.hp == monster_state.max_hp - 2, \
        f"Monster should be damaged for 2, hp={monster_state.hp}, max={monster_state.max_hp}"
