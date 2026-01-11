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
