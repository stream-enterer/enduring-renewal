"""Tests from TestItem.java - item effect tests."""

from src.entity import Entity, Team, GOBLIN
from src.fight import FightLog, Temporality
from src.hero import Hero, FIGHTER_TYPE
from src.item import item_by_name, Item
from src.effects import EffectType
from src.dice import Side


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


def test_steel_heart():
    """Faint Halo grants +1 max HP when wearer is rescued.

    From TestItem.testSteelHeart - OnRescue trigger fires when hero
    transitions from dying (future HP <= 0) to surviving.
    """
    fight, hero, monster = setup_fight_with_hero()

    # Register hero for trigger system
    fight.register_hero(hero)

    # Add Faint Halo item
    hero.add_item(item_by_name("Faint Halo"))

    initial_max_hp = hero.hero_type.hp  # Fighter has 5 HP

    # Shield self while not dying - max HP shouldn't increase
    fight.apply_shield(hero.entity, amount=1)
    hero_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert hero_state.max_hp == initial_max_hp, \
        "Max HP shouldn't increase from just shielding"

    # Take lethal pending damage (more than current HP + shield)
    fight.apply_damage(monster, hero.entity, amount=initial_max_hp + 1, is_pending=True)

    # Should be dying in future state
    future_state = fight.get_state(hero.entity, Temporality.FUTURE)
    assert future_state.hp <= 0, "Should be dying"

    # Max HP should still be initial even while dying
    present_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert present_state.max_hp == initial_max_hp, \
        "Max HP shouldn't change while dying"

    # Shield self again - this should rescue (shield blocks pending damage)
    fight.apply_shield(hero.entity, amount=1)

    # Now max HP should have increased by 1 due to rescue trigger
    present_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert present_state.max_hp == initial_max_hp + 1, \
        f"Max HP should increase when rescued, got {present_state.max_hp}"


def test_pipe_and_studs_with_heal_shield():
    """Dragon Pipe and Metal Studs both apply to healShield sides.

    From TestItem.testPipeAndStudsWithHealShield - combined effect types
    match both component type conditions, so bonuses stack.

    healShield(1) + Dragon Pipe (+1 heal) + Metal Studs (+1 shield)
    = healShield(3) because HEAL_SHIELD contains both HEAL and SHIELD.
    """
    fight, hero, monster = setup_fight_with_hero()

    # Add Dragon Pipe (+1 to heal) and Metal Studs (+1 to shield)
    hero.add_item(item_by_name("Dragon Pipe"))
    hero.add_item(item_by_name("Metal Studs"))

    # Fighter has 5 HP, take 4 damage -> 1 HP
    fight.apply_damage(monster, hero.entity, amount=4, is_pending=False)
    hero_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert hero_state.hp == 1, f"Warrior should be on 1hp, got {hero_state.hp}"

    # Create a healShield side and apply item modifiers
    base_side = Side(EffectType.HEAL_SHIELD, 1)

    # Apply item modifiers - both should apply since HEAL_SHIELD contains both types
    modified_side = base_side
    for item in hero.get_items():
        modified_side = item.modify_side(modified_side)

    # Should be healShield(3) now (1 + 1 from Dragon Pipe + 1 from Metal Studs)
    assert modified_side.value == 3, \
        f"healShield should be boosted to 3, got {modified_side.value}"

    # Apply the heal_shield effect
    fight.apply_heal_shield(hero.entity, heal_amount=modified_side.value,
                            shield_amount=modified_side.value)

    # Warrior should be on 4 HP (1 + 3 heal, capped at 5)
    hero_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert hero_state.hp == 4, f"Warrior should be on 4hp, got {hero_state.hp}"

    # Warrior should have 3 shields
    assert hero_state.shield == 3, \
        f"Warrior should have 3 shields, got {hero_state.shield}"
