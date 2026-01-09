"""Tests from TestStrangeScenarios.java - strange edge cases."""

from src.entity import Entity, EntityType, Team, FIGHTER, HEALER, GOBLIN, DRAGON
from src.fight import FightLog, Temporality


def test_cleave_hits_target_and_adjacent():
    """Cleave damage hits the target plus adjacent entities.

    Verified: Cleave 40 on middle hero (position 2) of 5 Fighters
    kills 3 heroes (positions 1, 2, 3).
    """
    heroes = [Entity(FIGHTER, Team.HERO, i) for i in range(5)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    goblin = monsters[0]
    middle_hero = heroes[2]  # Position 2 = middle of 0,1,2,3,4

    # Cleave 40 from goblin targeting middle hero (pending damage)
    fight.apply_cleave(goblin, middle_hero, amount=40, is_pending=True)

    # Should kill 3 heroes (positions 1, 2, 3)
    assert fight.count_dying_heroes() == 3

    # Verify which heroes are dying (middle 3)
    death_hash = fight.hash_death_state()
    assert death_hash.count('d') == 3
    assert death_hash.count('a') == 2
    # Edge heroes (0 and 4) should survive
    assert death_hash[0] == 'a'
    assert death_hash[4] == 'a'


def test_undo_preserves_death_state():
    """Undo returns to exact same state deterministically.

    Verified: After cleave puts 3 heroes in dying state,
    doing other actions and undoing returns to same death hash.
    """
    heroes = [Entity(FIGHTER, Team.HERO, i) for i in range(5)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    goblin = monsters[0]
    middle_hero = heroes[2]

    # Setup: cleave puts 3 heroes in dying state
    fight.apply_cleave(goblin, middle_hero, amount=40, is_pending=True)
    original_hash = fight.hash_death_state()

    # Do and undo several actions
    for _ in range(10):
        fight.apply_damage(heroes[0], goblin, amount=1, is_pending=False)
        assert fight.hash_death_state() == original_hash  # Still same dying heroes

        fight.undo()
        assert fight.hash_death_state() == original_hash  # Same after undo


def test_killing_attacker_cancels_pending_damage():
    """Killing the source of pending damage cancels that damage.

    Verified: If goblin has pending cleave damage on heroes,
    killing the goblin saves all the heroes.
    """
    heroes = [Entity(FIGHTER, Team.HERO, i) for i in range(5)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    goblin = monsters[0]
    middle_hero = heroes[2]

    # Goblin's cleave puts 3 heroes in dying state
    fight.apply_cleave(goblin, middle_hero, amount=40, is_pending=True)
    assert fight.count_dying_heroes() == 3

    # Kill the goblin (999 damage, more than its 3 HP)
    fight.apply_damage(heroes[0], goblin, amount=999, is_pending=False)

    # Goblin is dead, so its pending damage is cancelled
    assert fight.count_dying_heroes() == 0


def test_targeting_excludes_dying_heroes():
    """Enemy targeting excludes heroes that are already dying in future state.

    Verified: When an enemy makes lethal attacks, each subsequent attack
    targets a hero that isn't already doomed. 4 lethal attacks against
    4 heroes results in all 4 dying (no wasted overkill).
    """
    heroes = [Entity(HEALER, Team.HERO, i) for i in range(4)]
    monsters = [Entity(DRAGON, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    dragon = monsters[0]

    # Initially all 4 heroes are valid targets
    assert len(fight.get_valid_enemy_targets()) == 4
    assert len(fight.get_alive_heroes(Temporality.FUTURE)) == 4

    # Dragon makes 4 attacks, each targeting a valid (non-dying) hero
    for i in range(4):
        valid_targets = fight.get_valid_enemy_targets()
        assert len(valid_targets) == 4 - i, f"Should have {4-i} valid targets before attack {i+1}"

        # Pick first valid target (simulating AI choice)
        target = valid_targets[0]

        # 40 damage is lethal (Healer has 6 HP)
        fight.apply_damage(dragon, target, amount=40, is_pending=True)

    # After 4 attacks, all 4 heroes should be dying
    assert fight.count_dying_heroes() == 4
    assert len(fight.get_valid_enemy_targets()) == 0
    assert len(fight.get_alive_heroes(Temporality.FUTURE)) == 0

    # Undo all 4 attacks (8 undos since apply_damage records action)
    for _ in range(4):
        fight.undo()

    # Back to initial state
    assert fight.count_dying_heroes() == 0
    assert len(fight.get_valid_enemy_targets()) == 4


def test_pain_damages_user():
    """Pain is self-damage to the attacker when using an effect.

    Verified: Pain damages the user immediately. It can kill the user.
    Pain is a modifier on effects, not tied to dealing damage.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0), Entity(GOBLIN, Team.MONSTER, 1)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    monster = monsters[0]
    hero_max_hp = HEALER.hp  # 6

    # Hero uses dmgPain(3) - deals 3 damage to monster, takes 3 pain
    fight.apply_pain_damage(hero, monster, damage=3, pain=3)

    # Hero should be damaged by pain
    hero_state = fight.get_state(hero, Temporality.PRESENT)
    assert hero_state.hp == hero_max_hp - 3, "Hero should be damaged for 3 from pain"
    assert not hero_state.is_dead, "Hero should be alive"

    # Monster should also be damaged
    monster_state = fight.get_state(monster, Temporality.PRESENT)
    assert monster_state.hp == GOBLIN.hp - 3, "Monster should be damaged for 3"


def test_pain_can_kill_user():
    """Pain can kill the user if it exceeds their remaining HP.

    Verified: Lethal pain damage kills the attacker.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    monster = monsters[0]

    # Hero uses dmgPain(30) - way more pain than hero's 6 HP
    fight.apply_pain_damage(hero, monster, damage=30, pain=30)

    # Hero should be dead from pain
    hero_state = fight.get_state(hero, Temporality.PRESENT)
    assert hero_state.is_dead, "Hero should be dead from pain"
