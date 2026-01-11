#!/usr/bin/env python3
"""Basic combat loop - can we run a fight without crashing?

This is the first test of system coherency. If this runs and produces
sensible output, the core architecture is sound.

Usage:
    cd combat && uv run python -m examples.basic_combat

Or:
    cd combat && uv run python examples/basic_combat.py
"""

import sys
from pathlib import Path

# Add the combat directory to path so we can import src as a package
combat_dir = Path(__file__).parent.parent
sys.path.insert(0, str(combat_dir))

from src.entity import Entity, EntityType, EntitySize, Team, FIGHTER, GOBLIN
from src.dice import Die, Side, Keyword, create_fighter_die
from src.effects import EffectType
from src.fight import FightLog, Temporality


def create_goblin_die() -> Die:
    """Create a simple goblin die - mostly damage."""
    return Die([
        Side(EffectType.DAMAGE, 1),
        Side(EffectType.DAMAGE, 1),
        Side(EffectType.DAMAGE, 2),
        Side(EffectType.DAMAGE, 2),
        Side(EffectType.BLANK, 0),
        Side(EffectType.DAMAGE, 3),
    ])


def print_state(fight: FightLog, label: str = ""):
    """Print current state of all entities."""
    if label:
        print(f"\n=== {label} ===")

    print("Heroes:")
    for hero in fight.heroes:
        state = fight.get_state(hero, Temporality.PRESENT)
        status = "DEAD" if state.is_dead else f"{state.hp}/{state.max_hp}hp"
        shield_str = f" +{state.shield}sh" if state.shield > 0 else ""
        used_str = " (used)" if state.is_used() else ""
        print(f"  {hero.entity_type.name}: {status}{shield_str}{used_str}")

    print("Monsters:")
    for monster in fight.monsters:
        state = fight.get_state(monster, Temporality.PRESENT)
        status = "DEAD" if state.is_dead else f"{state.hp}/{state.max_hp}hp"
        shield_str = f" +{state.shield}sh" if state.shield > 0 else ""
        print(f"  {monster.entity_type.name}: {status}")

    print(f"Mana: {fight.get_total_mana()}")


def run_basic_combat():
    """Run a simple fight: Fighter vs Goblin."""
    print("=" * 50)
    print("BASIC COMBAT TEST: Fighter vs Goblin")
    print("=" * 50)

    # Create entities
    fighter = Entity(FIGHTER, Team.HERO)
    fighter.die = create_fighter_die()

    goblin = Entity(GOBLIN, Team.MONSTER)
    goblin.die = create_goblin_die()

    # Create fight
    fight = FightLog([fighter], [goblin])

    print_state(fight, "Initial State")

    turn = 0
    max_turns = 10  # Safety limit

    while turn < max_turns:
        turn += 1
        print(f"\n--- Turn {turn} ---")

        # Check victory/defeat conditions
        if fight.is_victory(Temporality.PRESENT):
            print("VICTORY! All monsters defeated.")
            break

        fighter_state = fight.get_state(fighter, Temporality.PRESENT)
        if fighter_state.is_dead:
            print("DEFEAT! Fighter has fallen.")
            break

        # Hero phase: Fighter attacks
        if not fighter_state.is_used():
            goblin_state = fight.get_state(goblin, Temporality.PRESENT)
            if not goblin_state.is_dead:
                # Use side 5 (strongest attack: 3 damage)
                print(f"  Fighter attacks Goblin with side 5 (3 damage)")
                fight.use_die(fighter, 5, goblin)

        # Monster phase: Goblin attacks
        goblin_state = fight.get_state(goblin, Temporality.PRESENT)
        if not goblin_state.is_dead:
            print(f"  Goblin attacks Fighter with side 5 (3 damage)")
            fight.use_die(goblin, 5, fighter)

        print_state(fight)

        # End turn
        fight.next_turn()

        # Recharge dice for next turn
        fight.recharge_die(fighter)
        fight.recharge_die(goblin)

    if turn >= max_turns:
        print(f"\nReached turn limit ({max_turns})")

    print("\n" + "=" * 50)
    print("Combat complete!")
    print("=" * 50)


def run_keyword_combat():
    """Run a fight with keyword-enhanced dice."""
    print("\n" + "=" * 50)
    print("KEYWORD COMBAT TEST: Fighter (engage) vs Goblin")
    print("=" * 50)

    # Create entities
    fighter = Entity(FIGHTER, Team.HERO)

    # Custom die with ENGAGE keyword (x2 vs full HP)
    engage_sword = Side(EffectType.DAMAGE, 2, {Keyword.ENGAGE})
    fighter.die = Die([engage_sword] * 6)

    goblin = Entity(GOBLIN, Team.MONSTER)
    goblin.die = create_goblin_die()

    fight = FightLog([fighter], [goblin])

    print_state(fight, "Initial State")

    # Fighter attacks with engage (should do 4 damage: 2 base * 2 engage)
    print("\nFighter attacks Goblin with engage sword (2 base, x2 vs full HP)")
    fight.use_die(fighter, 0, goblin)

    goblin_state = fight.get_state(goblin, Temporality.PRESENT)
    expected_hp = 3 - 4  # 3hp goblin - 4 damage = -1 (dead)
    print(f"  Goblin HP: {goblin_state.hp} (expected: dead)")

    if goblin_state.is_dead:
        print("  ENGAGE worked correctly - goblin killed in one hit!")
    else:
        print(f"  WARNING: Goblin survived with {goblin_state.hp}hp")

    print_state(fight, "Final State")


def run_multi_entity_combat():
    """Run a fight with multiple heroes and monsters."""
    print("\n" + "=" * 50)
    print("MULTI-ENTITY COMBAT: 2 Heroes vs 2 Goblins")
    print("=" * 50)

    # Create heroes
    fighter = Entity(FIGHTER, Team.HERO)
    fighter.die = create_fighter_die()

    healer_type = EntityType("Healer", 4, EntitySize.HERO)
    healer = Entity(healer_type, Team.HERO)
    heal_side = Side(EffectType.HEAL, 2)
    healer.die = Die([heal_side] * 6)

    # Create monsters
    goblin1 = Entity(GOBLIN, Team.MONSTER, position=0)
    goblin1.die = create_goblin_die()

    goblin2 = Entity(GOBLIN, Team.MONSTER, position=1)
    goblin2.die = create_goblin_die()

    fight = FightLog([fighter, healer], [goblin1, goblin2])

    print_state(fight, "Initial State")

    turn = 0
    max_turns = 10

    while turn < max_turns:
        turn += 1
        print(f"\n--- Turn {turn} ---")

        if fight.is_victory(Temporality.PRESENT):
            print("VICTORY!")
            break

        # Check if all heroes dead
        all_dead = all(
            fight.get_state(h, Temporality.PRESENT).is_dead
            for h in fight.heroes
        )
        if all_dead:
            print("DEFEAT!")
            break

        # Hero phase
        fighter_state = fight.get_state(fighter, Temporality.PRESENT)
        if not fighter_state.is_dead and not fighter_state.is_used():
            # Find first alive goblin
            for goblin in [goblin1, goblin2]:
                gs = fight.get_state(goblin, Temporality.PRESENT)
                if not gs.is_dead:
                    print(f"  Fighter attacks {goblin.entity_type.name}")
                    fight.use_die(fighter, 5, goblin)  # 3 damage
                    break

        healer_state = fight.get_state(healer, Temporality.PRESENT)
        if not healer_state.is_dead and not healer_state.is_used():
            # Heal the fighter if damaged
            if fighter_state.hp < fighter_state.max_hp and not fighter_state.is_dead:
                print(f"  Healer heals Fighter")
                fight.use_die(healer, 0, fighter)  # 2 heal

        # Monster phase
        for goblin in [goblin1, goblin2]:
            gs = fight.get_state(goblin, Temporality.PRESENT)
            if not gs.is_dead:
                # Attack the fighter
                if not fighter_state.is_dead:
                    print(f"  {goblin.entity_type.name} attacks Fighter")
                    fight.use_die(goblin, 3, fighter)  # 2 damage

        print_state(fight)

        # End turn
        fight.next_turn()

        # Recharge all dice
        for entity in [fighter, healer, goblin1, goblin2]:
            state = fight.get_state(entity, Temporality.PRESENT)
            if not state.is_dead:
                fight.recharge_die(entity)

    print("\n" + "=" * 50)
    print("Multi-entity combat complete!")


def run_status_effect_combat():
    """Run a fight with poison and regen."""
    print("\n" + "=" * 50)
    print("STATUS EFFECT TEST: Poison & Regen")
    print("=" * 50)

    fighter = Entity(EntityType("Fighter", 10, EntitySize.HERO), Team.HERO)
    poison_sword = Side(EffectType.DAMAGE, 1, {Keyword.POISON})
    fighter.die = Die([poison_sword] * 6)

    goblin = Entity(EntityType("ToughGoblin", 8, EntitySize.HERO), Team.MONSTER)
    goblin.die = create_goblin_die()

    fight = FightLog([fighter], [goblin])

    print_state(fight, "Initial State")

    # Apply poison
    print("\nFighter attacks with poison sword (1 damage + 1 poison)")
    fight.use_die(fighter, 0, goblin)

    goblin_state = fight.get_state(goblin, Temporality.PRESENT)
    poison = goblin_state.get_poison_damage_taken()
    print(f"  Goblin: {goblin_state.hp}hp, {poison} poison stacks")

    # Apply regen to fighter
    print("\nApplying regen to Fighter...")
    fight.apply_damage(goblin, fighter, 3)  # Damage first
    fight.apply_regen(fighter, 2)

    fighter_state = fight.get_state(fighter, Temporality.PRESENT)
    print(f"  Fighter: {fighter_state.hp}hp, {fighter_state.regen} regen")

    print("\n--- End of turn (poison & regen trigger) ---")
    fight.next_turn()

    goblin_state = fight.get_state(goblin, Temporality.PRESENT)
    fighter_state = fight.get_state(fighter, Temporality.PRESENT)

    print(f"  Goblin after poison: {goblin_state.hp}hp")
    print(f"  Fighter after regen: {fighter_state.hp}hp")

    print_state(fight, "Final State")


if __name__ == "__main__":
    try:
        run_basic_combat()
        run_keyword_combat()
        run_multi_entity_combat()
        run_status_effect_combat()

        print("\n" + "=" * 50)
        print("ALL COMBAT TESTS COMPLETED SUCCESSFULLY")
        print("=" * 50)

    except Exception as e:
        print(f"\n!!! COMBAT LOOP CRASHED !!!")
        print(f"Error: {type(e).__name__}: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
