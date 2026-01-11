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


def test_bonus_incoming_with_heal_shields():
    """Garnet and Iron Pendant add incoming effect bonuses.

    From TestItem.testBonusIncomingWithHealShields - IncomingEffBonus items
    modify effects RECEIVED by the entity, not die sides.

    healShield(1) + Garnet (+1 incoming heal) + Iron Pendant (+1 incoming shield)
    = heal(2) + shield(2) because bonuses apply when effect is received.
    """
    fight, hero, monster = setup_fight_with_hero()

    # Register hero for incoming bonus system
    fight.register_hero(hero)

    # Add Garnet (+1 incoming heal) and Iron Pendant (+1 incoming shield)
    hero.add_item(item_by_name("Garnet"))
    hero.add_item(item_by_name("Iron Pendant"))

    # Fighter has 5 HP, take 4 damage -> 1 HP
    fight.apply_damage(monster, hero.entity, amount=4, is_pending=False)
    hero_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert hero_state.hp == 1, f"Warrior should be on 1hp, got {hero_state.hp}"

    # Apply healShield(1) - with incoming bonuses becomes heal(2) + shield(2)
    fight.apply_heal_shield(hero.entity, heal_amount=1, shield_amount=1)

    # Warrior should be on 3 HP (1 + 2 heal)
    hero_state = fight.get_state(hero.entity, Temporality.PRESENT)
    assert hero_state.hp == 3, f"Warrior should be on 3hp, got {hero_state.hp}"

    # Warrior should have 2 shields
    assert hero_state.shield == 2, \
        f"Warrior should have 2 shields, got {hero_state.shield}"


# =============================================================================
# Item-related keyword tests
# =============================================================================

class TestHoardKeyword:
    """Tests for HOARD keyword: +N pips where N = unequipped items in party."""

    def test_hoard_with_no_unequipped_items(self):
        """Hoard adds 0 pips when party has no unequipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # No unequipped items by default
        assert fight.get_unequipped_item_count() == 0

        # Set up a damage(2) side with HOARD
        hoard_side = Side(EffectType.DAMAGE, 2, {Keyword.HOARD})
        hero_die = Die()
        hero_die.set_all_sides(hoard_side)
        hero.entity.die = hero_die

        # Use die - should deal 2 damage (no bonus)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 2, \
            f"Hoard with 0 unequipped items should deal base 2 damage"

    def test_hoard_with_unequipped_items(self):
        """Hoard adds +N pips where N = number of unequipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # Add 3 unequipped items to party
        unequipped_items = [
            Item("Sword", tier=1),
            Item("Shield", tier=2),
            Item("Ring", tier=3)
        ]
        fight.set_party_unequipped_items(unequipped_items)
        assert fight.get_unequipped_item_count() == 3

        # Set up a damage(1) side with HOARD
        hoard_side = Side(EffectType.DAMAGE, 1, {Keyword.HOARD})
        hero_die = Die()
        hero_die.set_all_sides(hoard_side)
        hero.entity.die = hero_die

        # Use die - should deal 4 damage (1 + 3 from hoard)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 4, \
            f"Hoard with 3 unequipped items should deal 4 damage (1+3)"


class TestEquippedKeyword:
    """Tests for EQUIPPED keyword: +N pips where N = equipped items on me."""

    def test_equipped_with_no_items(self):
        """Equipped adds 0 pips when hero has no equipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # No items equipped by default
        assert fight.get_equipped_item_count(hero.entity) == 0

        # Set up a damage(2) side with EQUIPPED
        equipped_side = Side(EffectType.DAMAGE, 2, {Keyword.EQUIPPED})
        hero_die = Die()
        hero_die.set_all_sides(equipped_side)
        hero.entity.die = hero_die

        # Use die - should deal 2 damage (no bonus)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 2, \
            f"Equipped with 0 items should deal base 2 damage"

    def test_equipped_with_items(self):
        """Equipped adds +N pips where N = number of equipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # Equip 2 items
        hero.add_item(Item("Sword", tier=1))
        hero.add_item(Item("Ring", tier=3))
        assert fight.get_equipped_item_count(hero.entity) == 2

        # Set up a damage(1) side with EQUIPPED
        equipped_side = Side(EffectType.DAMAGE, 1, {Keyword.EQUIPPED})
        hero_die = Die()
        hero_die.set_all_sides(equipped_side)
        hero.entity.die = hero_die

        # Use die - should deal 3 damage (1 + 2 from equipped)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 3, \
            f"Equipped with 2 items should deal 3 damage (1+2)"


class TestFashionableKeyword:
    """Tests for FASHIONABLE keyword: +N pips where N = total tier of equipped items."""

    def test_fashionable_with_no_items(self):
        """Fashionable adds 0 pips when hero has no equipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # No items equipped by default
        assert fight.get_total_equipped_tier(hero.entity) == 0

        # Set up a damage(2) side with FASHIONABLE
        fashionable_side = Side(EffectType.DAMAGE, 2, {Keyword.FASHIONABLE})
        hero_die = Die()
        hero_die.set_all_sides(fashionable_side)
        hero.entity.die = hero_die

        # Use die - should deal 2 damage (no bonus)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 2, \
            f"Fashionable with 0 tier should deal base 2 damage"

    def test_fashionable_with_items(self):
        """Fashionable adds +N pips where N = total tier of equipped items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)
        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)
        monster = monsters[0]

        # Equip 2 items with tiers 2 and 3 (total 5)
        hero.add_item(Item("Sword", tier=2))
        hero.add_item(Item("Ring", tier=3))
        assert fight.get_total_equipped_tier(hero.entity) == 5

        # Set up a damage(1) side with FASHIONABLE
        fashionable_side = Side(EffectType.DAMAGE, 1, {Keyword.FASHIONABLE})
        hero_die = Die()
        hero_die.set_all_sides(fashionable_side)
        hero.entity.die = hero_die

        # Use die - should deal 6 damage (1 + 5 from fashionable)
        initial_hp = fight.get_state(monster, Temporality.PRESENT).hp
        fight.use_die(hero.entity, 0, monster)

        final_hp = fight.get_state(monster, Temporality.PRESENT).hp
        assert initial_hp - final_hp == 6, \
            f"Fashionable with 5 total tier should deal 6 damage (1+5)"

    def test_fashionable_tier_calculation(self):
        """Fashionable correctly sums up tier from multiple items."""
        from src.dice import Side, Keyword, Die

        hero = Hero(FIGHTER_TYPE, 0)

        # Equip 3 items with different tiers
        hero.add_item(Item("Common Sword", tier=1))
        hero.add_item(Item("Rare Ring", tier=3))
        hero.add_item(Item("Epic Helm", tier=5))

        monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
        fight = FightLog([hero.entity], monsters)
        fight.register_hero(hero)

        # Total tier should be 1 + 3 + 5 = 9
        assert fight.get_total_equipped_tier(hero.entity) == 9
