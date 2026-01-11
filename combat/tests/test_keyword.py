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


class TestPoison:
    """Tests for Poison keyword.

    Poison is a damage keyword that deals immediate damage AND adds
    pending damage equal to the poison amount.

    Example: dmgPoison(1) deals 1 damage now and 1 more at turn end.

    Verified: Confirmed.
    """

    def test_poison_deals_immediate_damage(self):
        """Poison deals immediate damage to present state."""
        hero = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # Poison(1) should deal 1 immediate damage
        fight.apply_poison_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 1, "Poison should deal immediate damage"

    def test_poison_adds_pending_damage(self):
        """Poison adds pending damage equal to poison amount."""
        hero = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # Poison(1) should deal 1 now + 1 pending
        fight.apply_poison_damage(hero, monster, 1)

        # Present: 1 damage taken
        present = fight.get_state(monster, Temporality.PRESENT)
        assert present.hp == monster_max - 1, "Present should show immediate damage"

        # Future: 2 damage total (1 immediate + 1 pending)
        future = fight.get_state(monster, Temporality.FUTURE)
        assert future.hp == monster_max - 2, "Future should show poison pending damage"

    def test_poison_full_scenario(self):
        """Full poison test matching original Java test.

        Setup: 1 hero vs 1 goblin (4 HP)
        - dmgPoison(1) on monster
        - Present HP: maxHp - 1 (immediate damage)
        - Future HP: maxHp - 2 (immediate + poison pending)

        Verified: Confirmed.
        """
        hero = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # Apply poison damage
        fight.apply_poison_damage(hero, monster, 1)

        # Check present state - should be damaged for 1
        present = fight.get_state(monster, Temporality.PRESENT)
        assert present.hp == monster_max - 1, "should be damaged for 1"

        # Check future state - should be damaged by poison (total 2)
        future = fight.get_state(monster, Temporality.FUTURE)
        assert future.hp == monster_max - 2, "should be damaged by poison"

    def test_poison_stacks(self):
        """Multiple poison applications stack pending damage."""
        hero = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=6)

        fight = FightLog([hero], [monster])
        monster_max = 6

        # First poison(1): 1 immediate + 1 pending
        fight.apply_poison_damage(hero, monster, 1)

        # Second poison(1): 1 more immediate + 1 more pending
        fight.apply_poison_damage(hero, monster, 1)

        # Present: 2 immediate damage
        present = fight.get_state(monster, Temporality.PRESENT)
        assert present.hp == monster_max - 2, "Should have 2 immediate damage"

        # Future: 4 total (2 immediate + 2 pending)
        future = fight.get_state(monster, Temporality.FUTURE)
        assert future.hp == monster_max - 4, "Should have 4 total damage (stacked poison)"


class TestEngage:
    """Tests for Engage keyword.

    Engage deals x2 damage against targets at full HP.
    Once they're damaged (HP < maxHP), no multiplier applies.

    Verified: Confirmed (from keywords.csv: "x2 vs targets with full hp").
    """

    def test_engage_doubles_damage_at_full_hp(self):
        """Engage deals x2 damage when target is at full HP."""
        hero = make_hero("Fencer", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # Target at full HP -> x2 damage
        fight.apply_engage_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 2, "Engage should deal x2 damage vs full HP target"

    def test_engage_no_bonus_when_damaged(self):
        """Engage deals normal damage when target is not at full HP."""
        hero = make_hero("Fencer", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # First attack at full HP -> x2 = 2 damage
        fight.apply_engage_damage(hero, monster, 1)

        # Second attack - target now damaged -> no multiplier
        fight.apply_engage_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        # 2 + 1 = 3 total damage
        assert state.hp == monster_max - 3, "Engage should deal normal damage vs damaged target"

    def test_engage_full_scenario(self):
        """Full engage test matching original Java test.

        Setup: 1 hero vs 1 goblin (4 HP)
        - dmgEngage(1) -> monster at maxHp-2 (x2 damage)
        - dmgEngage(1) -> monster at maxHp-3 (normal damage)

        Verified: Confirmed.
        """
        hero = make_hero("Fencer", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        monster_max = 4

        # First engage attack - target at full HP
        fight.apply_engage_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 2, "should be damaged for 2"

        # Second engage attack - target no longer at full HP
        fight.apply_engage_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 3, "should be damaged for 3 total"

    def test_engage_vs_different_targets(self):
        """Engage bonus applies independently per target."""
        hero = make_hero("Fencer", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(2)]

        fight = FightLog([hero], monsters)
        monster_max = 4

        # Attack monster 0 - at full HP -> x2
        fight.apply_engage_damage(hero, monsters[0], 1)
        state0 = fight.get_state(monsters[0], Temporality.PRESENT)
        assert state0.hp == monster_max - 2, "First monster should take x2"

        # Attack monster 1 - also at full HP -> x2
        fight.apply_engage_damage(hero, monsters[1], 1)
        state1 = fight.get_state(monsters[1], Temporality.PRESENT)
        assert state1.hp == monster_max - 2, "Second monster should also take x2"

        # Attack monster 0 again - now damaged -> normal
        fight.apply_engage_damage(hero, monsters[0], 1)
        state0 = fight.get_state(monsters[0], Temporality.PRESENT)
        assert state0.hp == monster_max - 3, "First monster should take normal damage now"


class TestCruel:
    """Tests for Cruel keyword.

    Cruel deals x2 damage vs targets at half HP or less (HP <= maxHP/2).

    Note: The original Java test is named "fierce" but uses dmgCruel.
    Fierce is a separate keyword (target flees if HP <= N).

    Verified: Confirmed. When monster at half HP, cruel triggers x2.
    """

    def test_cruel_no_bonus_above_half(self):
        """Cruel deals normal damage when target is above half HP."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=6)  # Half = 3

        fight = FightLog([hero], [monster])

        # At 6 HP (above half) -> no x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 5, "Should deal 1 damage (above half HP)"

        # At 5 HP (above half) -> no x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 4, "Should deal 1 damage (above half HP)"

        # At 4 HP (above half) -> no x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 3, "Should deal 1 damage (above half HP)"

    def test_cruel_doubles_at_half_or_less(self):
        """Cruel deals x2 damage when target is at half HP or less."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=6)  # Half = 3

        fight = FightLog([hero], [monster])

        # Damage to 3 HP (half)
        fight.apply_damage(hero, monster, 3, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 3, "Should be at half HP"

        # At 3 HP (= half) -> x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 1, "Should deal 2 damage (at half HP)"

    def test_cruel_full_scenario(self):
        """Full cruel test matching original Java test (named 'fierce').

        Setup: 1 hero vs 1 testGoblin (6 HP, half = 3)
        - dmgCruel(1) x3 -> 3 damage total, monster at 3 HP
        - dmgCruel(1) -> monster at half, x2 -> 2 damage, total 5

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=6)

        fight = FightLog([hero], [monster])
        monster_max = 6

        # First 3 attacks - above half HP
        fight.apply_cruel_damage(hero, monster, 1)
        fight.apply_cruel_damage(hero, monster, 1)
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 3, "should be damaged for 3"

        # 4th attack - now at half HP (3 = 6/2) -> x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 5, "should be damaged for 5 total (attack 4 did x2)"

    def test_cruel_at_one_hp(self):
        """Cruel still works at very low HP."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)  # Half = 2

        fight = FightLog([hero], [monster])

        # Damage to 1 HP
        fight.apply_damage(hero, monster, 3, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 1, "Should be at 1 HP"

        # At 1 HP (< half of 4) -> x2
        fight.apply_cruel_damage(hero, monster, 1)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == -1, "Should deal 2 damage (well below half HP)"


class TestWeaken:
    """Tests for Weaken keyword.

    Weaken deals damage to target AND reduces target's outgoing pending damage by N.
    This affects pending damage that the target has already dealt.

    Example: If monster dealt 3 pending to hero, weaken(2) reduces that to 1 pending.

    Verified: Confirmed.
    """

    def test_weaken_deals_damage(self):
        """Weaken deals damage to the target."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        fight.apply_weaken_damage(hero, monster, 2)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 2, "Weaken should deal damage to target"

    def test_weaken_reduces_pending_damage(self):
        """Weaken reduces target's outgoing pending damage."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Monster attacks hero for 3 pending
        fight.apply_damage(monster, hero, 3, is_pending=True)

        # Hero's future should show 3 damage
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == 2, "Hero should have 3 pending damage"

        # Hero uses weaken(2) on monster
        fight.apply_weaken_damage(hero, monster, 2)

        # Hero's future should now show only 1 damage (3 - 2 = 1)
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == 4, "Pending damage should be reduced by 2"

    def test_weaken_full_scenario(self):
        """Full weaken test matching original Java test.

        Setup: 1 Fighter hero (5 HP) vs 1 Goblin monster (4 HP)
        - Monster attacks hero for 3 pending damage
        - Hero's future: 5 - 3 = 2 HP
        - Hero uses weaken(2) on monster
        - Hero's future: 5 - 1 = 4 HP (pending reduced from 3 to 1)

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Monster attacks hero for 3 pending
        fight.apply_damage(monster, hero, 3, is_pending=True)

        # Check hero's future state - should show 3 damage
        future = fight.get_state(hero, Temporality.FUTURE)
        hero_max = fight.get_state(hero, Temporality.PRESENT).max_hp
        assert future.hp == hero_max - 3, "hero should be hit for 3 damage"

        # Hero uses weaken(2) on monster
        fight.apply_weaken_damage(hero, monster, 2)

        # Check hero's future state - pending should be reduced by 2
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == hero_max - 1, "damage should be reduced by 2"

    def test_weaken_cannot_reduce_below_zero(self):
        """Weaken cannot make pending damage negative."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Monster attacks hero for 1 pending
        fight.apply_damage(monster, hero, 1, is_pending=True)

        # Hero uses weaken(5) on monster (more than pending)
        fight.apply_weaken_damage(hero, monster, 5)

        # Hero's future should show 0 damage (not negative)
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == 5, "Pending damage should be reduced to 0, not negative"

    def test_weaken_affects_only_target_source(self):
        """Weaken only reduces pending from the weakened target, not other sources."""
        hero = make_hero("Fighter", hp=5)
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(2)]

        fight = FightLog([hero], monsters)

        # Both monsters attack hero for 2 pending each
        fight.apply_damage(monsters[0], hero, 2, is_pending=True)
        fight.apply_damage(monsters[1], hero, 2, is_pending=True)

        # Hero's future should show 4 damage
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == 1, "Hero should have 4 pending damage"

        # Hero uses weaken(2) on monster 0
        fight.apply_weaken_damage(hero, monsters[0], 2)

        # Hero's future should show 2 damage (only monster 1's pending remains)
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == 3, "Only monster 0's pending should be reduced"


class TestDrain:
    """Tests for Drain/SelfHeal keyword.

    Drain deals damage to target AND heals the attacker by the same amount.
    Example: drain(1) deals 1 damage to target and heals user by 1.

    Note: This is different from the selfHeal buff (which negates pain).
    This is an attack keyword that provides lifesteal.

    Verified: Confirmed.
    """

    def test_drain_deals_damage(self):
        """Drain deals damage to the target."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        fight.apply_drain_damage(hero, monster, 2)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 2, "Drain should deal damage to target"

    def test_drain_heals_attacker(self):
        """Drain heals the attacker by the damage amount."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero first
        fight.apply_damage(monster, hero, 3, is_pending=False)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.hp == 2, "Hero should be at 2 HP"

        # Hero uses drain on monster
        fight.apply_drain_damage(hero, monster, 1)

        # Hero should be healed by 1
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.hp == 3, "Drain should heal attacker by 1"

    def test_drain_full_scenario(self):
        """Full drain test matching original Java test.

        Setup: 1 Fighter hero (5 HP) vs 1 Goblin monster (4 HP)
        - Monster damages hero for 3 (immediate)
        - Hero at maxHp - 3 = 2 HP
        - Hero uses dmgSelfHeal(1) on monster
        - Hero at maxHp - 2 = 3 HP (healed 1)

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        hero_max = 5

        # Monster damages hero for 3 (immediate)
        fight.apply_damage(monster, hero, 3, is_pending=False)
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == hero_max - 3, "should be damaged for 3"

        # Hero uses drain(1) on monster
        fight.apply_drain_damage(hero, monster, 1)
        future = fight.get_state(hero, Temporality.FUTURE)
        assert future.hp == hero_max - 2, "should be damaged for 2 (healed 1)"

    def test_drain_heal_capped_at_max(self):
        """Drain heal is capped at max HP."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Hero at full HP uses drain
        fight.apply_drain_damage(hero, monster, 2)

        # Hero should still be at max HP (not over)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.hp == 5, "Drain heal should be capped at max HP"


class TestLifestealVsInvincible:
    """Tests for Drain/SelfHeal vs Dodge (invincibility).

    Drain heals the attacker by the intended amount even if the target
    has the Dodge buff (invincibility), which prevents the damage.

    The heal is based on the attack's value, not actual damage dealt.

    Verified: Confirmed.
    """

    def test_dodge_makes_target_immune(self):
        """Dodge buff makes target immune to damage."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Apply dodge to monster
        fight.apply_dodge(monster)

        # Try to damage monster
        fight.apply_damage(hero, monster, 3, is_pending=False)

        # Monster should take no damage
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 4, "Dodge should make target immune to damage"

    def test_drain_heals_even_with_dodge(self):
        """Drain still heals attacker even when target has dodge."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero first
        fight.apply_damage(monster, hero, 3, is_pending=False)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.hp == 2, "Hero should be at 2 HP"

        # Apply dodge to monster
        fight.apply_dodge(monster)

        # Hero uses drain on monster with dodge
        fight.apply_drain_damage(hero, monster, 1)

        # Hero should still be healed by 1
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.hp == 3, "Drain should heal attacker even with dodge"

        # Monster should take no damage
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 4, "Monster with dodge should take no damage"

    def test_lifesteal_vs_invincible_full_scenario(self):
        """Full lifestealVsInvincible test matching original Java test.

        Setup: 1 Fighter hero (5 HP) vs 1 Goblin monster (4 HP)
        - Monster damages hero for 3 -> hero at 2 HP
        - Hero uses drain(1) on monster -> hero at 3 HP
        - Monster gets dodge buff
        - Hero uses drain(1) again -> hero at 4 HP (still heals despite dodge)

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        hero_max = 5

        # Monster damages hero for 3
        fight.apply_damage(monster, hero, 3, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 3, "hero should have taken 3 damage"

        # Hero uses drain(1) on monster
        fight.apply_drain_damage(hero, monster, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 2, "hero should have healed 1 damage"

        # Monster gets dodge buff (invincibility)
        fight.apply_dodge(monster)

        # Hero uses drain(1) on monster again
        fight.apply_drain_damage(hero, monster, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 1, "hero should still heal additional damage"


class TestCruelHeal:
    """Tests for Cruel keyword on heals.

    Cruel applies x2 multiplier to heals when target is at <= half HP (HP <= maxHP/2).
    This is the same condition as cruel damage.

    Verified: Confirmed (from TestKeywordSpell.cruel).
    """

    def test_cruel_heal_doubles_at_half_or_less(self):
        """Cruel heal doubles when target is at half HP or less."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero to at or below half HP (ceil(5/2) = 3 damage -> 2 HP)
        fight.apply_damage(monster, hero, 3, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 2, "Hero should be at 2 HP"
        # 2 HP <= 2.5 (half of 5), so cruel should trigger

        # Cruel heal(1) should heal for 2
        fight.apply_cruel_heal(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 4, "Cruel heal should heal x2 when at half HP or less"

    def test_cruel_heal_no_bonus_above_half(self):
        """Cruel heal has no bonus when target is above half HP."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero to at or below half HP
        fight.apply_damage(monster, hero, 3, is_pending=False)

        # First cruel heal(1) = heal 2, hero now at 4 HP
        fight.apply_cruel_heal(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 4, "Hero at 4 HP"

        # 4 HP > 2.5 (half of 5), so no bonus
        # Second cruel heal(1) should heal for 1 (no bonus)
        fight.apply_cruel_heal(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 5, "Cruel heal should heal normally when above half HP"

    def test_cruel_heal_full_scenario(self):
        """Full cruel heal test matching original Java test.

        Setup: 1 Fighter hero (5 HP) vs 1 Goblin monster
        - Damage hero for ceil(5/2) = 3 -> hero at 2 HP
        - hp = 2, half = 2.5, so hp <= half (cruel triggers)
        - heal(1) with cruel -> heals 2, hero at hp + 2 = 4 HP
        - Now at 4 HP which is > 2.5, so no bonus
        - heal(1) with cruel -> heals 1, hero at hp + 3 = 5 HP

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        import math
        hero_max = 5

        # Damage hero to at or below half HP
        damage = math.ceil(hero_max / 2.0)  # 3
        fight.apply_damage(monster, hero, damage, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        hp_after_damage = state.hp
        assert hp_after_damage <= hero_max / 2.0, "hp should be less than half"

        # First cruel heal
        fight.apply_cruel_heal(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hp_after_damage + 2, "hero should be healed for 2"

        # Second cruel heal (now above half HP)
        fight.apply_cruel_heal(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hp_after_damage + 3, "hero should be healed for 1 more"


class TestRegen:
    """Tests for Regen keyword.

    healRegen(N) heals N immediately AND applies a persistent regen buff.
    The buff heals N HP at the start of each subsequent turn.
    Healing is capped at max HP.

    Verified: Confirmed.
    """

    def test_regen_heals_immediately(self):
        """Regen heals immediately."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero first
        fight.apply_damage(monster, hero, 3, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 2, "Hero should be at 2 HP"

        # Apply regen
        fight.apply_heal_regen(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 3, "Regen should heal immediately"

    def test_regen_heals_each_turn(self):
        """Regen heals at the start of each turn."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero first
        fight.apply_damage(monster, hero, 3, is_pending=False)

        # Apply regen
        fight.apply_heal_regen(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 3, "Should heal 1 immediately"

        # Next turn - regen triggers
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 4, "Should regen 1 HP"

        # Another turn - regen triggers again
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 5, "Should regen to full HP"

    def test_regen_capped_at_max_hp(self):
        """Regen healing is capped at max HP."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero a bit
        fight.apply_damage(monster, hero, 1, is_pending=False)

        # Apply regen
        fight.apply_heal_regen(hero, 1)

        # Multiple turns - should not exceed max HP
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 5, "Should not regen past max HP"

    def test_regen_full_scenario(self):
        """Full regen test matching original Java test.

        Setup: 1 Fighter hero (5 HP) vs 1 Goblin monster (4 HP)
        - Monster damages hero for 3 (with pain) -> hero at 2 HP
        - healRegen(1) -> heals 1, hero at 3 HP
        - nextTurn -> regen 1, hero at 4 HP
        - nextTurn -> regen 1, hero at 5 HP (full)
        - 5 more turns -> still at 5 HP (capped)

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        hero_max = 5

        # Monster damages hero for 3
        fight.apply_damage(monster, hero, 3, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 3, "hero should damaged for 3"

        # healRegen(1)
        fight.apply_heal_regen(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 2, "hero should heal 1"

        # Next turn - regen triggers
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 1, "hero should regen 1"

        # Next turn - regen triggers again
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max, "hero should regen 2 (total, now full)"

        # 5 more turns - should stay at max
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max, "hero should not regen past full"


class TestGrowth:
    """Tests for Growth keyword.

    Growth is a side keyword that increases the side's value by +1 after each use.
    The bonus is permanent (persists for the rest of the fight).

    Example: shieldMana(1) with growth
    - First use: shield=1, mana=1 (base value)
    - Second use: shield=2, mana=2 (growth increased value to 2)
    - Third use: shield=3, mana=3 (growth increased value to 3)
    - Cumulative: shield=6, mana=6

    Verified: Confirmed from TestKeyword.growth in Java test.
    """

    def test_growth_basic(self):
        """Growth increases side value by +1 after each use."""
        from src.dice import Die, Side, Keyword, shield_mana

        hero = make_hero("Mage", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with shieldMana(1) + growth on all sides
        hero.die = Die()
        base_side = shield_mana(1)
        base_side.keywords.add(Keyword.GROWTH)
        hero.die.set_all_sides(base_side)

        # First use: value=1, shield=1, mana=1
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 1, "shield should be 1"
        assert fight.get_total_mana() == 1, "mana should be 1"

        # Second use: value=2 (growth!), shield=1+2=3, mana=1+2=3
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 3, "shield should be 3"
        assert fight.get_total_mana() == 3, "mana should be 3"

        # Third use: value=3 (growth!), shield=3+3=6, mana=3+3=6
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 6, "shield should be 6"
        assert fight.get_total_mana() == 6, "mana should be 6"

    def test_growth_full_scenario(self):
        """Full growth test matching original Java test (TestKeyword.growth).

        Setup: 1 hero with shieldMana(1) on all sides, growth keyword added
        - DieCommand (side 0, self target): shield=1, mana=1
        - DieCommand (side 0, self target): shield=3, mana=3
        - DieCommand (side 0, self target): shield=6, mana=6

        Verified: Confirmed.
        """
        from src.dice import Die, Side, Keyword, shield_mana

        hero = make_hero("Mage", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with shieldMana(1)
        hero.die = Die()
        hero.die.set_all_sides(shield_mana(1))

        # Add growth keyword to all sides (simulates AffectSides(AddKeyword(growth)))
        hero.die.add_keyword_to_all(Keyword.GROWTH)

        # First DieCommand
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 1, "shield should be 1"
        assert fight.get_total_mana() == 1, "mana should be 1"

        # Second DieCommand
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 3, "shield should be 3"
        assert fight.get_total_mana() == 3, "mana should be 3"

        # Third DieCommand
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 6, "shield should be 6"
        assert fight.get_total_mana() == 6, "mana should be 6"

    def test_growth_only_affects_used_side(self):
        """Growth only increases the value of the side that was used."""
        from src.dice import Die, Side, Keyword, shield_mana

        hero = make_hero("Mage", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with shieldMana(1) + growth on all sides
        hero.die = Die()
        base_side = shield_mana(1)
        base_side.keywords.add(Keyword.GROWTH)
        hero.die.set_all_sides(base_side)

        # Use side 0 twice
        fight.use_die(hero, 0, hero)
        fight.use_die(hero, 0, hero)

        # Side 0 should have grown twice (value = 3)
        assert hero.die.get_side(0).calculated_value == 3, "Side 0 should be at value 3"

        # Side 1 should still be at base value (not used)
        assert hero.die.get_side(1).calculated_value == 1, "Side 1 should still be at value 1"

    def test_growth_without_mana(self):
        """Growth works on sides without the mana keyword."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with shield(1) + growth (no mana)
        hero.die = Die()
        base_side = Side(EffectType.SHIELD, 1, {Keyword.GROWTH})
        hero.die.set_all_sides(base_side)

        # First use: shield=1
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 1, "shield should be 1"
        assert fight.get_total_mana() == 0, "mana should be 0 (no mana keyword)"

        # Second use: shield=1+2=3
        fight.use_die(hero, 0, hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 3, "shield should be 3"
        assert fight.get_total_mana() == 0, "mana should still be 0"
