"""Tests for TestKeyword - keyword effects on abilities."""

from src.entity import Entity, EntityType, Team, EntitySize
from src.fight import FightLog, Temporality


def make_hero(name: str, hp: int = 5) -> Entity:
    """Create a hero entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)


def make_monster(name: str, hp: int = 4) -> Entity:
    """Create a monster entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)


class TestBloodlust:
    """Tests for Bloodlust keyword.

    Bloodlust is a damage keyword that gains +N bonus damage
    where N = number of currently damaged enemies.
    The count is evaluated at time of attack.
    Damaging a new enemy increases future bonus; killing an enemy decreases it.

    Verified: Bonus equals count of damaged enemies at time of attack.
    """

    def test_first_attack_has_no_bonus(self):
        """First bloodlust attack has no bonus (0 damaged enemies)."""
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # First attack - 0 damaged enemies, so bonus = 0
        fight.apply_bloodlust_damage(hero, monsters[0], 1)
        state = fight.get_state(monsters[0], Temporality.PRESENT)
        assert state.hp == monster_max - 1, "First attack should deal base damage (1)"

    def test_second_attack_has_bonus_one(self):
        """After damaging one enemy, bloodlust gets +1 bonus."""
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # First attack - damages monster 0
        fight.apply_bloodlust_damage(hero, monsters[0], 1)

        # Second attack - 1 damaged enemy, so bonus = +1
        fight.apply_bloodlust_damage(hero, monsters[0], 1)
        state = fight.get_state(monsters[0], Temporality.PRESENT)
        # First did 1, second did 2 (1 + 1 bonus) = total 3 damage
        assert state.hp == monster_max - 3, "Second attack should deal 1+1=2 damage (total 3)"

    def test_bonus_persists_across_targets(self):
        """Bloodlust bonus counts ALL damaged enemies, not per-target."""
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # Attack monster 0 twice (damages monster 0)
        fight.apply_bloodlust_damage(hero, monsters[0], 1)  # deals 1
        fight.apply_bloodlust_damage(hero, monsters[0], 1)  # deals 2

        # Attack monster 1 (different target, but 1 damaged enemy exists)
        fight.apply_bloodlust_damage(hero, monsters[1], 1)
        state = fight.get_state(monsters[1], Temporality.PRESENT)
        # 1 damaged enemy = +1 bonus, so deals 2
        assert state.hp == monster_max - 2, "Attack on new target should still get +1 bonus"

    def test_bonus_increases_with_more_damaged_enemies(self):
        """Bonus equals count of ALL damaged enemies."""
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # Attack monster 0 twice
        fight.apply_bloodlust_damage(hero, monsters[0], 1)  # 0 damaged -> deals 1
        fight.apply_bloodlust_damage(hero, monsters[0], 1)  # 1 damaged -> deals 2

        # Attack monster 1 (now 1 damaged enemy)
        fight.apply_bloodlust_damage(hero, monsters[1], 1)  # 1 damaged -> deals 2

        # Attack monster 2 (now 2 damaged enemies)
        fight.apply_bloodlust_damage(hero, monsters[2], 1)
        state = fight.get_state(monsters[2], Temporality.PRESENT)
        # 2 damaged enemies = +2 bonus, so deals 3
        assert state.hp == monster_max - 3, "With 2 damaged enemies, bonus should be +2"

    def test_bloodlust_full_scenario(self):
        """Full bloodlust test matching original Java test.

        Setup: 1 Healer hero vs 3 testGoblins
        - rollHit monster 0 with dmgBloodlust(1) -> monster at maxHp-1 (1 damage)
        - rollHit monster 0 with dmgBloodlust(1) -> monster at maxHp-3 (2 damage)
        - rollHit monster 1 with dmgBloodlust(1) -> monster at maxHp-2 (2 damage)
        - rollHit monster 2 with dmgBloodlust(1) -> monster at maxHp-3 (3 damage)

        Verified: Bonus equals count of damaged enemies at time of attack.
        """
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # Attack 1: dmgBloodlust(1) on monster 0
        # 0 damaged enemies -> bonus = 0 -> deals 1
        fight.apply_bloodlust_damage(hero, monsters[0], 1)
        state = fight.get_state(monsters[0], Temporality.PRESENT)
        assert state.hp == monster_max - 1, "monster should be hit for 1"

        # Attack 2: dmgBloodlust(1) on monster 0
        # 1 damaged enemy -> bonus = 1 -> deals 2
        fight.apply_bloodlust_damage(hero, monsters[0], 1)
        state = fight.get_state(monsters[0], Temporality.PRESENT)
        assert state.hp == monster_max - 3, "monster should be hit for 2 (total 3)"

        # Attack 3: dmgBloodlust(1) on monster 1
        # 1 damaged enemy (monster 0) -> bonus = 1 -> deals 2
        fight.apply_bloodlust_damage(hero, monsters[1], 1)
        state = fight.get_state(monsters[1], Temporality.PRESENT)
        assert state.hp == monster_max - 2, "monster should be hit for 2"

        # Attack 4: dmgBloodlust(1) on monster 2
        # 2 damaged enemies (monster 0 and 1) -> bonus = 2 -> deals 3
        fight.apply_bloodlust_damage(hero, monsters[2], 1)
        state = fight.get_state(monsters[2], Temporality.PRESENT)
        assert state.hp == monster_max - 3, "monster should be hit for 3"

    def test_killing_enemy_reduces_bonus(self):
        """Killing an enemy reduces the damaged enemy count.

        Verified: Killing an enemy would reduce the number by 1.
        """
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=2) for i in range(3)]  # 2 HP each

        fight = FightLog([hero], monsters)
        monster_max = 2

        # Attack monster 0 - damages it (1 damaged enemy now)
        fight.apply_bloodlust_damage(hero, monsters[0], 1)

        # Attack monster 1 - now 1 damaged enemy, so +1 bonus = 2 damage = dead
        fight.apply_bloodlust_damage(hero, monsters[1], 1)
        state = fight.get_state(monsters[1], Temporality.PRESENT)
        assert state.is_dead, "Monster 1 should be dead (1 base + 1 bonus = 2 damage)"

        # Attack monster 2 - only 1 damaged enemy (monster 0), monster 1 is DEAD not damaged
        fight.apply_bloodlust_damage(hero, monsters[2], 1)
        state = fight.get_state(monsters[2], Temporality.PRESENT)
        # 1 damaged enemy (monster 0 only - monster 1 is dead) = +1 bonus
        assert state.hp == monster_max - 2, "With 1 damaged enemy, should deal 1+1=2"

    def test_no_bonus_without_damaged_enemies(self):
        """No bonus when there are no damaged enemies."""
        hero = make_hero("Healer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(3)]

        fight = FightLog([hero], monsters)

        # Count damaged enemies
        assert fight.count_damaged_enemies() == 0, "No damaged enemies initially"

        # Damage one
        fight.apply_damage(hero, monsters[0], 1, is_pending=False)
        assert fight.count_damaged_enemies() == 1, "One damaged enemy now"

        # Damage another
        fight.apply_damage(hero, monsters[1], 1, is_pending=False)
        assert fight.count_damaged_enemies() == 2, "Two damaged enemies now"
