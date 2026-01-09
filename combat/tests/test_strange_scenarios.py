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


def test_max_hp_modification():
    """Max HP can be modified during combat, cumulatively.

    Verified: Effects can increase or decrease max HP.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    start_max_hp = fight.get_state(hero, Temporality.PRESENT).max_hp  # 6

    # Increase max HP by 1
    fight.modify_max_hp(hero, +1)
    assert fight.get_state(hero, Temporality.PRESENT).max_hp == start_max_hp + 1

    # Decrease max HP by 4 (net -3 from start)
    fight.modify_max_hp(hero, -4)
    assert fight.get_state(hero, Temporality.PRESENT).max_hp == start_max_hp - 3


def test_max_hp_floor_at_one():
    """Max HP cannot go below 1, even with extreme reductions.

    Verified: Max HP stays at minimum 1.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    start_max_hp = fight.get_state(hero, Temporality.PRESENT).max_hp  # 6

    # Reduce by exactly start_max_hp (would be 0, clamped to 1)
    fight.modify_max_hp(hero, -start_max_hp)
    assert fight.get_state(hero, Temporality.PRESENT).max_hp == 1

    # Reduce by another 400 (would be -399, still clamped to 1)
    fight.modify_max_hp(hero, -400)
    assert fight.get_state(hero, Temporality.PRESENT).max_hp == 1


def test_shield_repel_spiky_interaction():
    """Tests Shield+Repel+Spiky interaction.

    Verified:
    - Spiky deals damage back when entity is hit
    - Repel reflects pending damage back to attacker
    - Shield blocks Spiky return damage

    Scenario: Hero at 1 HP with lethal pending damage from monster.
    Monster has Spiky(1). Hero uses shieldRepel(1).
    Result: Hero survives (shield blocks Spiky), monster takes 1 damage.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    monster = monsters[0]

    # Give monster Spiky(1)
    fight.apply_buff_spiky(monster, 1)
    assert fight.get_state(monster, Temporality.PRESENT).spiky == 1

    # Damage hero to 1 HP
    hero_max = HEALER.hp  # 6
    fight.apply_damage(monster, hero, hero_max - 1, is_pending=False)
    assert fight.get_state(hero, Temporality.PRESENT).hp == 1

    # Monster deals 3 pending damage to hero (lethal)
    fight.apply_damage(monster, hero, 3, is_pending=True)
    assert fight.get_state(hero, Temporality.PRESENT).hp == 1  # Present unchanged
    assert fight.get_state(hero, Temporality.FUTURE).is_dead  # Will die

    # Hero uses shieldRepel(1)
    fight.apply_shield_repel(hero, 1)

    # Hero should still be at 1 HP (shield absorbed Spiky's return damage)
    assert fight.get_state(hero, Temporality.PRESENT).hp == 1

    # Monster should be damaged by 1 (repel reflected 1 damage)
    assert fight.get_state(monster, Temporality.PRESENT).hp == GOBLIN.hp - 1


def test_selfheal_negates_pain():
    """selfHeal buff negates pain damage, keeping hero at full HP.

    Verified: selfHeal prevents pain from reducing HP.
    """
    heroes = [Entity(HEALER, Team.HERO, 0)]
    monsters = [Entity(GOBLIN, Team.MONSTER, 0), Entity(GOBLIN, Team.MONSTER, 1)]
    fight = FightLog(heroes, monsters)

    hero = heroes[0]
    monster = monsters[0]
    hero_max_hp = HEALER.hp  # 6

    # Give hero selfHeal buff
    fight.apply_buff_self_heal(hero)
    assert fight.get_state(hero, Temporality.PRESENT).self_heal is True

    # Hero uses dmgPain(3) - normally takes 3 self-damage
    fight.apply_pain_damage(hero, monster, damage=3, pain=3)

    # Hero should NOT be damaged (selfHeal negates pain)
    assert fight.get_state(hero, Temporality.PRESENT).hp == hero_max_hp
    assert not fight.get_state(hero, Temporality.PRESENT).is_dead

    # Even massive pain doesn't hurt
    fight.apply_pain_damage(hero, monster, damage=30, pain=30)
    assert fight.get_state(hero, Temporality.PRESENT).hp == hero_max_hp
    assert not fight.get_state(hero, Temporality.PRESENT).is_dead
