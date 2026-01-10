"""Tests from TestBasicEff.java - basic effect tests."""

from src.entity import Entity, Team, FIGHTER, GOBLIN, HEALER, TEST_GOBLIN
from src.fight import FightLog, Temporality


def setup_fight() -> tuple[FightLog, Entity, Entity]:
    """Standard 1v1 fight setup: 1 Fighter vs 1 Goblin.

    Returns (fight, hero, monster) tuple for convenience.
    Matches Java's TestUtils.setupFight() default behavior.
    """
    heroes = [Entity(FIGHTER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)
    return fight, heroes[0], monsters[0]


def test_basic_sanity():
    """setupFight creates exactly 1 hero and 1 monster.

    From TestBasicEff.basicSanityTest - verifies test harness works.
    """
    fight, hero, monster = setup_fight()

    # Check alive counts in present state
    alive_heroes = fight.get_alive_heroes(Temporality.PRESENT)
    alive_monsters = fight.get_alive_monsters(Temporality.PRESENT)

    assert len(alive_heroes) == 1, "There should be 1 hero"
    assert len(alive_monsters) == 1, "There should be 1 monster"


def test_attack_enemy():
    """Attacking a monster reduces its HP by the damage amount.

    From TestBasicEff.attackEnemy - basic damage to enemy.
    """
    fight, hero, monster = setup_fight()

    # Monster should start undamaged
    monster_state = fight.get_state(monster, Temporality.FUTURE)
    assert monster_state.hp == monster_state.max_hp, "Monster should be undamaged"

    # Hero attacks monster for 1 damage (immediate, not pending)
    fight.apply_damage(hero, monster, amount=1, is_pending=False)

    # Monster should now have 1 less HP
    monster_state = fight.get_state(monster, Temporality.FUTURE)
    assert monster_state.hp == monster_state.max_hp - 1, "Monster should be damaged"


def test_attack_hero():
    """Attacking a hero reduces their HP by the damage amount.

    From TestBasicEff.attackHero - basic damage to hero.
    """
    fight, hero, monster = setup_fight()

    # Hero should start undamaged
    hero_state = fight.get_state(hero, Temporality.FUTURE)
    assert hero_state.hp == hero_state.max_hp, "Hero should be undamaged"

    # Monster attacks hero for 1 damage (immediate, not pending)
    fight.apply_damage(monster, hero, amount=1, is_pending=False)

    # Hero should now have 1 less HP
    hero_state = fight.get_state(hero, Temporality.FUTURE)
    assert hero_state.hp == hero_state.max_hp - 1, "Hero should be damaged"


def test_basic_block():
    """Shield blocks incoming damage.

    From TestBasicEff.basicBlock - shield(1) + dmg(2) = 1 damage taken, 1 blocked.
    """
    fight, hero, monster = setup_fight()

    # Give hero 1 shield
    fight.apply_shield(hero, amount=1)

    # Take 2 damage (immediate)
    fight.apply_damage(monster, hero, amount=2, is_pending=False)

    # Hero should have taken only 1 damage (2 - 1 blocked)
    hero_state = fight.get_state(hero, Temporality.FUTURE)
    assert hero_state.hp == hero_state.max_hp - 1, "Hero should take 1 damage"
    assert hero_state.damage_blocked == 1, "Hero should have blocked 1 damage"


def test_basic_heal():
    """Heal restores HP, capped at max HP.

    From TestBasicEff.basicHeal - dmg(2) + heal(3) = full HP (capped).
    """
    fight, hero, monster = setup_fight()

    # Take 2 damage
    fight.apply_damage(monster, hero, amount=2, is_pending=False)

    # Verify damaged
    hero_state = fight.get_state(hero, Temporality.FUTURE)
    assert hero_state.hp == hero_state.max_hp - 2, "Hero should be damaged"

    # Heal for 3 (more than the 2 damage)
    fight.apply_heal(hero, amount=3)

    # Hero should be back to full HP (heal capped at max)
    hero_state = fight.get_state(hero, Temporality.FUTURE)
    assert hero_state.hp == hero_state.max_hp, "Hero should be on full hp"


def test_reinforcements():
    """Large monster groups spawn in waves based on field capacity.

    From TestBasicEff.reinforcements - tests the reinforcement spawning system.

    Field capacity: 165 units
    Hero-sized (testGoblin): 24 units each
    Max on field: floor(165/24) = 6

    Reinforcements spawn immediately when room becomes available (monster dies).
    """
    # Create 30 testGoblins
    monsters = [Entity(TEST_GOBLIN, Team.MONSTER, i) for i in range(30)]
    heroes = [Entity(HEALER, Team.HERO, 0)]
    fight = FightLog(heroes, monsters)

    # Should have 3-20 goblins present initially (spec says 6 fit)
    initial_present = len(fight.get_present_monsters(Temporality.PRESENT))
    assert initial_present > 3 and initial_present < 20, \
        f"Should be a few goblins at the start, got {initial_present}"

    # More specifically: 6 should fit (6*24=144 <= 165, 7*24=168 > 165)
    assert initial_present == 6, f"Expected 6 goblins to fit, got {initial_present}"

    # Single target damage (kills one, reinforcement spawns)
    target = fight.get_present_monsters(Temporality.PRESENT)[0]
    fight.apply_damage(None, target, amount=100, is_pending=False)

    # Count should not have changed (killed 1, spawned 1)
    present_after_single = len(fight.get_present_monsters(Temporality.PRESENT))
    assert present_after_single == initial_present, \
        f"Goblins present should not have changed after single damage, got {present_after_single}"

    # Group damage (kills all present, spawns replacements)
    fight.apply_group_damage(None, amount=100, is_pending=False)

    # Count should not have changed (as long as reinforcements remain)
    present_after_group = len(fight.get_present_monsters(Temporality.PRESENT))
    assert present_after_group == initial_present, \
        f"Goblins present should not have changed after group damage, got {present_after_group}"

    # Keep dealing group damage until all 30 are dead
    # We've killed: 1 (single) + 6 (group) = 7
    # Remaining: 30 - 7 = 23
    # Each group kills 6, so need ceil(23/6) = 4 more groups
    for _ in range(8):  # Match Java test (does more than needed)
        fight.apply_group_damage(None, amount=100, is_pending=False)

    # All goblins should be dead now
    present_final = len(fight.get_present_monsters(Temporality.PRESENT))
    assert present_final == 0, f"Goblins present should be 0, got {present_final}"

    # Should be victorious
    assert fight.is_victory(Temporality.PRESENT), "Should be victorious"
