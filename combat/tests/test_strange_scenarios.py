"""Tests from TestStrangeScenarios.java - strange edge cases."""

from src.entity import Entity, EntityType, Team, FIGHTER, GOBLIN
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
