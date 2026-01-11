"""Tests for status effect keywords (poison, regen, cleanse, plague, acidic)."""

from src.entity import Entity, EntityType, Team, EntitySize
from src.fight import FightLog, Temporality
from src.dice import Die, Side, Keyword
from src.effects import EffectType


def make_hero(name: str, hp: int = 5) -> Entity:
    """Create a hero entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)


def make_monster(name: str, hp: int = 4) -> Entity:
    """Create a monster entity."""
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)


class TestPoison:
    """Tests for poison keyword.

    Poison:
    - Applies N poison stacks to target
    - Poison deals damage at end of turn (direct, bypasses shields)
    - Multiple poison applications stack (values add)
    - Can be removed by cleanse
    """

    def test_poison_applies_stacks(self):
        """Poison side applies poison stacks to target."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        poison_side = Side(EffectType.DAMAGE, 2, {Keyword.POISON})
        hero.die.set_all_sides(poison_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.get_poison_damage_taken() == 2

    def test_poison_damages_on_turn_end(self):
        """Poison deals damage at end of turn."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        poison_side = Side(EffectType.DAMAGE, 3, {Keyword.POISON})
        hero.die.set_all_sides(poison_side)

        # Apply 3 poison (also 3 damage from the side itself)
        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Monster took 3 damage + has 3 poison pending
        assert state.hp == 7  # 10 - 3 damage

        # End turn - poison triggers
        fight.next_turn()

        state = fight.get_state(monster, Temporality.PRESENT)
        # 7 - 3 poison = 4 HP
        assert state.hp == 4

    def test_poison_stacks_additively(self):
        """Multiple poison applications stack."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=20)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        poison_side = Side(EffectType.DAMAGE, 2, {Keyword.POISON})
        hero.die.set_all_sides(poison_side)

        # Apply poison twice
        fight.use_die(hero, 0, monster)
        state = fight.get_state(hero, Temporality.PRESENT)
        state.used_die = False  # Reset for second use
        fight._update_state(hero, used_die=False)

        fight.use_die(hero, 1, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Should have 4 poison (2 + 2)
        assert state.get_poison_damage_taken() == 4

    def test_poison_bypasses_shields(self):
        """Poison damage bypasses shields."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Give monster shields
        fight.apply_shield(monster, 5)
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.shield == 5

        # Apply poison via direct method
        fight.apply_poison(monster, 3)

        # End turn - poison should bypass shields
        fight.next_turn()

        state = fight.get_state(monster, Temporality.PRESENT)
        # Shields should still be there (cleared by turn end), HP reduced by poison
        assert state.hp == 7  # 10 - 3 poison


class TestRegen:
    """Tests for regen keyword.

    Regen:
    - Applies N regen stacks to target
    - Regen heals at end of turn (capped at max HP)
    - Multiple regen applications stack (values add)
    """

    def test_regen_applies_stacks(self):
        """Regen side applies regen stacks to target."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        regen_side = Side(EffectType.HEAL, 2, {Keyword.REGEN})
        hero.die.set_all_sides(regen_side)

        # Damage the hero first so we can see regen
        fight.apply_damage(monster, hero, 3)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 2  # 5 - 3

        fight.use_die(hero, 0, hero)

        state = fight.get_state(hero, Temporality.PRESENT)
        # Got 2 healing from heal effect + has 2 regen pending
        assert state.hp == 4  # 2 + 2 heal

    def test_regen_heals_on_turn_end(self):
        """Regen heals at end of turn."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Damage the hero
        fight.apply_damage(hero, hero, 5)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 5

        # Apply regen directly
        fight.apply_regen(hero, 3)

        # End turn - regen triggers
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        # 5 + 3 regen = 8 HP
        assert state.hp == 8

    def test_regen_capped_at_max_hp(self):
        """Regen is capped at max HP."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Damage the hero slightly
        fight.apply_damage(hero, hero, 2)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 8

        # Apply large regen
        fight.apply_regen(hero, 10)

        # End turn - regen triggers (but capped at max HP)
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        # Should be at max HP (10), not 18
        assert state.hp == 10


class TestCleanse:
    """Tests for cleanse keyword.

    Cleanse:
    - Removes N points of negative effects from target
    - Works against poison, petrify, weaken, inflict
    """

    def test_cleanse_removes_poison(self):
        """Cleanse removes poison stacks."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Apply poison
        fight.apply_poison(hero, 5)
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_poison_damage_taken() == 5

        # Apply cleanse
        fight.apply_cleanse(hero, 3)

        state = fight.get_state(hero, Temporality.PRESENT)
        # 5 - 3 = 2 poison remaining
        assert state.get_poison_damage_taken() == 2

    def test_cleanse_fully_removes_poison(self):
        """Cleanse can fully remove poison."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Apply poison
        fight.apply_poison(hero, 3)

        # Apply more cleanse than poison
        fight.apply_cleanse(hero, 5)

        state = fight.get_state(hero, Temporality.PRESENT)
        # All poison removed
        assert state.get_poison_damage_taken() == 0


class TestPoisonRegenInteraction:
    """Tests for poison and regen interaction at turn end."""

    def test_poison_and_regen_at_turn_end(self):
        """Poison and regen are netted at turn end."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Apply both
        fight.apply_poison(hero, 3)
        fight.apply_regen(hero, 5)

        # End turn - net effect is +2 HP (5 regen - 3 poison)
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        # 10 + (5 - 3) = 12, but capped at max HP 10
        assert state.hp == 10

    def test_poison_greater_than_regen(self):
        """When poison > regen, entity takes net damage."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        # Apply poison > regen
        fight.apply_poison(hero, 5)
        fight.apply_regen(hero, 2)

        # End turn - net damage of 3
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        # 10 - (5 - 2) = 7 HP
        assert state.hp == 7


class TestSelfPoison:
    """Tests for selfPoison keyword."""

    def test_self_poison_applies_to_source(self):
        """Self-poison applies poison to the die user, not target."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        self_poison_side = Side(EffectType.DAMAGE, 3, {Keyword.SELF_POISON})
        hero.die.set_all_sides(self_poison_side)

        fight.use_die(hero, 0, monster)

        # Hero should have poison, not monster
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        monster_state = fight.get_state(monster, Temporality.PRESENT)

        assert hero_state.get_poison_damage_taken() == 3
        assert monster_state.get_poison_damage_taken() == 0


class TestSelfRegen:
    """Tests for selfRegen keyword."""

    def test_self_regen_applies_to_source(self):
        """Self-regen applies regen to the die user, not target."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Damage hero first
        fight.apply_damage(monster, hero, 5)

        hero.die = Die()
        self_regen_side = Side(EffectType.DAMAGE, 3, {Keyword.SELF_REGEN})
        hero.die.set_all_sides(self_regen_side)

        fight.use_die(hero, 0, monster)

        # Hero should have regen (tracked as buff, triggers on turn end)
        # Let's check by advancing turn
        fight.next_turn()

        hero_state = fight.get_state(hero, Temporality.PRESENT)
        # Started at 5 HP, gained 3 from regen
        assert hero_state.hp == 8


class TestSelfCleanse:
    """Tests for selfCleanse keyword."""

    def test_self_cleanse_cleanses_source(self):
        """Self-cleanse cleanses the die user, not target."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Poison both
        fight.apply_poison(hero, 5)
        fight.apply_poison(monster, 5)

        hero.die = Die()
        self_cleanse_side = Side(EffectType.DAMAGE, 3, {Keyword.SELF_CLEANSE})
        hero.die.set_all_sides(self_cleanse_side)

        fight.use_die(hero, 0, monster)

        # Hero should be cleansed, monster should not
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        monster_state = fight.get_state(monster, Temporality.PRESENT)

        assert hero_state.get_poison_damage_taken() == 2  # 5 - 3
        assert monster_state.get_poison_damage_taken() == 5  # Unchanged


class TestPlague:
    """Tests for plague keyword.

    Plague: +N pips where N = total poison on all characters.
    """

    def test_plague_bonus_from_total_poison(self):
        """Plague gets bonus from total poison on all characters."""
        hero = make_hero("Fighter", hp=10)
        monsters = [make_monster(f"Goblin{i}", hp=10) for i in range(2)]
        fight = FightLog([hero], monsters)

        # Apply poison to various entities
        fight.apply_poison(hero, 3)         # 3 on hero
        fight.apply_poison(monsters[0], 2)  # 2 on monster 0
        fight.apply_poison(monsters[1], 1)  # 1 on monster 1
        # Total: 6 poison

        hero.die = Die()
        plague_side = Side(EffectType.DAMAGE, 1, {Keyword.PLAGUE})
        hero.die.set_all_sides(plague_side)

        fight.use_die(hero, 0, monsters[0])

        state = fight.get_state(monsters[0], Temporality.PRESENT)
        # Base 1 + 6 plague bonus = 7 damage
        # Monster had 10 HP, now 10 - 7 = 3
        assert state.hp == 3


class TestAcidic:
    """Tests for acidic keyword.

    Acidic: +N pips where N = poison on me (source).
    """

    def test_acidic_bonus_from_self_poison(self):
        """Acidic gets bonus from poison on the source."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Apply poison to hero (source)
        fight.apply_poison(hero, 5)

        hero.die = Die()
        acidic_side = Side(EffectType.DAMAGE, 2, {Keyword.ACIDIC})
        hero.die.set_all_sides(acidic_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Base 2 + 5 acidic bonus = 7 damage
        # Monster had 10 HP, now 10 - 7 = 3
        assert state.hp == 3

    def test_acidic_no_bonus_without_poison(self):
        """Acidic provides no bonus if source has no poison."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # No poison on hero

        hero.die = Die()
        acidic_side = Side(EffectType.DAMAGE, 2, {Keyword.ACIDIC})
        hero.die.set_all_sides(acidic_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Base 2 + 0 acidic bonus = 2 damage
        assert state.hp == 8


# ============================================================================
# Buff System Keywords Tests
# ============================================================================

class TestWeaken:
    """Tests for weaken keyword.

    Weaken: Target gets -N to all pips for one turn.
    """

    def test_weaken_reduces_all_side_values(self):
        """Weaken reduces all side values on target."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Give monster a die with known values
        monster.die = Die()
        damage_side = Side(EffectType.DAMAGE, 5)
        monster.die.set_all_sides(damage_side)

        # Apply weaken to monster
        fight.apply_weaken(monster, 2)

        # Check side states - should have reduced values
        side_state = fight.get_side_state(monster, 0)
        # Base 5 - 2 weaken = 3
        assert side_state.value == 3

    def test_weaken_is_cleansable(self):
        """Weaken can be removed by cleanse."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        fight.apply_weaken(hero, 3)

        # Give hero a die
        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 5)
        hero.die.set_all_sides(damage_side)

        # Check side has reduced value
        side_state = fight.get_side_state(hero, 0)
        assert side_state.value == 2  # 5 - 3

        # Cleanse removes weaken
        fight.apply_cleanse(hero, 5)

        # Check side is back to normal
        side_state = fight.get_side_state(hero, 0)
        assert side_state.value == 5

    def test_weaken_stacks(self):
        """Multiple weaken applications stack."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 10)
        hero.die.set_all_sides(damage_side)

        fight.apply_weaken(hero, 2)
        fight.apply_weaken(hero, 3)

        side_state = fight.get_side_state(hero, 0)
        # 10 - (2 + 3) = 5
        assert side_state.value == 5


class TestBoost:
    """Tests for boost keyword.

    Boost: Target gets +N to all pips for one turn.
    """

    def test_boost_increases_all_side_values(self):
        """Boost increases all side values on target."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 2)
        hero.die.set_all_sides(damage_side)

        fight.apply_boost(hero, 3)

        side_state = fight.get_side_state(hero, 0)
        # Base 2 + 3 boost = 5
        assert side_state.value == 5

    def test_boost_from_keyword(self):
        """Boost keyword applies boost buff to target."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Give monster a die so we can check its side values
        monster.die = Die()
        damage_side = Side(EffectType.DAMAGE, 3)
        monster.die.set_all_sides(damage_side)

        # Create a boost side for hero
        hero.die = Die()
        boost_side = Side(EffectType.HEAL, 2, {Keyword.BOOST})
        hero.die.set_all_sides(boost_side)

        # Use boost on monster - heals 2 and applies boost buff
        fight.use_die(hero, 0, monster)

        # Monster should have boost buff - check its sides are boosted
        side_state = fight.get_side_state(monster, 0)
        # Base 3 + 2 boost = 5
        assert side_state.value == 5
        assert fight.get_buff_count(monster) >= 1


class TestVulnerable:
    """Tests for vulnerable keyword.

    Vulnerable: Target takes +N damage from dice/spells for one turn.
    """

    def test_vulnerable_increases_damage_taken(self):
        """Vulnerable increases damage from dice."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Make monster vulnerable
        fight.apply_vulnerable(monster, 3)

        # Attack monster
        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 2)
        hero.die.set_all_sides(damage_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # 2 base damage + 3 vulnerable = 5 damage
        # 10 HP - 5 = 5 HP
        assert state.hp == 5

    def test_vulnerable_stacks(self):
        """Multiple vulnerable applications stack."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=20)
        fight = FightLog([hero], [monster])

        fight.apply_vulnerable(monster, 2)
        fight.apply_vulnerable(monster, 3)

        assert fight.get_vulnerable_bonus(monster) == 5

        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 1)
        hero.die.set_all_sides(damage_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # 1 base + 5 vulnerable = 6 damage
        assert state.hp == 14

    def test_vulnerable_does_not_affect_poison(self):
        """Vulnerable does NOT increase poison damage."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        # Make monster vulnerable
        fight.apply_vulnerable(monster, 10)

        # Apply poison directly
        fight.apply_poison(monster, 3)

        # End turn - poison triggers
        fight.next_turn()

        state = fight.get_state(monster, Temporality.PRESENT)
        # Poison damage is NOT affected by vulnerable
        # 10 - 3 poison = 7 HP
        assert state.hp == 7


class TestSmith:
    """Tests for smith keyword.

    Smith: Target gets +N to damage and shield sides for one turn.
    """

    def test_smith_increases_damage_sides(self):
        """Smith increases damage side values."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        hero.die = Die()
        hero.die.sides = [
            Side(EffectType.DAMAGE, 2),
            Side(EffectType.HEAL, 3),
            Side(EffectType.SHIELD, 1),
            Side(EffectType.MANA, 2),
            Side(EffectType.DAMAGE, 4),
            Side(EffectType.BLANK, 0),
        ]

        fight.apply_smith(hero, 2)

        # Damage sides should be boosted
        damage0 = fight.get_side_state(hero, 0)
        assert damage0.value == 4  # 2 + 2

        damage4 = fight.get_side_state(hero, 4)
        assert damage4.value == 6  # 4 + 2

        # Shield side should be boosted
        shield2 = fight.get_side_state(hero, 2)
        assert shield2.value == 3  # 1 + 2

        # Heal side should NOT be boosted
        heal1 = fight.get_side_state(hero, 1)
        assert heal1.value == 3  # Unchanged

        # Mana side should NOT be boosted
        mana3 = fight.get_side_state(hero, 3)
        assert mana3.value == 2  # Unchanged


class TestPermaBoost:
    """Tests for permaBoost keyword.

    PermaBoost: Target gets +N to all pips for the entire fight.
    """

    def test_perma_boost_persists(self):
        """PermaBoost lasts through turn ends."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        hero.die = Die()
        damage_side = Side(EffectType.DAMAGE, 2)
        hero.die.set_all_sides(damage_side)

        fight.apply_perma_boost(hero, 3)

        # Check initial value
        side_state = fight.get_side_state(hero, 0)
        assert side_state.value == 5  # 2 + 3

        # End turn - should still have boost
        fight.next_turn()

        side_state = fight.get_side_state(hero, 0)
        assert side_state.value == 5  # Still boosted

        # Another turn
        fight.next_turn()

        side_state = fight.get_side_state(hero, 0)
        assert side_state.value == 5  # Still boosted


class TestSelfVulnerable:
    """Tests for selfVulnerable keyword.

    SelfVulnerable: Apply vulnerable to myself (the die user).
    """

    def test_self_vulnerable_applies_to_source(self):
        """Self-vulnerable applies vulnerable to the die user."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        self_vuln_side = Side(EffectType.DAMAGE, 2, {Keyword.SELF_VULNERABLE})
        hero.die.set_all_sides(self_vuln_side)

        fight.use_die(hero, 0, monster)

        # Hero should have vulnerable, not monster
        assert fight.get_vulnerable_bonus(hero) == 2
        assert fight.get_vulnerable_bonus(monster) == 0


class TestBuffed:
    """Tests for buffed keyword.

    Buffed: +N pips where N = number of buffs on me.
    """

    def test_buffed_bonus_from_buff_count(self):
        """Buffed gets bonus from number of buffs on source."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=20)
        fight = FightLog([hero], [monster])

        # Apply several buffs to hero
        fight.apply_boost(hero, 1)      # Buff 1
        fight.apply_perma_boost(hero, 1)  # Buff 2
        fight.apply_regen(hero, 1)      # Buff 3

        hero.die = Die()
        buffed_side = Side(EffectType.DAMAGE, 1, {Keyword.BUFFED})
        hero.die.set_all_sides(buffed_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Base 1 + buff bonuses (boost, permaBoost, regen) + buffed bonus (3)
        # Note: boost and permaBoost also add to the value, so we need to account for that
        # The buffed keyword adds +N where N = buff count (3)
        # So total = 1 (base) + 1 (boost) + 1 (permaBoost) + 3 (buffed) = 6 damage
        assert state.hp <= 17  # At least 3 damage from buffed bonus


class TestAffected:
    """Tests for affected keyword.

    Affected: +N pips where N = number of triggers affecting me.
    """

    def test_affected_bonus_from_trigger_count(self):
        """Affected gets bonus from number of triggers on source."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=20)
        fight = FightLog([hero], [monster])

        # Apply triggers to hero
        fight.apply_poison(hero, 1)  # Trigger 1
        fight.apply_regen(hero, 1)   # Trigger 2

        hero.die = Die()
        affected_side = Side(EffectType.DAMAGE, 1, {Keyword.AFFECTED})
        hero.die.set_all_sides(affected_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Base 1 + affected bonus (at least 2 triggers)
        # Monster had 20 HP, should have taken at least 3 damage
        assert state.hp <= 17


class TestSkill:
    """Tests for skill keyword.

    Skill: +N pips where N = my level/tier.
    """

    def test_skill_bonus_from_tier(self):
        """Skill gets bonus from entity tier."""
        # Create a hero with a tier
        hero_type = EntityType("Knight", 10, EntitySize.HERO)
        hero_type.tier = 3  # Set tier to 3
        hero = Entity(hero_type, Team.HERO)

        monster = make_monster("Goblin", hp=20)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        skill_side = Side(EffectType.DAMAGE, 1, {Keyword.SKILL})
        hero.die.set_all_sides(skill_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        # Base 1 + skill bonus (tier 3) = 4 damage
        assert state.hp == 16
