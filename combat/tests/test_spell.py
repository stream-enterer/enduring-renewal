"""Tests for spell system infrastructure."""

import pytest
from src.entity import Entity, EntityType, EntitySize, Team
from src.effects import EffectType
from src.dice import Keyword
from src.spell import Spell, SpellState, SpellEffect, QueuedSpell
from src.fight import FightLog


def make_hero(name: str, hp: int = 5) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)


def make_monster(name: str, hp: int = 4) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)


def make_damage_spell(name: str, cost: int, damage: int, keywords: set = None) -> Spell:
    """Create a damage spell for testing."""
    return Spell(
        name=name,
        base_cost=cost,
        effect=SpellEffect(
            effect_type=EffectType.DAMAGE,
            value=damage,
            keywords=keywords or set(),
            target_friendly=False
        )
    )


def make_heal_spell(name: str, cost: int, heal: int, keywords: set = None) -> Spell:
    """Create a heal spell for testing."""
    return Spell(
        name=name,
        base_cost=cost,
        effect=SpellEffect(
            effect_type=EffectType.HEAL,
            value=heal,
            keywords=keywords or set(),
            target_friendly=True
        )
    )


class TestSpellState:
    """Test SpellState class."""

    def test_initial_state(self):
        """SpellState starts with no casts."""
        spell = make_damage_spell("Fireball", 2, 3)
        state = SpellState(spell)

        assert state.cast_count_this_fight == 0
        assert state.cast_count_this_turn == 0
        assert state.cost_modifier == 0

    def test_get_current_cost(self):
        """Cost returns base cost with no modifiers."""
        spell = make_damage_spell("Fireball", 2, 3)
        state = SpellState(spell)

        assert state.get_current_cost() == 2

    def test_cost_minimum_is_one(self):
        """Cost can never go below 1."""
        spell = make_damage_spell("Fireball", 2, 3)
        state = SpellState(spell)
        state.cost_modifier = -10  # Way below base cost

        assert state.get_current_cost() == 1

    def test_on_cast_increments_counts(self):
        """on_cast increments both turn and fight counts."""
        spell = make_damage_spell("Fireball", 2, 3)
        state = SpellState(spell)

        state.on_cast()

        assert state.cast_count_this_fight == 1
        assert state.cast_count_this_turn == 1

    def test_start_turn_resets_turn_count(self):
        """start_turn resets turn count but not fight count."""
        spell = make_damage_spell("Fireball", 2, 3)
        state = SpellState(spell)

        state.on_cast()
        state.start_turn()

        assert state.cast_count_this_fight == 1
        assert state.cast_count_this_turn == 0


class TestSingleCast:
    """Test singleCast keyword - spell can only be cast once per fight."""

    def test_single_cast_available_initially(self):
        """singleCast spell is available before being cast."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.SINGLE_CAST})
        state = SpellState(spell)

        assert state.is_available() is True

    def test_single_cast_unavailable_after_cast(self):
        """singleCast spell is unavailable after being cast."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.SINGLE_CAST})
        state = SpellState(spell)

        state.on_cast()

        assert state.is_available() is False

    def test_single_cast_stays_unavailable_after_turn(self):
        """singleCast spell stays unavailable after turn ends."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.SINGLE_CAST})
        state = SpellState(spell)

        state.on_cast()
        state.start_turn()

        assert state.is_available() is False


class TestCooldown:
    """Test cooldown keyword - spell can only be cast once per turn."""

    def test_cooldown_available_initially(self):
        """cooldown spell is available before being cast."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.COOLDOWN})
        state = SpellState(spell)

        assert state.is_available() is True

    def test_cooldown_unavailable_after_cast(self):
        """cooldown spell is unavailable after being cast this turn."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.COOLDOWN})
        state = SpellState(spell)

        state.on_cast()

        assert state.is_available() is False

    def test_cooldown_available_next_turn(self):
        """cooldown spell is available again next turn."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.COOLDOWN})
        state = SpellState(spell)

        state.on_cast()
        state.start_turn()

        assert state.is_available() is True


class TestDeplete:
    """Test deplete keyword - spell costs +1 mana each time it is cast."""

    def test_deplete_increases_cost_after_cast(self):
        """deplete spell costs more after each cast."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.DEPLETE})
        state = SpellState(spell)

        assert state.get_current_cost() == 2

        state.on_cast()
        assert state.get_current_cost() == 3

        state.on_cast()
        assert state.get_current_cost() == 4

    def test_deplete_cost_persists_across_turns(self):
        """deplete cost increase persists across turns."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.DEPLETE})
        state = SpellState(spell)

        state.on_cast()
        state.start_turn()

        assert state.get_current_cost() == 3


class TestChannel:
    """Test channel keyword - spell costs -1 mana each time it is cast (minimum 1)."""

    def test_channel_decreases_cost_after_cast(self):
        """channel spell costs less after each cast."""
        spell = make_damage_spell("Fireball", 3, 3, {Keyword.CHANNEL})
        state = SpellState(spell)

        assert state.get_current_cost() == 3

        state.on_cast()
        assert state.get_current_cost() == 2

        state.on_cast()
        assert state.get_current_cost() == 1

    def test_channel_minimum_cost_is_one(self):
        """channel spell cost cannot go below 1."""
        spell = make_damage_spell("Fireball", 2, 3, {Keyword.CHANNEL})
        state = SpellState(spell)

        state.on_cast()  # Cost goes to 1
        state.on_cast()  # Cost stays at 1 (minimum)

        assert state.get_current_cost() == 1

    def test_channel_cost_persists_across_turns(self):
        """channel cost decrease persists across turns."""
        spell = make_damage_spell("Fireball", 3, 3, {Keyword.CHANNEL})
        state = SpellState(spell)

        state.on_cast()
        state.start_turn()

        assert state.get_current_cost() == 2


class TestFightLogSpellMethods:
    """Test FightLog spell infrastructure."""

    def test_get_spell_cost(self):
        """get_spell_cost returns the current spell cost."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])

        spell = make_damage_spell("Fireball", 2, 3)
        cost = fight.get_spell_cost(hero, spell)

        assert cost == 2

    def test_is_spell_available(self):
        """is_spell_available returns True for new spells."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])

        spell = make_damage_spell("Fireball", 2, 3)
        available = fight.is_spell_available(hero, spell)

        assert available is True

    def test_can_cast_spell_with_mana(self):
        """can_cast_spell returns True with enough mana."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 3

        spell = make_damage_spell("Fireball", 2, 3)
        can_cast = fight.can_cast_spell(hero, spell)

        assert can_cast is True

    def test_can_cast_spell_without_mana(self):
        """can_cast_spell returns False without enough mana."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 1

        spell = make_damage_spell("Fireball", 2, 3)
        can_cast = fight.can_cast_spell(hero, spell)

        assert can_cast is False

    def test_cast_spell_deducts_mana(self):
        """cast_spell deducts the spell cost from mana."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        spell = make_damage_spell("Fireball", 2, 3)
        fight.cast_spell(hero, spell, monster)

        assert fight._total_mana == 3

    def test_cast_spell_deals_damage(self):
        """cast_spell applies damage effect."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        spell = make_damage_spell("Fireball", 2, 3)
        fight.cast_spell(hero, spell, monster)

        from src.fight import Temporality
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 2  # 5 - 3 = 2

    def test_cast_heal_spell(self):
        """cast_spell applies heal effect."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # Damage the hero first
        fight.apply_damage(monster, hero, 4)

        spell = make_heal_spell("Heal", 2, 3)
        fight.cast_spell(hero, spell, hero)

        from src.fight import Temporality
        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 9  # 10 - 4 + 3 = 9

    def test_cast_single_cast_twice_fails(self):
        """Casting a singleCast spell twice fails."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 10

        spell = make_damage_spell("Fireball", 2, 3, {Keyword.SINGLE_CAST})

        result1 = fight.cast_spell(hero, spell, monster)
        result2 = fight.cast_spell(hero, spell, monster)

        assert result1 is True
        assert result2 is False
        assert fight._total_mana == 8  # Only first cast deducted mana

    def test_cast_cooldown_twice_same_turn_fails(self):
        """Casting a cooldown spell twice in same turn fails."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 10

        spell = make_damage_spell("Fireball", 2, 3, {Keyword.COOLDOWN})

        result1 = fight.cast_spell(hero, spell, monster)
        result2 = fight.cast_spell(hero, spell, monster)

        assert result1 is True
        assert result2 is False

    def test_cooldown_resets_next_turn(self):
        """cooldown spell can be cast again next turn."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=15)  # High HP to survive
        fight = FightLog([hero], [monster])
        fight._total_mana = 10

        spell = make_damage_spell("Fireball", 2, 3, {Keyword.COOLDOWN})

        result1 = fight.cast_spell(hero, spell, monster)
        fight.next_turn()
        result2 = fight.cast_spell(hero, spell, monster)

        assert result1 is True
        assert result2 is True

    def test_deplete_increases_cost_in_fight(self):
        """deplete spell costs more each time in a fight."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=15)
        fight = FightLog([hero], [monster])
        fight._total_mana = 20

        spell = make_damage_spell("Fireball", 2, 3, {Keyword.DEPLETE})

        # First cast costs 2
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 18

        # Second cast costs 3
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 15

        # Third cast costs 4
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 11

    def test_channel_decreases_cost_in_fight(self):
        """channel spell costs less each time in a fight."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=15)
        fight = FightLog([hero], [monster])
        fight._total_mana = 20

        spell = make_damage_spell("Fireball", 3, 3, {Keyword.CHANNEL})

        # First cast costs 3
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 17

        # Second cast costs 2
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 15

        # Third cast costs 1 (minimum)
        fight.cast_spell(hero, spell, monster)
        assert fight._total_mana == 14


class TestFuture:
    """Test future keyword - effect is delayed until start of next turn."""

    def test_future_queues_effect(self):
        """future spell queues effect instead of immediate application."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        spell = make_damage_spell("Delayed Blast", 2, 3, {Keyword.FUTURE})
        fight.cast_spell(hero, spell, monster)

        # Effect not applied yet
        from src.fight import Temporality
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 5  # No damage yet

        # Queue should have the spell
        assert len(fight._future_queue) == 1

    def test_future_applies_at_turn_start(self):
        """future spell effect applies at start of next turn."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=5)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        spell = make_damage_spell("Delayed Blast", 2, 3, {Keyword.FUTURE})
        fight.cast_spell(hero, spell, monster)

        # Advance to next turn
        fight.next_turn()

        # Now effect should be applied
        from src.fight import Temporality
        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.hp == 2  # 5 - 3 = 2

    def test_future_clears_queue_after_application(self):
        """future queue is cleared after effects are applied."""
        hero = make_hero("Fighter", hp=10)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        spell = make_damage_spell("Delayed Blast", 2, 3, {Keyword.FUTURE})
        fight.cast_spell(hero, spell, monster)

        fight.next_turn()

        assert len(fight._future_queue) == 0


class TestQueuedSpell:
    """Test QueuedSpell helper class."""

    def test_tick_decrements_counter(self):
        """tick decrements turns_remaining."""
        spell = make_damage_spell("Fireball", 2, 3)
        hero = make_hero("Fighter")
        queued = QueuedSpell(spell, hero, None, turns_remaining=2)

        result = queued.tick()

        assert queued.turns_remaining == 1
        assert result is False  # Not ready yet

    def test_tick_returns_true_when_ready(self):
        """tick returns True when turns_remaining reaches 0."""
        spell = make_damage_spell("Fireball", 2, 3)
        hero = make_hero("Fighter")
        queued = QueuedSpell(spell, hero, None, turns_remaining=1)

        result = queued.tick()

        assert queued.turns_remaining == 0
        assert result is True  # Ready to execute


class TestSpellRescue:
    """Test spellRescue keyword - mana refunded if spell saves a hero."""

    def test_spell_rescue_refunds_when_heal_saves_hero(self):
        """spellRescue refunds mana when heal saves a hero from pending damage."""
        hero = make_hero("Fighter", hp=10)  # Higher max HP so heal isn't capped
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # Damage hero first so they have room to heal
        fight.apply_damage(monster, hero, 5)  # Hero now at 5 HP

        # Monster attacks hero again - pending damage will kill them
        fight.apply_damage(monster, hero, 6, is_pending=True)

        # Verify hero would die from pending damage (5 - 6 = -1)
        from src.fight import Temporality
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert future_state.is_dead  # Would die from pending damage

        # Cast spellRescue heal to save the hero
        spell = make_heal_spell("Rescue Heal", 2, 3, {Keyword.SPELL_RESCUE})
        fight.cast_spell(hero, spell, hero)

        # Hero should now survive in future (5 HP + 3 heal - 6 damage = 2 HP)
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert not future_state.is_dead

        # Mana should be refunded: started with 5, spent 2, refunded 2 = 5
        assert fight._total_mana == 5

    def test_spell_rescue_refunds_when_shield_saves_hero(self):
        """spellRescue refunds mana when shield saves a hero from pending damage."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # Monster attacks hero - pending damage will kill them
        fight.apply_damage(monster, hero, 6, is_pending=True)

        # Verify hero would die from pending damage
        from src.fight import Temporality
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert future_state.is_dead

        # Cast spellRescue shield to save the hero
        spell = make_shield_spell("Rescue Shield", 2, 3, {Keyword.SPELL_RESCUE})
        fight.cast_spell(hero, spell, hero)

        # Hero should now survive (shield blocks pending damage)
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert not future_state.is_dead

        # Mana should be refunded
        assert fight._total_mana == 5

    def test_spell_rescue_no_refund_when_hero_wasnt_dying(self):
        """spellRescue does not refund when hero wasn't going to die."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # No pending damage - hero is safe
        from src.fight import Temporality
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert not future_state.is_dead

        # Cast spellRescue heal (but hero didn't need saving)
        spell = make_heal_spell("Rescue Heal", 2, 3, {Keyword.SPELL_RESCUE})
        fight.cast_spell(hero, spell, hero)

        # Mana should NOT be refunded: started with 5, spent 2 = 3
        assert fight._total_mana == 3

    def test_spell_rescue_no_refund_when_heal_not_enough(self):
        """spellRescue does not refund when heal wasn't enough to save hero."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # Monster attacks hero - pending damage will kill them (massive damage)
        fight.apply_damage(monster, hero, 10, is_pending=True)

        # Verify hero would die from pending damage
        from src.fight import Temporality
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert future_state.is_dead  # 5 HP - 10 damage = -5

        # Cast spellRescue heal (but only 3 heal isn't enough to save from 10 damage)
        spell = make_heal_spell("Rescue Heal", 2, 3, {Keyword.SPELL_RESCUE})
        fight.cast_spell(hero, spell, hero)

        # Hero still dies (5 + 3 - 10 = -2)
        future_state = fight.get_state(hero, Temporality.FUTURE)
        assert future_state.is_dead

        # Mana should NOT be refunded
        assert fight._total_mana == 3

    def test_regular_heal_does_not_refund_mana(self):
        """Regular heal spell (without spellRescue) never refunds mana."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])
        fight._total_mana = 5

        # Monster attacks hero - pending damage will kill them
        fight.apply_damage(monster, hero, 6, is_pending=True)

        # Cast regular heal (no spellRescue keyword)
        spell = make_heal_spell("Regular Heal", 2, 3)  # No SPELL_RESCUE
        fight.cast_spell(hero, spell, hero)

        # Mana should NOT be refunded even though we saved the hero
        assert fight._total_mana == 3


def make_shield_spell(name: str, cost: int, shield: int, keywords: set = None) -> Spell:
    """Create a shield spell for testing."""
    return Spell(
        name=name,
        base_cost=cost,
        effect=SpellEffect(
            effect_type=EffectType.SHIELD,
            value=shield,
            keywords=keywords or set(),
            target_friendly=True
        )
    )
