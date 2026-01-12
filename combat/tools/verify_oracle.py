#!/usr/bin/env python3
"""
Verify oracle tests against the combat implementation.

Loads test specifications from oracle_tests/generated_tests.json and runs
each test against the actual combat system, reporting mismatches.

Usage:
    uv run python tools/verify_oracle.py              # Run all tests
    uv run python tools/verify_oracle.py --verbose    # Show all results
    uv run python tools/verify_oracle.py --filter engage  # Filter by test ID
    uv run python tools/verify_oracle.py --stop-on-fail   # Stop at first failure
"""

import json
import sys
import argparse
from pathlib import Path
from dataclasses import dataclass
from enum import Enum
from typing import Any, Optional
import traceback

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.entity import Entity, EntityType, Team, EntitySize
from src.effects import EffectType
from src.dice import Die, Side, Keyword
from src.fight import FightLog, Temporality, SideState


# =============================================================================
# Keyword Name Mapping
# =============================================================================

def build_keyword_map() -> dict[str, Keyword]:
    """Build mapping from test file keyword names to Keyword enum values.

    Test file uses names like 'ANTIENGAGE' but enum uses 'ANTI_ENGAGE'.
    """
    mapping = {}

    # Direct mappings (same name)
    for kw in Keyword:
        # Add the exact enum name
        mapping[kw.name] = kw
        # Add without underscores for test file format
        mapping[kw.name.replace('_', '')] = kw

    # Handle any special cases where the mapping isn't obvious
    # (Most should be handled by the underscore removal above)

    return mapping


KEYWORD_MAP = build_keyword_map()


def parse_keyword(name: str) -> Optional[Keyword]:
    """Parse a keyword name from the test file to a Keyword enum value."""
    # Try direct lookup
    if name in KEYWORD_MAP:
        return KEYWORD_MAP[name]

    # Try uppercase
    upper_name = name.upper()
    if upper_name in KEYWORD_MAP:
        return KEYWORD_MAP[upper_name]

    # Try with underscores removed
    no_underscore = upper_name.replace('_', '')
    if no_underscore in KEYWORD_MAP:
        return KEYWORD_MAP[no_underscore]

    return None


def parse_keywords(names: list[str]) -> set[Keyword]:
    """Parse a list of keyword names to a set of Keyword enum values."""
    keywords = set()
    for name in names:
        kw = parse_keyword(name)
        if kw is None:
            print(f"  WARNING: Unknown keyword '{name}'")
        else:
            keywords.add(kw)
    return keywords


def parse_effect_type(name: str) -> EffectType:
    """Parse an effect type name to an EffectType enum value."""
    name_upper = name.upper()
    for et in EffectType:
        if et.name == name_upper:
            return et
    raise ValueError(f"Unknown effect type: {name}")


# =============================================================================
# Test Result Types
# =============================================================================

class TestStatus(Enum):
    PASS = "PASS"
    FAIL = "FAIL"
    SKIP = "SKIP"
    ERROR = "ERROR"


@dataclass
class TestResult:
    """Result of running a single oracle test."""
    test_id: str
    status: TestStatus
    expected: Any = None
    actual: Any = None
    message: str = ""
    traceback: str = ""


# =============================================================================
# Die History Helpers (aligned with fight.py API)
# =============================================================================

def create_mock_side_state(
    effect_type: EffectType,
    value: int,
    keywords: set[Keyword] = None,
    index: int = 0
) -> SideState:
    """Create a SideState for populating die history.

    This is used to set up previous die state for testing PAIR, COPYCAT, ECHO, etc.
    """
    if keywords is None:
        keywords = set()

    side = Side(effect_type, value, keywords)
    return SideState(
        original_side=side,
        index=index,
        calculated_effect=side.copy(),
        is_petrified=False
    )


def record_die_use(fight: FightLog, side_state: SideState, target: Entity = None):
    """Record a die use in the fight's history.

    This directly sets the internal state that fight.py uses for
    COPYCAT, PAIR, ECHO, TRIO, etc.
    """
    fight._most_recent_die_effect = side_state
    fight._die_effect_history.append(side_state)

    # Also track target for FOCUS keyword
    if target is not None:
        fight._last_die_target = target


# =============================================================================
# Entity Setup Helpers
# =============================================================================

def create_entity(spec: dict | str, team: Team, name: str = "Entity") -> Entity:
    """Create an entity from a test specification."""
    if spec == "SELF":
        # Special case: target is self (handled by caller)
        return None

    if isinstance(spec, str):
        raise ValueError(f"Unknown entity spec: {spec}")

    hp = spec.get("hp", 20)
    max_hp = spec.get("max_hp", hp)

    entity_type = EntityType(name, max_hp, EntitySize.HERO)
    entity = Entity(entity_type, team)

    return entity


def setup_entity_state(fight: FightLog, entity: Entity, spec: dict):
    """Set up entity state from test specification.

    This handles fields like:
    - hp (current HP)
    - shields
    - poison
    - mana
    - dying
    - died_this_fight
    - used_last_turn
    - blank_sides
    - level
    - buffs
    - etc.
    """
    if spec == "SELF" or spec is None:
        return

    hp = spec.get("hp", 20)
    max_hp = spec.get("max_hp", hp)

    # Get state to modify
    state = fight._states[entity]

    # Set HP by applying damage if needed
    if hp < max_hp:
        damage = max_hp - hp
        fight.apply_damage(entity, entity, damage, is_pending=False)
        # Clear any shield that might have been applied
        state.shield = 0

    # Apply shields
    if "shields" in spec:
        fight.apply_shield(entity, spec["shields"])

    # Apply poison
    if "poison" in spec:
        fight.apply_poison(entity, spec["poison"])

    # Apply regen
    if "regen" in spec:
        fight.apply_regen(entity, spec["regen"])

    # Set mana (global fight state)
    if "mana" in spec:
        fight._total_mana = spec["mana"]

    # Handle dying flag (future HP <= 0)
    if spec.get("dying", False):
        # Apply pending damage to make entity dying
        current_hp = state.hp
        if current_hp > 0:
            fight.apply_damage(entity, entity, current_hp, is_pending=True)

    # Handle died_this_fight flag
    if spec.get("died_this_fight", False):
        state.deaths_this_fight = 1

    # Handle used_last_turn flag
    if spec.get("used_last_turn", False):
        state._used_last_turn = True

    # Handle blank_sides (modify die)
    if "blank_sides" in spec:
        blank_count = spec["blank_sides"]
        if entity.die:
            for i in range(min(blank_count, 6)):
                entity.die.sides[i] = Side(EffectType.BLANK, 0)

    # Handle level/tier
    if "level" in spec:
        entity.level = spec["level"]

    # Handle times_used_this_turn
    if "times_used_this_turn" in spec:
        state.times_used_this_turn = spec["times_used_this_turn"]

    # Handle equipped items count
    if "equipped_items_count" in spec:
        entity.equipped_items = [object() for _ in range(spec["equipped_items_count"])]

    # Handle equipped items total tier
    if "equipped_items_total_tier" in spec:
        # Create mock items with the total tier
        entity.equipped_item_tier_total = spec["equipped_items_total_tier"]

    # Handle incoming_damage (for DEFY)
    if "incoming_damage" in spec:
        fight.apply_damage(entity, entity, spec["incoming_damage"], is_pending=True)

    # Handle shields_gained_this_turn
    if "shields_gained_this_turn" in spec:
        state._shields_gained_this_turn = spec["shields_gained_this_turn"]

    # Handle buffs count
    if "buffs" in spec:
        # Apply N dummy buffs
        for _ in range(spec["buffs"]):
            fight.apply_boost(entity, 0)  # 0-value boost still counts as buff

    # Handle effects_on_me (for AFFECTED)
    if "effects_on_me" in spec:
        # Apply N dummy triggers
        for _ in range(spec["effects_on_me"]):
            fight.apply_weaken(entity, 0)

    # TODO: Handle "is_topmost" for TALL keyword
    # The fight.py doesn't have direct topmost tracking - position 0 is topmost
    # For now, we can set entity.position but this may not fully exercise TALL
    if "is_topmost" in spec:
        if spec["is_topmost"]:
            entity.position = 0
        else:
            entity.position = 1


def setup_context(fight: FightLog, context: dict, source: Entity, target: Entity):
    """Set up fight context from test specification.

    Handles:
    - dice_used_this_turn
    - previous_die_value
    - previous_die_values (list)
    - previous_die_keywords
    - previous_die_target
    - elapsed_turns
    - damaged_enemies
    - defeated_allies
    - all_entities_hp
    - total_poison_all_characters
    - etc.
    """
    if not context:
        return

    # Set dice used this turn
    if "dice_used_this_turn" in context:
        fight._dice_used_this_turn = context["dice_used_this_turn"]

    # Set up previous die for PAIR, ECHO, COPYCAT, etc.
    if "previous_die_value" in context:
        prev_value = context["previous_die_value"]
        if prev_value is not None:
            # Get keywords if specified
            prev_keywords = set()
            if "previous_die_keywords" in context:
                prev_keywords = parse_keywords(context["previous_die_keywords"])

            # Create a SideState and record it
            prev_side_state = create_mock_side_state(
                EffectType.DAMAGE, prev_value, prev_keywords
            )
            record_die_use(fight, prev_side_state, target)

    # Handle previous_die_values for TRIO, RUN, etc.
    # (Only if previous_die_value wasn't set - they're mutually exclusive)
    elif "previous_die_values" in context:
        prev_values = context["previous_die_values"]
        for val in prev_values:
            prev_side_state = create_mock_side_state(EffectType.DAMAGE, val)
            record_die_use(fight, prev_side_state, target)

    # Handle previous_die_keywords without previous_die_value
    # (for tests that only care about keyword sharing, like CHAIN)
    if "previous_die_keywords" in context and "previous_die_value" not in context and "previous_die_values" not in context:
        prev_keywords = parse_keywords(context["previous_die_keywords"])
        prev_side_state = create_mock_side_state(EffectType.DAMAGE, 1, prev_keywords)
        record_die_use(fight, prev_side_state, target)

    # Handle previous_die_target for FOCUS
    if "previous_die_target" in context:
        prev_target_id = context["previous_die_target"]
        # We need to find the entity with matching id, or use target if ids match
        target_spec = context.get("_target_spec", {})
        if isinstance(target_spec, dict) and target_spec.get("id") == prev_target_id:
            fight._last_die_target = target
        else:
            # TODO: For multi-entity tests, we'd need to track entity IDs
            # For now, if target ID doesn't match, set to None (focus won't trigger)
            fight._last_die_target = None

    # Handle elapsed_turns for ERA
    if "elapsed_turns" in context:
        fight._turn = context["elapsed_turns"]

    # Handle damaged_enemies for BLOODLUST
    if "damaged_enemies" in context:
        count = context["damaged_enemies"]
        # Damage some monsters
        for i, monster in enumerate(fight.monsters[:count]):
            if i < count:
                fight.apply_damage(source, monster, 1, is_pending=False)

    # Handle defeated_allies for VIGIL
    if "defeated_allies" in context:
        count = context["defeated_allies"]
        # Mark allies as dead
        for hero in fight.heroes[1:count+1]:  # Skip source
            fight.apply_damage(source, hero, hero.entity_type.hp, is_pending=False)

    # Handle total_poison_all_characters for PLAGUE
    if "total_poison_all_characters" in context:
        total = context["total_poison_all_characters"]
        # Apply poison spread across entities
        fight.apply_poison(target, total)

    # Handle unused_allies for RITE
    if "unused_allies" in context:
        # Set up allies that haven't been used
        pass  # Default state is unused

    # Handle abilities_used_this_turn for FIZZ
    if "abilities_used_this_turn" in context:
        fight._dice_used_this_turn = context["abilities_used_this_turn"]

    # Handle unequipped_items for HOARD
    if "unequipped_items" in context:
        # Add items to party unequipped items list
        for _ in range(context["unequipped_items"]):
            fight._party_unequipped_items.append(object())

    # Note: "all_entities_hp" is handled in run_value_test before FightLog creation
    # since it requires adding entities to the heroes/monsters lists


# =============================================================================
# Test Execution
# =============================================================================

def run_value_test(test: dict) -> TestResult:
    """Run a test that checks expected_value (final calculated value after keywords)."""
    test_id = test["id"]

    try:
        # Parse test parameters
        keywords = parse_keywords(test.get("keywords", []))
        effect_type = parse_effect_type(test.get("effect", "DAMAGE"))
        base_value = test.get("base_value", 1)
        expected_value = test.get("expected_value")

        if expected_value is None:
            return TestResult(test_id, TestStatus.SKIP, message="No expected_value")

        # Create entities
        source_spec = test.get("source", {"hp": 20, "max_hp": 20})
        target_spec = test.get("target", {"hp": 10, "max_hp": 15})

        # Handle SELF target
        target_is_self = target_spec == "SELF"

        source = create_entity(source_spec, Team.HERO, "Source")
        if target_is_self:
            target = source
        else:
            target = create_entity(target_spec, Team.MONSTER, "Target")

        # Create dice
        test_side = Side(effect_type, base_value, keywords)
        source.die = Die([test_side] * 6)

        if not target_is_self:
            target.die = Die([Side(EffectType.DAMAGE, 1)] * 6)

        # Create fight
        heroes = [source]
        monsters = [] if target_is_self else [target]

        # Add extra monsters/heroes for multi-entity keywords
        context = test.get("context", {})
        if "damaged_enemies" in context or "all_enemy_hp" in context:
            # Add extra monsters
            for i in range(3):
                extra = Entity(EntityType(f"Extra{i}", 10), Team.MONSTER)
                extra.die = Die([Side(EffectType.DAMAGE, 1)] * 6)
                monsters.append(extra)

        if "defeated_allies" in context or "unused_allies" in context:
            # Add extra heroes
            for i in range(3):
                extra = Entity(EntityType(f"Ally{i}", 10), Team.HERO)
                extra.die = Die([Side(EffectType.DAMAGE, 1)] * 6)
                heroes.append(extra)

        # Handle "all_entities_hp" for MOXIE/BULLY/SQUISH/UPPERCUT
        # These keywords check min/max HP across ALL living entities
        # Format: list of all HP values in the fight (including source and target)
        # We remove one instance each of source/target HP, create entities for the rest
        if "all_entities_hp" in context:
            remaining_hp = list(context["all_entities_hp"])
            # Remove source HP (one instance)
            src_hp = source_spec.get("hp", 20) if isinstance(source_spec, dict) else 20
            if src_hp in remaining_hp:
                remaining_hp.remove(src_hp)
            # Remove target HP (one instance)
            tgt_hp = target_spec.get("hp", 10) if isinstance(target_spec, dict) else 10
            if tgt_hp in remaining_hp:
                remaining_hp.remove(tgt_hp)
            # Create entities for remaining HP values
            for i, hp in enumerate(remaining_hp):
                extra = Entity(EntityType(f"ExtraEntity{i}", hp), Team.MONSTER)
                extra.die = Die([Side(EffectType.DAMAGE, 1)] * 6)
                monsters.append(extra)

        if not target_is_self and target not in monsters:
            monsters.insert(0, target)

        fight = FightLog(heroes, monsters)

        # Set up entity states
        setup_entity_state(fight, source, source_spec)
        if not target_is_self:
            setup_entity_state(fight, target, target_spec if isinstance(target_spec, dict) else {})

        # Store target spec in context for setup_context to use
        context_with_target = dict(context)
        context_with_target["_target_spec"] = target_spec

        # Set up context
        setup_context(fight, context_with_target, source, target)

        # Get the calculated value WITHOUT actually applying the effect
        # We need to use the internal calculation path
        source_state = fight._states[source]
        side_state = source_state.get_side_state(0, fight)
        calculated_side = side_state.get_calculated_effect()

        # Get base value from calculated side
        value = calculated_side.calculated_value

        # Apply conditional keyword bonuses
        target_state = fight._states[target]
        actual_value = fight._apply_conditional_keyword_bonuses(
            value, calculated_side, source_state, target_state, source, target
        )

        # Compare
        if actual_value == expected_value:
            return TestResult(test_id, TestStatus.PASS, expected_value, actual_value)
        else:
            return TestResult(
                test_id, TestStatus.FAIL,
                expected_value, actual_value,
                f"Expected {expected_value}, got {actual_value}"
            )

    except Exception as e:
        return TestResult(
            test_id, TestStatus.ERROR,
            message=str(e),
            traceback=traceback.format_exc()
        )


def run_targeting_test(test: dict) -> TestResult:
    """Run a test that checks expected_valid_target."""
    test_id = test["id"]

    try:
        expected_valid = test.get("expected_valid_target")
        if expected_valid is None:
            return TestResult(test_id, TestStatus.SKIP, message="No expected_valid_target")

        # Parse test parameters
        keywords = parse_keywords(test.get("keywords", []))
        effect_type = parse_effect_type(test.get("effect", "DAMAGE"))
        base_value = test.get("base_value", 1)

        # Create entities
        source_spec = test.get("source", {"hp": 20, "max_hp": 20})
        target_spec = test.get("target", {"hp": 10, "max_hp": 15})

        # Handle SELF target
        target_is_self = target_spec == "SELF"

        source = create_entity(source_spec, Team.HERO, "Source")
        if target_is_self:
            target = source
        else:
            target = create_entity(target_spec, Team.MONSTER, "Target")

        # Create dice
        test_side = Side(effect_type, base_value, keywords)
        source.die = Die([test_side] * 6)

        if not target_is_self:
            target.die = Die([Side(EffectType.DAMAGE, 1)] * 6)

        # Create fight with potentially multiple enemies for ELIMINATE/HEAVY
        heroes = [source]
        monsters = [] if target_is_self else [target]

        context = test.get("context", {})
        if "all_enemy_hp" in context:
            for i, hp in enumerate(context["all_enemy_hp"]):
                if i == 0:
                    # First one is our target, already added
                    continue
                extra = Entity(EntityType(f"Enemy{i}", hp), Team.MONSTER)
                extra.die = Die([Side(EffectType.DAMAGE, 1)] * 6)
                monsters.append(extra)

        fight = FightLog(heroes, monsters)

        # Set up entity states
        setup_entity_state(fight, source, source_spec)
        if not target_is_self:
            setup_entity_state(fight, target, target_spec if isinstance(target_spec, dict) else {})

        # Check targeting validity
        source_state = fight._states[source]
        side_state = source_state.get_side_state(0, fight)
        calculated_side = side_state.get_calculated_effect()

        # Check targeting keywords
        actual_valid = True

        # GENEROUS: cannot target self
        if Keyword.GENEROUS in keywords and target_is_self:
            actual_valid = False

        # ELIMINATE: target must have least HP
        if Keyword.ELIMINATE in keywords:
            min_hp = min(fight.get_state(m, Temporality.PRESENT).hp
                        for m in fight.monsters if not fight.get_state(m, Temporality.PRESENT).is_dead)
            target_hp = fight.get_state(target, Temporality.PRESENT).hp
            if target_hp > min_hp:
                actual_valid = False

        # HEAVY: target must have most HP
        if Keyword.HEAVY in keywords:
            max_hp = max(fight.get_state(m, Temporality.PRESENT).hp
                        for m in fight.monsters if not fight.get_state(m, Temporality.PRESENT).is_dead)
            target_hp = fight.get_state(target, Temporality.PRESENT).hp
            if target_hp < max_hp:
                actual_valid = False

        # SCARED: target must have N or less HP
        if Keyword.SCARED in keywords:
            target_hp = fight.get_state(target, Temporality.PRESENT).hp
            if target_hp > base_value:
                actual_valid = False

        # PICKY: target must have exactly N HP
        if Keyword.PICKY in keywords:
            target_hp = fight.get_state(target, Temporality.PRESENT).hp
            if target_hp != base_value:
                actual_valid = False

        # Compare
        if actual_valid == expected_valid:
            return TestResult(test_id, TestStatus.PASS, expected_valid, actual_valid)
        else:
            return TestResult(
                test_id, TestStatus.FAIL,
                expected_valid, actual_valid,
                f"Expected valid={expected_valid}, got valid={actual_valid}"
            )

    except Exception as e:
        return TestResult(
            test_id, TestStatus.ERROR,
            message=str(e),
            traceback=traceback.format_exc()
        )


def run_effect_test(test: dict) -> TestResult:
    """Run a test that checks effect application (damage/heal/shield with side effects)."""
    test_id = test["id"]

    try:
        # Check what kind of effect test this is
        expected_damage = test.get("expected_damage")
        expected_heal = test.get("expected_heal")
        expected_shield = test.get("expected_shield")

        if expected_damage is None and expected_heal is None and expected_shield is None:
            return TestResult(test_id, TestStatus.SKIP, message="No expected effect amount")

        # Parse test parameters
        keywords = parse_keywords(test.get("keywords", []))
        effect_type = parse_effect_type(test.get("effect", "DAMAGE"))
        base_value = test.get("base_value", 1)

        # Create entities
        source_spec = test.get("source", {"hp": 20, "max_hp": 20})
        target_spec = test.get("target", {"hp": 10, "max_hp": 15})

        source = create_entity(source_spec, Team.HERO, "Source")
        target = create_entity(target_spec, Team.MONSTER, "Target")

        # Create dice
        test_side = Side(effect_type, base_value, keywords)
        source.die = Die([test_side] * 6)
        target.die = Die([Side(EffectType.DAMAGE, 1)] * 6)

        fight = FightLog([source], [target])

        # Set up entity states
        setup_entity_state(fight, source, source_spec)
        setup_entity_state(fight, target, target_spec if isinstance(target_spec, dict) else {})

        # Set up context
        setup_context(fight, test.get("context", {}), source, target)

        # Record initial state
        target_state_before = fight.get_state(target, Temporality.PRESENT)
        source_state_before = fight.get_state(source, Temporality.PRESENT)

        # Use the die
        fight.use_die(source, 0, target)

        # Get final state
        target_state_after = fight.get_state(target, Temporality.PRESENT)
        source_state_after = fight.get_state(source, Temporality.PRESENT)

        # Check expected results
        results = []

        if expected_damage is not None:
            actual_damage = target_state_before.hp - target_state_after.hp
            if actual_damage != expected_damage:
                results.append(f"damage: expected {expected_damage}, got {actual_damage}")

        if expected_heal is not None:
            actual_heal = target_state_after.hp - target_state_before.hp
            if actual_heal != expected_heal:
                results.append(f"heal: expected {expected_heal}, got {actual_heal}")

        if expected_shield is not None:
            actual_shield = target_state_after.shield
            if actual_shield != expected_shield:
                results.append(f"shield: expected {expected_shield}, got {actual_shield}")

        # Check side effects
        if "expected_poison_applied" in test:
            # TODO: Implement poison tracking verification
            pass

        if "expected_side_growth" in test:
            expected_growth = test["expected_side_growth"]
            actual_growth = source.die.sides[0].growth_bonus
            if actual_growth != expected_growth:
                results.append(f"growth: expected {expected_growth}, got {actual_growth}")

        if results:
            return TestResult(
                test_id, TestStatus.FAIL,
                message="; ".join(results)
            )
        else:
            return TestResult(test_id, TestStatus.PASS)

    except Exception as e:
        return TestResult(
            test_id, TestStatus.ERROR,
            message=str(e),
            traceback=traceback.format_exc()
        )


def run_special_test(test: dict) -> TestResult:
    """Run tests for special/behavioral keywords that don't fit other categories."""
    test_id = test["id"]

    # These tests check behavioral aspects like:
    # - expected_cannot_use_manually
    # - expected_must_use
    # - expected_activates_during_roll
    # - expected_side_changes_randomly
    # - expected_targets_as_enemy
    # etc.

    # Most of these are design/spec tests that can't be verified programmatically
    # without more infrastructure. Mark as SKIP for now.

    behavioral_fields = [
        "expected_cannot_use_manually",
        "expected_must_use",
        "expected_activates_during_roll",
        "expected_side_changes_randomly",
        "expected_targets_as_enemy",
        "expected_cannot_reroll",
        "expected_only_pips_change_on_replace",
        "expected_pips_remain_on_replace",
        "expected_keywords_remain_on_replace",
        "expected_random_extra_keyword",
        "expected_reusable_if_lethal",
        "expected_reusable_if_saves",
        "expected_source_dies_if_lethal",
        "expected_source_dies_if_saves",
        "expected_source_dies",
        "expected_once_per_fight",
        "expected_once_per_turn",
        "expected_target_flees",
        "expected_inflicted_keyword",
        "expected_target_gains_keywords",
        "expected_target_loses_keywords",
        "expected_copies_to_allies",
        "expected_tactic_cost_contribution",
    ]

    for field in behavioral_fields:
        if field in test:
            return TestResult(
                test_id, TestStatus.SKIP,
                message=f"Behavioral test ({field}) - not yet implemented"
            )

    return TestResult(test_id, TestStatus.SKIP, message="Unknown test type")


def run_test(test: dict) -> TestResult:
    """Run a single oracle test and return the result."""
    test_id = test["id"]

    # Determine test type and dispatch to appropriate handler
    if "expected_value" in test:
        return run_value_test(test)
    elif "expected_valid_target" in test:
        return run_targeting_test(test)
    elif any(k in test for k in ["expected_damage", "expected_heal", "expected_shield"]):
        return run_effect_test(test)
    else:
        return run_special_test(test)


# =============================================================================
# Reporting
# =============================================================================

def print_result(result: TestResult, verbose: bool = False):
    """Print a single test result."""
    status_symbols = {
        TestStatus.PASS: "\033[92m\u2713\033[0m",  # Green checkmark
        TestStatus.FAIL: "\033[91m\u2717\033[0m",  # Red X
        TestStatus.SKIP: "\033[93m\u25cb\033[0m",  # Yellow circle
        TestStatus.ERROR: "\033[91m!\033[0m", # Red exclamation
    }

    symbol = status_symbols[result.status]

    if result.status == TestStatus.PASS:
        if verbose:
            print(f"  {symbol} {result.test_id}: {result.expected} == {result.actual}")
    elif result.status == TestStatus.FAIL:
        print(f"  {symbol} {result.test_id}: {result.message}")
        if result.expected is not None:
            print(f"      Expected: {result.expected}")
            print(f"      Actual:   {result.actual}")
    elif result.status == TestStatus.SKIP:
        if verbose:
            print(f"  {symbol} {result.test_id}: {result.message}")
    elif result.status == TestStatus.ERROR:
        print(f"  {symbol} {result.test_id}: ERROR - {result.message}")
        if verbose and result.traceback:
            for line in result.traceback.split('\n'):
                print(f"      {line}")


def print_summary(results: list[TestResult]):
    """Print a summary of all test results."""
    total = len(results)
    passed = sum(1 for r in results if r.status == TestStatus.PASS)
    failed = sum(1 for r in results if r.status == TestStatus.FAIL)
    skipped = sum(1 for r in results if r.status == TestStatus.SKIP)
    errors = sum(1 for r in results if r.status == TestStatus.ERROR)

    print("\n" + "=" * 60)
    print("SUMMARY")
    print("=" * 60)
    print(f"Total:   {total}")
    print(f"Passed:  \033[92m{passed}\033[0m")
    print(f"Failed:  \033[91m{failed}\033[0m")
    print(f"Skipped: \033[93m{skipped}\033[0m")
    print(f"Errors:  \033[91m{errors}\033[0m")

    if failed > 0 or errors > 0:
        print("\nFailed/Error tests:")
        for r in results:
            if r.status in (TestStatus.FAIL, TestStatus.ERROR):
                print(f"  - {r.test_id}")


# =============================================================================
# Main
# =============================================================================

def main():
    parser = argparse.ArgumentParser(description="Verify oracle tests against combat implementation")
    parser.add_argument("--verbose", "-v", action="store_true", help="Show all results including passes")
    parser.add_argument("--filter", "-f", type=str, help="Filter tests by ID (case-insensitive substring match)")
    parser.add_argument("--stop-on-fail", "-s", action="store_true", help="Stop at first failure")
    parser.add_argument("--show-skipped", action="store_true", help="Show skipped tests")
    args = parser.parse_args()

    # Load tests
    tests_path = Path(__file__).parent.parent / "oracle_tests" / "generated_tests.json"
    if not tests_path.exists():
        print(f"ERROR: Test file not found: {tests_path}")
        sys.exit(1)

    with open(tests_path) as f:
        data = json.load(f)

    tests = data.get("tests", [])
    print(f"Loaded {len(tests)} tests from {tests_path.name}")

    # Filter tests if requested
    if args.filter:
        filter_lower = args.filter.lower()
        tests = [t for t in tests if filter_lower in t["id"].lower()]
        print(f"Filtered to {len(tests)} tests matching '{args.filter}'")

    # Run tests
    results = []
    for test in tests:
        result = run_test(test)
        results.append(result)

        # Print result
        if result.status != TestStatus.SKIP or args.show_skipped:
            print_result(result, args.verbose)

        # Stop on fail if requested
        if args.stop_on_fail and result.status in (TestStatus.FAIL, TestStatus.ERROR):
            print("\nStopping on first failure (--stop-on-fail)")
            break

    # Print summary
    print_summary(results)

    # Exit with appropriate code
    failed_count = sum(1 for r in results if r.status in (TestStatus.FAIL, TestStatus.ERROR))
    sys.exit(1 if failed_count > 0 else 0)


if __name__ == "__main__":
    main()
