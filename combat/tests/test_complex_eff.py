"""Tests for TestComplexEff - complex effect interactions."""

from src.entity import Entity, EntityType, Team, EntitySize
from src.fight import FightLog, Temporality


def make_hero(name: str, hp: int = 5) -> Entity:
    """Create a hero entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)


def make_monster(name: str, hp: int = 4) -> Entity:
    """Create a monster entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)


class TestRedirect:
    """Tests for redirect (taunt) mechanics.

    Redirect moves ALL incoming damage from target to user (redirector).
    Redirect has selfShield keyword which grants user N shields.
    Shields then block the redirected damage.
    Multiple redirects stack shields.
    """

    def test_redirect_moves_all_damage_to_redirector(self):
        """Redirect(N) moves ALL pending damage from target to redirector.

        Verified: Redirect moves all damage to taunter.
        """
        fighter = make_hero("Fighter", hp=5)
        thief = make_hero("Thief", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([fighter, thief], [goblin])

        # Monster attacks fighter for 3 (pending)
        fight.apply_damage(goblin, fighter, 3, is_pending=True)

        # Fighter should have 3 pending damage
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == 5 - 3, "Fighter should take 3 damage in future"

        # Thief uses redirect(1) targeting fighter
        # This moves ALL damage from fighter to thief, and gives thief 1 shield
        fight.apply_redirect(user=thief, target=fighter, self_shield=1)

        # Fighter should now have full HP (damage redirected away)
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == 5, "Fighter should have full HP after redirect"

        # Thief should have 3 damage - 1 shield = 2 damage taken
        thief_future = fight.get_state(thief, Temporality.FUTURE)
        assert thief_future.hp == 5 - 2, "Thief should take 3-1=2 damage in future"

    def test_redirect_to_self_adds_more_shield(self):
        """Redirecting to self just adds more shields (stacks).

        Verified: Multiple redirects stack shields.
        """
        fighter = make_hero("Fighter", hp=5)
        thief = make_hero("Thief", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([fighter, thief], [goblin])

        # Monster attacks fighter for 3 (pending)
        fight.apply_damage(goblin, fighter, 3, is_pending=True)

        # Thief redirects damage from fighter to self (gets 1 shield)
        fight.apply_redirect(user=thief, target=fighter, self_shield=1)

        # Thief redirects own damage to self (gets +1 more shield)
        fight.apply_redirect(user=thief, target=thief, self_shield=1)

        # Fighter should still have full HP
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == 5, "Fighter should still have full HP"

        # Thief should have 3 damage - 2 shields = 1 damage
        thief_future = fight.get_state(thief, Temporality.FUTURE)
        assert thief_future.hp == 5 - 1, "Thief should take 3-2=1 damage with stacked shields"

    def test_double_taunt_full_scenario(self):
        """Full doubleTaunt test matching original Java test.

        Setup: Fighter + Thief vs Goblin
        1. Goblin attacks Fighter for 3 -> Fighter takes 3
        2. Thief redirect(1) on Fighter -> Fighter 0, Thief takes 2
        3. Thief redirect(1) on self -> Thief takes 1

        Verified: selfShield keyword provides the damage reduction via shields.
        """
        fighter = make_hero("Fighter", hp=5)
        thief = make_hero("Thief", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([fighter, thief], [goblin])
        fighter_max = 5
        thief_max = 5
        attack_amount = 3
        taunt_amount = 1

        # 1. Monster attacks fighter for 3
        fight.apply_damage(goblin, fighter, attack_amount, is_pending=True)
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == fighter_max - attack_amount, "Fighter should take 3 damage"

        # 2. Thief redirect(1) targeting fighter
        fight.apply_redirect(user=thief, target=fighter, self_shield=taunt_amount)

        # Fighter should be "taunted away" - full HP
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == fighter_max, "Fighter should have full HP (taunted away)"

        # Thief should take (attack - taunt) = 2 damage
        thief_future = fight.get_state(thief, Temporality.FUTURE)
        expected_thief_hp = thief_max - (attack_amount - taunt_amount)
        assert thief_future.hp == expected_thief_hp, "Thief should take 3-1=2 damage (taunted towards)"

        # 3. Thief redirect(1) targeting self
        fight.apply_redirect(user=thief, target=thief, self_shield=taunt_amount)

        # Thief should take even less damage now (more shields)
        thief_future = fight.get_state(thief, Temporality.FUTURE)
        expected_thief_hp = thief_max - (attack_amount - taunt_amount * 2)
        assert thief_future.hp == expected_thief_hp, "Thief should take 3-2=1 damage (self taunt)"

        # Fighter should still be at full HP
        fighter_future = fight.get_state(fighter, Temporality.FUTURE)
        assert fighter_future.hp == fighter_max, "Fighter should still have full HP"


class TestKeepShields:
    """Tests for KeepShields buff and turn transitions.

    Shields normally clear at end of turn.
    KeepShields buff prevents shield decay - shields persist across turns.
    """

    def test_shields_clear_at_turn_end(self):
        """Shields normally disappear at the end of a turn.

        Verified: Shields clear on turn transition by default.
        """
        hero = make_hero("Fighter", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [goblin])

        # Give hero 3 shields
        fight.apply_shield(hero, 3)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 3, "Hero should have 3 shields"

        # Next turn - shields should disappear
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 0, "Shields should disappear at turn end"

    def test_keep_shields_prevents_decay(self):
        """KeepShields buff prevents shields from clearing at turn end.

        Verified: With KeepShields, shields persist across turns.
        """
        hero = make_hero("Fighter", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [goblin])

        # Give hero 5 shields and KeepShields buff
        fight.apply_shield(hero, 5)
        fight.apply_keep_shields(hero)

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 5, "Hero should have 5 shields"

        # Next turn - shields should NOT disappear
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 5, "Shields should persist with KeepShields"

    def test_keep_shield_full_scenario(self):
        """Full keepShield test matching original Java test.

        1. Hero gets 3 shields -> 3 shields
        2. Next turn -> shields disappear (0)
        3. Hero gets 5 shields -> 5 shields
        4. Hero gets KeepShields buff
        5. Next turn -> shields persist (5)

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        goblin = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [goblin])

        # 1. Give hero 3 shields
        fight.apply_shield(hero, 3)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 3, "Should be shielded for 3"

        # 2. Next turn - shields should disappear
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 0, "Shield should disappear"

        # 3. Give hero 5 shields
        fight.apply_shield(hero, 5)

        # 4. Apply KeepShields buff
        fight.apply_keep_shields(hero)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 5, "Should be shielded for 5"

        # 5. Next turn - shields should NOT disappear
        fight.next_turn()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.shield == 5, "Shield should not disappear with KeepShields"


class TestStoneskin:
    """Tests for Stone HP (stoneskin) mechanic.

    Stone HP caps all incoming damage to 1 per hit.
    Some units have all stone HP, some have mixed stone/regular HP.
    """

    def test_stone_hp_caps_damage_to_one(self):
        """Stone HP caps any positive damage to 1 per hit.

        Verified: Stoneskin caps incoming damage to 1 per hit.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Golem", hp=10)

        fight = FightLog([hero], [monster])
        monster_max = 10

        # Apply stone HP buff to monster (10 stone pips)
        fight.apply_stone_hp(monster, 10)

        # Verify initial state
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max, "Monster should start at full HP"

        # 0 damage should still do 0
        fight.apply_damage(hero, monster, 0, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max, "0 damage should do 0 damage"

        # 1 damage should do 1
        fight.apply_damage(hero, monster, 1, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 1, "1 damage should do 1 damage"

        # 3 damage should be capped to 1
        fight.apply_damage(hero, monster, 3, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 2, "3 damage should be capped to 1"

        # 2000 damage should be capped to 1
        fight.apply_damage(hero, monster, 2000, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 3, "2000 damage should be capped to 1"

    def test_stoneskin_full_scenario(self):
        """Full stoneskin test matching original Java test.

        Verified: Confirmed.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Golem", hp=10)

        fight = FightLog([hero], [monster])
        monster_max = 10

        # Apply StoneSpecialHp buff (10 pips)
        fight.apply_stone_hp(monster, 10)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 0, "Monster should have taken 0 damage"

        # damage(0) - no damage
        fight.apply_damage(hero, monster, 0, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 0, "Monster should have taken 0 damage"

        # attack 1 - takes 1
        fight.apply_damage(hero, monster, 1, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 1, "Monster should have taken 1 damage"

        # attack 3 - capped to 1, total 2
        fight.apply_damage(hero, monster, 3, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 2, "Monster should have taken 2 damage total"

        # attack 2000 - capped to 1, total 3
        fight.apply_damage(hero, monster, 2000, is_pending=False)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == monster_max - 3, "Monster should have taken 3 damage total"


class TestResurrect:
    """Tests for resurrect mechanic.

    resurrect(N) brings back up to N dead heroes with full HP.
    Capped at number of dead heroes.
    """

    def test_resurrect_brings_back_dead_hero(self):
        """Resurrect brings back a dead hero with full HP.

        Verified: Confirmed.
        """
        heroes = [make_hero(f"Hero{i}", hp=5) for i in range(3)]
        monster = make_monster("Goblin", hp=4)

        fight = FightLog(heroes, [monster])

        # All 3 heroes alive
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 3

        # Kill hero 0
        fight.apply_kill(heroes[0])
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 2

        # Resurrect 1 hero
        fight.apply_resurrect(1)
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 3

        # Check resurrected hero has full HP
        state = fight.get_state(heroes[0], Temporality.PRESENT)
        assert state.hp == 5, "Resurrected hero should have full HP"

    def test_resurrect_capped_at_dead_count(self):
        """Resurrect can't bring back more than are dead.

        Verified: Confirmed.
        """
        heroes = [make_hero(f"Hero{i}", hp=5) for i in range(5)]
        monster = make_monster("Goblin", hp=4)

        fight = FightLog(heroes, [monster])

        # Kill 2 heroes
        fight.apply_kill(heroes[0])
        fight.apply_kill(heroes[1])
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 3

        # Resurrect 5 (but only 2 are dead)
        fight.apply_resurrect(5)
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 5, "Should resurrect all dead (capped)"

    def test_resurrect_full_scenario(self):
        """Full resurrect test matching original Java test.

        Verified: Confirmed.
        """
        heroes = [make_hero(f"Hero{i}", hp=5) for i in range(5)]
        monsters = [make_monster(f"Goblin{i}", hp=4) for i in range(6)]

        fight = FightLog(heroes, monsters)

        # All 5 heroes should be alive
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 5

        # Kill hero 0 -> 4 alive
        fight.apply_kill(heroes[0])
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 4

        # Kill heroes 1, 2 -> 2 alive
        fight.apply_kill(heroes[1])
        fight.apply_kill(heroes[2])
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 2

        # Resurrect 1 -> 3 alive
        fight.apply_resurrect(1)
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 3

        # Resurrect 5 -> all 5 alive (only 2 more dead)
        fight.apply_resurrect(5)
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 5

        # Kill heroes 0,1,2,3 -> 1 alive
        fight.apply_kill(heroes[0])
        fight.apply_kill(heroes[1])
        fight.apply_kill(heroes[2])
        fight.apply_kill(heroes[3])
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 1

        # Resurrect 3 -> 4 alive
        fight.apply_resurrect(3)
        assert len(fight.get_alive_heroes(Temporality.PRESENT)) == 4


class TestVitality:
    """Tests for Vitality keyword on heal.

    healVitality(N) heals by N AND increases max HP by N.
    HP capped at new max (can exceed old max).
    """

    def test_vitality_heals_and_increases_max(self):
        """Vitality heals by N and increases max HP by N.

        Verified: 2/4 + healVitality(3) = 5/7
        """
        hero = make_hero("Fighter", hp=4)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Damage hero to 2/4
        fight.apply_damage(monster, hero, 2, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 2, "Hero should be at 2 HP"
        assert state.max_hp == 4, "Hero should have 4 max HP"

        # healVitality(3) -> 5/7
        fight.apply_heal_vitality(hero, 3)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 5, "Hero should be at 5 HP after vitality heal"
        assert state.max_hp == 7, "Hero should have 7 max HP after vitality heal"

    def test_vitality_at_full_hp(self):
        """Vitality on full HP hero increases both HP and max HP.

        Verified: 7/7 + healVitality(2) = 9/9
        """
        hero = make_hero("Fighter", hp=7)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 7 and state.max_hp == 7

        # healVitality(2) -> 9/9
        fight.apply_heal_vitality(hero, 2)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 9, "Hero should be at 9 HP"
        assert state.max_hp == 9, "Hero should have 9 max HP"

    def test_fortitude_heal_scenario(self):
        """Test based on original fortitudeHeal test.

        Hero at 5 max HP, damaged for 3 -> 2/5
        healVitality(2) -> 4/7
        healVitality(5) -> 9/12 (based on confirmed mechanic)

        Note: Original test expected 12/12, but confirmed mechanic gives 9/12.
        """
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])
        hero_max = 5

        # Damage hero for 3 -> 2/5
        fight.apply_damage(monster, hero, 3, is_pending=False)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == hero_max - 3, "Hero should have lost 3 HP"

        # healVitality(2) -> 4/7
        fight.apply_heal_vitality(hero, 2)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 4, "Hero should be at 4 HP"
        assert state.max_hp == hero_max + 2, "Hero should have +2 max HP"

        # healVitality(5) -> 9/12 (hp + 5, max + 5, capped at new max)
        fight.apply_heal_vitality(hero, 5)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 9, "Hero HP should be 4+5=9"
        assert state.max_hp == hero_max + 2 + 5, "Hero max HP should be 5+2+5=12"


class TestGoblinFlee:
    """Tests for goblin flee mechanic.

    Goblins flee when they become the only remaining enemy after an ally dies.
    Fleeing counts as victory.
    """

    def test_goblin_flees_when_alone(self):
        """When one goblin dies and another is left alone, it flees.

        Verified: Goblins flee when they are the only remaining enemy.
        """
        from src.entity import Entity, Team, FLEEING_GOBLIN, FIGHTER

        hero = Entity(FIGHTER, Team.HERO)
        goblin1 = Entity(FLEEING_GOBLIN, Team.MONSTER, position=0)
        goblin2 = Entity(FLEEING_GOBLIN, Team.MONSTER, position=1)

        fight = FightLog([hero], [goblin1, goblin2])

        # Both goblins should be alive
        assert not fight.is_victory(Temporality.PRESENT), "Should not be victory yet"
        assert len(fight.get_present_monsters(Temporality.PRESENT)) == 2

        # Kill goblin1 with massive damage
        fight.apply_damage(hero, goblin1, 100, is_pending=False)

        # Goblin1 is dead
        state1 = fight.get_state(goblin1, Temporality.PRESENT)
        assert state1.is_dead, "Goblin1 should be dead"

        # Goblin2 should have fled (only one remaining, has flee behavior)
        state2 = fight.get_state(goblin2, Temporality.PRESENT)
        assert state2.fled, "Goblin2 should have fled"

        # Victory should be achieved
        assert fight.is_victory(Temporality.PRESENT), "Should be victory after goblin flees"

    def test_non_fleeing_monster_doesnt_flee(self):
        """Regular monsters don't flee when left alone."""
        from src.entity import Entity, Team, GOBLIN, FIGHTER

        hero = Entity(FIGHTER, Team.HERO)
        # GOBLIN (not FLEEING_GOBLIN) doesn't have flee behavior
        goblin1 = Entity(GOBLIN, Team.MONSTER, position=0)
        goblin2 = Entity(GOBLIN, Team.MONSTER, position=1)

        fight = FightLog([hero], [goblin1, goblin2])

        # Kill goblin1
        fight.apply_damage(hero, goblin1, 100, is_pending=False)

        # Goblin2 should NOT flee (no flee behavior)
        state2 = fight.get_state(goblin2, Temporality.PRESENT)
        assert not state2.fled, "Regular goblin should not flee"

        # Not victory yet
        assert not fight.is_victory(Temporality.PRESENT), "Should not be victory"

    def test_goblin_flee_full_scenario(self):
        """Full goblinFlee test matching original Java test.

        Setup: 2 fleeing goblins
        Kill one -> other flees -> victory

        Verified: Confirmed.
        """
        from src.entity import Entity, Team, FLEEING_GOBLIN, FIGHTER

        hero = Entity(FIGHTER, Team.HERO)
        goblins = [
            Entity(FLEEING_GOBLIN, Team.MONSTER, position=i)
            for i in range(2)
        ]

        fight = FightLog([hero], goblins)

        # Kill first goblin with 100 damage
        fight.apply_damage(hero, goblins[0], 100, is_pending=False)

        # Should be victory (other goblin fled)
        assert fight.is_victory(Temporality.PRESENT), "Should be goblinflee victory"


class TestPetrify:
    """Tests for petrify mechanics.

    Petrify turns die sides into Blank (no effect).
    Petrification order: Top, Left, Middle, Right, Rightmost, Bottom
    (indices: 0, 2, 4, 3, 5, 1)

    Verified: User confirmed petrify order in-game.
    """

    def test_petrify_changes_side_to_blank(self):
        """Petrify changes a Damage side to Blank.

        Verified: Basilisk petrifies hero sides, confirmed in-game.
        """
        from src.dice import Die, Side, PETRIFY_ORDER
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die with sword (Damage) on all sides
        hero.die = Die([
            Side(EffectType.DAMAGE, 1),  # Side 0 (Top)
            Side(EffectType.DAMAGE, 1),  # Side 1 (Bottom)
            Side(EffectType.DAMAGE, 2),  # Side 2 (Left)
            Side(EffectType.DAMAGE, 2),  # Side 3 (Right)
            Side(EffectType.SHIELD, 1),  # Side 4 (Middle)
            Side(EffectType.DAMAGE, 3),  # Side 5 (Rightmost)
        ])

        # Before petrify: side 0 should be Damage
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)
        assert side_state.effect_type == EffectType.DAMAGE, "Side 0 should be Damage before petrify"

        # Apply petrify(1) - should petrify side 0 (Top, first in PETRIFY_ORDER)
        fight.apply_petrify(hero, 1)

        # After petrify: side 0 should be Blank
        state = fight.get_state(hero, Temporality.PRESENT)
        side_state = state.get_side_state(0)
        assert side_state.effect_type == EffectType.BLANK, "Side 0 should be Blank after petrify"
        assert side_state.is_petrified, "Side 0 should be marked as petrified"

    def test_petrify_order(self):
        """Petrify follows specific order: Top, Left, Middle, Right, Rightmost, Bottom.

        Verified: User confirmed order in-game.
        """
        from src.dice import Die, Side, PETRIFY_ORDER
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die
        hero.die = Die([Side(EffectType.DAMAGE, 1) for _ in range(6)])

        # Petrify one at a time and check order
        expected_order = PETRIFY_ORDER  # [0, 2, 4, 3, 5, 1]

        for i, expected_side in enumerate(expected_order):
            fight.apply_petrify(hero, 1)
            state = fight.get_state(hero, Temporality.PRESENT)

            # Check this side is now petrified
            side_state = state.get_side_state(expected_side)
            assert side_state.effect_type == EffectType.BLANK, f"Side {expected_side} should be petrified after {i+1} petrify"

    def test_petrify_capped_at_six(self):
        """Petrify cannot petrify more than 6 sides (one die).

        Verified: petrifyAboveSix test confirms this.
        """
        from src.dice import Die, Side
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die
        hero.die = Die([Side(EffectType.DAMAGE, 1) for _ in range(6)])

        # Apply petrify(7) - should only petrify 6 sides
        fight.apply_petrify(hero, 7)

        state = fight.get_state(hero, Temporality.PRESENT)
        total_petrified = state.get_total_petrification()

        assert total_petrified == 6, f"Should only petrify 6 sides, got {total_petrified}"

    def test_cleanse_removes_petrification_reverse_order(self):
        """Cleanse removes petrification in reverse order (last petrified first).

        Verified: User confirmed cleanse order in-game.
        """
        from src.dice import Die, Side, PETRIFY_ORDER
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die
        hero.die = Die([Side(EffectType.DAMAGE, 1) for _ in range(6)])

        # Petrify all 6 sides
        fight.apply_petrify(hero, 6)

        # Should have 6 petrified
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 6, "Should have 6 petrified"

        # Cleanse 4 - should remove: Bottom(1), Rightmost(5), Right(3), Middle(4)
        # Remaining petrified: Top(0), Left(2)
        fight.apply_cleanse_petrify(hero, 4)

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 2, "Should have 2 petrified after cleanse"

        # Top (0) and Left (2) should still be petrified
        assert state.get_side_state(0).effect_type == EffectType.BLANK, "Top should still be petrified"
        assert state.get_side_state(2).effect_type == EffectType.BLANK, "Left should still be petrified"

        # Middle (4), Right (3), Rightmost (5), Bottom (1) should be restored
        assert state.get_side_state(4).effect_type == EffectType.DAMAGE, "Middle should be restored"
        assert state.get_side_state(3).effect_type == EffectType.DAMAGE, "Right should be restored"
        assert state.get_side_state(5).effect_type == EffectType.DAMAGE, "Rightmost should be restored"
        assert state.get_side_state(1).effect_type == EffectType.DAMAGE, "Bottom should be restored"

    def test_get_total_petrification(self):
        """getTotalPetrification counts petrified sides correctly.

        Verified: Java test uses this method.
        """
        from src.dice import Die, Side
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die
        hero.die = Die([Side(EffectType.DAMAGE, 1) for _ in range(6)])

        # Initially: 0 petrified
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 0, "Should have 0 petrified initially"

        # Petrify 3
        fight.apply_petrify(hero, 3)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 3, "Should have 3 petrified"

        # Cleanse 1
        fight.apply_cleanse_petrify(hero, 1)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 2, "Should have 2 petrified after cleanse"

    def test_undo_restores_petrification_state(self):
        """Undo properly restores petrification state.

        Verified: petrifyBug test uses undo.
        """
        from src.dice import Die, Side
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Basilisk", hp=10)

        fight = FightLog([hero], [monster])

        # Set up hero's die
        hero.die = Die([Side(EffectType.DAMAGE, 1) for _ in range(6)])

        # Petrify 2
        fight.apply_petrify(hero, 2)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 2, "Should have 2 petrified"

        # Undo
        fight.undo()
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_total_petrification() == 0, "Should have 0 petrified after undo"


class TestPair:
    """Tests for the PAIR keyword.

    PAIR doubles the effect value (x2) if the previous die had the same
    calculated value. The comparison is between the current die's value
    (after triggers, before pair bonus) and the previous die's final
    calculated value (including its pair bonus if any).

    Java source: TestComplexEff.pair()
    """

    def test_pair_first_use_no_bonus(self):
        """First use of pair gives base value - no previous die to pair with.

        Java: rollHit(f, h, null, ESB.manaPair.val(1));
              assertEquals("Should have 1 mana", 1, f.getSnapshot(Present).getTotalMana());
        """
        from src.dice import Die, mana_pair

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with manaPair(1) on side 0
        hero.die = Die([mana_pair(1) for _ in range(6)])

        # First use: no previous die, no pair bonus
        fight.use_die(hero, 0, monster)  # target doesn't matter for mana

        assert fight.get_total_mana() == 1, "First manaPair(1) should grant 1 mana"

    def test_pair_second_consecutive_doubles(self):
        """Second consecutive use of same value pairs and doubles.

        Java: rollHit(f, h, null, ESB.manaPair.val(1));  // 1 mana total
              rollHit(f, h, null, ESB.manaPair.val(1));  // 3 mana total
        """
        from src.dice import Die, mana_pair

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die with manaPair(1) on all sides
        hero.die = Die([mana_pair(1) for _ in range(6)])

        # First use: value=1, no pair → 1 mana
        fight.use_die(hero, 0, monster)
        fight.recharge_die(hero)  # Recharge to use again

        # Second use: value=1, prev final=1 → paired! 1*2=2 mana
        fight.use_die(hero, 1, monster)

        assert fight.get_total_mana() == 3, "Should have 1 + 2 = 3 mana after two manaPair(1)"

    def test_pair_chains_based_on_final_value(self):
        """Pair chains based on final calculated value.

        After pair doubles the value, that doubled value becomes
        what the next die compares against.

        Java: rollHit manaPair(1) → 1 mana
              rollHit manaPair(1) → 3 mana (paired, +2)
              rollHit manaPair(2) → 7 mana (value=2, prev final=2, paired! +4)
              rollHit manaPair(2) → 9 mana (value=2, prev final=4, NOT paired, +2)
        """
        from src.dice import Die, mana_pair

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        # Set up hero's die: first two sides manaPair(1), rest manaPair(2)
        hero.die = Die([
            mana_pair(1), mana_pair(1),
            mana_pair(2), mana_pair(2),
            mana_pair(2), mana_pair(2)
        ])

        # Use 1: manaPair(1), no prev → 1 mana
        fight.use_die(hero, 0, monster)
        fight.recharge_die(hero)
        assert fight.get_total_mana() == 1, "After 1st die: 1 mana"

        # Use 2: manaPair(1), prev final=1 → paired! 1*2=2 mana
        fight.use_die(hero, 1, monster)
        fight.recharge_die(hero)
        assert fight.get_total_mana() == 3, "After 2nd die: 3 mana"

        # Use 3: manaPair(2), prev final=2 → paired! 2*2=4 mana
        fight.use_die(hero, 2, monster)
        fight.recharge_die(hero)
        assert fight.get_total_mana() == 7, "After 3rd die: 7 mana"

        # Use 4: manaPair(2), prev final=4 → NOT paired (2 != 4), 2 mana
        fight.use_die(hero, 3, monster)
        assert fight.get_total_mana() == 9, "After 4th die: 9 mana"

    def test_pair_different_value_breaks_chain(self):
        """Using a different value breaks the pair chain.

        Java logic: pair compares current value with previous final value.
        """
        from src.dice import Die, mana_pair

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=4)

        fight = FightLog([hero], [monster])

        hero.die = Die([mana_pair(1), mana_pair(3), mana_pair(1)] + [mana_pair(1) for _ in range(3)])

        # Use manaPair(1) → 1 mana
        fight.use_die(hero, 0, monster)
        fight.recharge_die(hero)

        # Use manaPair(3) → prev=1, not equal, no pair → 3 mana
        fight.use_die(hero, 1, monster)
        fight.recharge_die(hero)
        assert fight.get_total_mana() == 4, "manaPair(1) + manaPair(3) = 4 mana (no pair)"

        # Use manaPair(1) → prev=3, not equal, no pair → 1 mana
        fight.use_die(hero, 2, monster)
        assert fight.get_total_mana() == 5, "...+ manaPair(1) = 5 mana (still no pair)"


class TestDoubleUse:
    """Tests for DOUBLE_USE keyword.

    DOUBLE_USE allows a die to be used twice per turn before becoming used.
    """

    def test_double_use_first_use_not_exhausted(self):
        """First use of doubleUse die does not exhaust it."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)

        fight = FightLog([hero], [monster])

        # Create a damage side with DOUBLE_USE
        double_use_side = Side(EffectType.DAMAGE, 2, {Keyword.DOUBLE_USE})
        hero.die = Die([double_use_side for _ in range(6)])

        # First use
        fight.use_die(hero, 0, monster)

        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert not hero_state.is_used(), "Die should NOT be used after first use with doubleUse"
        assert hero_state.times_used_this_turn == 1

    def test_double_use_second_use_exhausts(self):
        """Second use of doubleUse die exhausts it."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)

        fight = FightLog([hero], [monster])

        double_use_side = Side(EffectType.DAMAGE, 2, {Keyword.DOUBLE_USE})
        hero.die = Die([double_use_side for _ in range(6)])

        # First use
        fight.use_die(hero, 0, monster)
        # Second use
        fight.use_die(hero, 1, monster)

        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.is_used(), "Die should be used after second use with doubleUse"
        assert hero_state.times_used_this_turn == 2


class TestQuadUse:
    """Tests for QUAD_USE keyword.

    QUAD_USE allows a die to be used 4 times per turn before becoming used.
    """

    def test_quad_use_exhausts_on_fourth(self):
        """Die with quadUse exhausts on 4th use."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero], [monster])

        quad_use_side = Side(EffectType.DAMAGE, 1, {Keyword.QUAD_USE})
        hero.die = Die([quad_use_side for _ in range(6)])

        # Uses 1-3: not exhausted
        for i in range(3):
            fight.use_die(hero, i, monster)
            hero_state = fight.get_state(hero, Temporality.PRESENT)
            assert not hero_state.is_used(), f"Die should NOT be used after {i+1} uses"

        # Use 4: exhausted
        fight.use_die(hero, 3, monster)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.is_used(), "Die should be used after 4th use"


class TestHyperUse:
    """Tests for HYPER_USE keyword.

    HYPER_USE allows a die to be used N times per turn (N = pip value).
    """

    def test_hyper_use_3_pips_exhausts_on_third(self):
        """Die with hyperUse(3) exhausts on 3rd use."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero], [monster])

        hyper_use_side = Side(EffectType.DAMAGE, 3, {Keyword.HYPER_USE})
        hero.die = Die([hyper_use_side for _ in range(6)])

        # Uses 1-2: not exhausted
        for i in range(2):
            fight.use_die(hero, i, monster)
            hero_state = fight.get_state(hero, Temporality.PRESENT)
            assert not hero_state.is_used(), f"Die should NOT be used after {i+1} uses"

        # Use 3: exhausted
        fight.use_die(hero, 2, monster)
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        assert hero_state.is_used(), "Die should be used after 3rd use (hyperUse(3))"


class TestRite:
    """Tests for RITE keyword.

    RITE gives +1 per unused ally (excluding self) and marks them as used.
    """

    def test_rite_bonus_per_unused_ally(self):
        """Rite grants +1 damage per unused ally."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero1 = make_hero("Fighter", hp=5)
        hero2 = make_hero("Thief", hp=5)
        hero3 = make_hero("Mage", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero1, hero2, hero3], [monster])

        # Give hero1 a rite damage side (base 1 damage)
        rite_side = Side(EffectType.DAMAGE, 1, {Keyword.RITE})
        hero1.die = Die([rite_side for _ in range(6)])

        # Give hero2 and hero3 dummy dice
        dummy = Side(EffectType.DAMAGE, 1, set())
        hero2.die = Die([dummy for _ in range(6)])
        hero3.die = Die([dummy for _ in range(6)])

        # Hero1 uses rite: 2 unused allies (hero2, hero3) = +2 bonus = 3 total damage
        fight.use_die(hero1, 0, monster)

        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 17, "Monster should take 3 damage (1 base + 2 unused allies)"

    def test_rite_marks_allies_as_used(self):
        """Rite marks all unused allies as used after applying bonus."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero1 = make_hero("Fighter", hp=5)
        hero2 = make_hero("Thief", hp=5)
        hero3 = make_hero("Mage", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero1, hero2, hero3], [monster])

        rite_side = Side(EffectType.DAMAGE, 1, {Keyword.RITE})
        hero1.die = Die([rite_side for _ in range(6)])

        dummy = Side(EffectType.DAMAGE, 1, set())
        hero2.die = Die([dummy for _ in range(6)])
        hero3.die = Die([dummy for _ in range(6)])

        # Hero1 uses rite
        fight.use_die(hero1, 0, monster)

        # All allies should be marked as used now
        assert fight.get_state(hero2, Temporality.PRESENT).is_used()
        assert fight.get_state(hero3, Temporality.PRESENT).is_used()

    def test_rite_second_use_no_bonus(self):
        """Second rite gets no bonus since all allies are now used."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero1 = make_hero("Fighter", hp=5)
        hero2 = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero1, hero2], [monster])

        rite_side = Side(EffectType.DAMAGE, 1, {Keyword.RITE})
        hero1.die = Die([rite_side for _ in range(6)])

        dummy = Side(EffectType.DAMAGE, 1, set())
        hero2.die = Die([dummy for _ in range(6)])

        # First rite: 1 unused ally = 2 damage
        fight.use_die(hero1, 0, monster)
        fight.recharge_die(hero1)

        # Second rite: 0 unused allies = 1 damage (base only)
        fight.use_die(hero1, 1, monster)

        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 17, "Monster should take 2+1=3 total damage"


class TestTrill:
    """Tests for TRILL keyword (trio + skill combined).

    TRILL = trio + skill:
    - x3 multiplier if previous 2 dice had same calculated value
    - +N pips where N = entity tier
    """

    def test_trill_skill_bonus_always_applies(self):
        """Trill adds tier bonus even without trio match."""
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero = make_hero("Fighter", hp=5)
        hero.entity_type.tier = 2  # Set tier for skill bonus
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero], [monster])

        # Create a damage(1) side with TRILL
        trill_side = Side(EffectType.DAMAGE, 1, {Keyword.TRILL})
        hero.die = Die([trill_side for _ in range(6)])

        # Use trill: base 1 + tier 2 = 3 damage (no trio match)
        fight.use_die(hero, 0, monster)

        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 17, "Monster should take 3 damage (1 base + 2 tier)"

    def test_trill_trio_multiplier_when_matched(self):
        """Trill x3 when previous 2 dice match current value.

        Note: trill has both trio (x3 if match) AND skill (+tier) bonuses.
        Default tier is 1, so trill damage = 1*3 + 1 = 4 when matching.
        """
        from src.dice import Die, Side, Keyword
        from src.effects import EffectType

        hero1 = make_hero("Fighter", hp=5)
        hero1.entity_type.tier = 0  # No tier bonus for easier testing
        hero2 = make_hero("Thief", hp=5)
        monster = make_monster("Goblin", hp=20)

        fight = FightLog([hero1, hero2], [monster])

        # Hero1 has trill(1), hero2 has regular damage(1)
        trill_side = Side(EffectType.DAMAGE, 1, {Keyword.TRILL})
        dmg_side = Side(EffectType.DAMAGE, 1, set())

        hero1.die = Die([trill_side for _ in range(6)])
        hero2.die = Die([dmg_side for _ in range(6)])

        # Use dmg(1) twice to set up trio
        fight.use_die(hero2, 0, monster)  # prev value = 1
        fight.recharge_die(hero2)
        fight.use_die(hero2, 1, monster)  # prev value = 1

        # Now hero1 uses trill(1): value=1, previous 2 also =1 → trio match! x3 = 3
        # With tier=0, final damage = 1*3 + 0 = 3
        fight.recharge_die(hero2)  # recharge hero2 so we can use hero1
        fight.use_die(hero1, 0, monster)

        # Total damage: 1 + 1 + 3 = 5
        monster_state = fight.get_state(monster, Temporality.PRESENT)
        assert monster_state.hp == 15, "Monster should take 1+1+3=5 damage (trill triggered x3)"
