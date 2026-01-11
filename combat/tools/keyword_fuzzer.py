#!/usr/bin/env python3
"""Iterative keyword combination tester.

Exhaustively tests keyword combinations to find crashes.
Logs crashes and skips known-crashing combinations on reruns.

Design principles:
- Deterministic: iterate, don't randomize
- Parallelizable: each combination is independent
- Skip-list based: track known crashes, skip on rerun
- Symmetric handling: A+B and B+A treated as same combination
- Recheck support: re-test skipped combos, unflag if fixed

Usage:
    cd combat && uv run python tools/keyword_fuzzer.py pairs       # Run, skip known crashes
    cd combat && uv run python tools/keyword_fuzzer.py recheck     # Re-test skipped combos
    cd combat && uv run python tools/keyword_fuzzer.py summary     # View crash summary
    cd combat && uv run python tools/keyword_fuzzer.py clear       # Clear all skip data
"""

import sys
import json
import time
import re
import argparse
import traceback
import os
import multiprocessing
from pathlib import Path
from datetime import datetime
from itertools import combinations
from dataclasses import dataclass, asdict, field
from typing import Iterator
from concurrent.futures import ProcessPoolExecutor, as_completed
from collections import defaultdict
import hashlib

# Get CPU count for parallel processing - use all available cores
CPU_COUNT = os.cpu_count() or 4
# Batch size for parallel processing (higher = less overhead, more memory)
PARALLEL_BATCH_SIZE = 1000

# Add src to path
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.entity import Entity, EntityType, EntitySize, Team
from src.dice import Die, Side, Keyword
from src.effects import EffectType
from src.fight import FightLog, Temporality


# Output directories
CRASH_LOG_DIR = Path(__file__).parent.parent / "crash_logs"
CHECKPOINT_FILE = Path(__file__).parent.parent / ".fuzzer_checkpoint.json"  # Legacy, will remove
REPORT_FILE = Path(__file__).parent.parent / "crash_logs" / "fuzzer_report.json"
SKIP_FILE = Path(__file__).parent.parent / "crash_logs" / "skip_list.json"

# CPU optimization: Pre-created reusable objects
_HERO_TYPE = EntityType("TestHero", 20, EntitySize.HERO)
_MONSTER_TYPE = EntityType("TestMonster", 20, EntitySize.HERO)
_MONSTER_SIDE = Side(EffectType.DAMAGE, 1)
_MONSTER_DIE_SIDES = [_MONSTER_SIDE] * 6  # Reused list

# Checkpoint save interval (higher = less I/O)
CHECKPOINT_INTERVAL = 5000

# Fix hints for common error patterns
FIX_HINTS = {
    "AttributeError": "Add null check - likely accessing attribute on None",
    "KeyError": "Use dict.get() with default or check key exists first",
    "TypeError": "Check argument types - likely passing wrong type or None",
    "AssertionError": "Logic error - invariant violated, trace the data flow",
    "IndexError": "Check list bounds - likely empty list or invalid index",
    "ZeroDivisionError": "Add divisor != 0 check",
    "RecursionError": "Check for infinite loop in keyword interaction",
}

# Keywords that depend on specific game state to function
STATE_DEPENDENT_KEYWORDS = {
    # Require damaged source
    "BLOODLUST", "ANTI_PRISTINE", "BERSERK",
    # Require damaged target
    "ANTI_ENGAGE", "EXECUTE",
    # Require previous die usage
    "COPYCAT", "ECHO",
    # Require existing shield
    "SHATTER", "UNSHIELDED",
    # Require specific HP states
    "ENGAGE", "PRISTINE", "SWAP_ENGAGE", "HALVE_ENGAGE",
}

# Keywords that may legitimately produce no effect
NO_EFFECT_OK_KEYWORDS = {
    "SCARED",      # Can't target full HP - may fail to target
    "PICKY",       # Must target lowest HP - edge cases
    "GENEROUS",    # Must target highest HP - edge cases
    "HEAVY",       # Can't target if source has shield
    "ELIMINATE",   # Skip if target HP > source HP
}


@dataclass
class TestScenario:
    """A specific test configuration to exercise different game states."""
    name: str
    source_hp_pct: float  # 1.0 = full, 0.5 = half
    target_hp_pct: float
    source_shield: int
    target_shield: int
    simulate_turn_2: bool  # Simulate a previous die use for COPYCAT/ECHO

    def describe(self) -> str:
        parts = [self.name]
        if self.source_hp_pct < 1.0:
            parts.append(f"src@{int(self.source_hp_pct*100)}%hp")
        if self.target_hp_pct < 1.0:
            parts.append(f"tgt@{int(self.target_hp_pct*100)}%hp")
        if self.source_shield:
            parts.append(f"src+{self.source_shield}sh")
        if self.target_shield:
            parts.append(f"tgt+{self.target_shield}sh")
        if self.simulate_turn_2:
            parts.append("turn2")
        return "_".join(parts)


# Test scenarios to exercise different game states
TEST_SCENARIOS = [
    # Baseline: full HP, no shields, turn 1 (current behavior)
    TestScenario("baseline", 1.0, 1.0, 0, 0, False),
    # Damaged source: triggers BLOODLUST, ANTI_PRISTINE, BERSERK
    TestScenario("src_damaged", 0.5, 1.0, 0, 0, False),
    # Damaged target: triggers ANTI_ENGAGE, EXECUTE
    TestScenario("tgt_damaged", 1.0, 0.5, 0, 0, False),
    # Both damaged: combined state-dependent keywords
    TestScenario("both_damaged", 0.5, 0.5, 0, 0, False),
    # Turn 2: enables COPYCAT, ECHO
    TestScenario("turn2", 1.0, 1.0, 0, 0, True),
    # Shielded target: tests shield interactions
    TestScenario("tgt_shielded", 1.0, 1.0, 0, 5, False),
]

# Fast mode uses only baseline; full mode uses all scenarios
FAST_SCENARIOS = [TEST_SCENARIOS[0]]  # Just baseline


# =============================================================================
# Skip List Management - Track known crashes, skip on rerun
# =============================================================================

def canonical_key(keywords: tuple | list, effect_type: str | EffectType, scenario_name: str = "baseline") -> str:
    """Generate a canonical key for a keyword combination.

    Keywords are sorted alphabetically so A+B and B+A produce the same key.
    This handles the symmetry requirement: knowing A+B crashes means B+A crashes too.

    Format: "KEYWORD1,KEYWORD2:EFFECT:scenario"
    """
    if isinstance(effect_type, EffectType):
        effect_type = effect_type.name

    # Sort keyword names alphabetically for canonical ordering
    kw_names = sorted(kw.name if hasattr(kw, 'name') else str(kw) for kw in keywords)
    return f"{','.join(kw_names)}:{effect_type}:{scenario_name}"


@dataclass
class SkipEntry:
    """Information about a skipped (known-crashing) combination."""
    key: str  # Canonical key
    signature: str  # Error signature (e.g., "AttributeError@fight.py:123")
    error_type: str
    error_message: str
    source_location: str
    first_seen: str  # ISO timestamp
    last_tested: str  # ISO timestamp
    test_count: int = 1  # How many times we've seen this crash
    keywords: list[str] = field(default_factory=list)
    effect_type: str = "DAMAGE"
    scenario: str = "baseline"
    phase: str = "unknown"
    likely_cause: str = "unknown"
    needs_java_review: bool = False
    traceback: str = ""

    def to_dict(self) -> dict:
        return asdict(self)

    @classmethod
    def from_dict(cls, d: dict) -> "SkipEntry":
        return cls(**d)

    @classmethod
    def from_crash(cls, crash: "CrashReport") -> "SkipEntry":
        """Create a SkipEntry from a CrashReport."""
        key = canonical_key(crash.keywords, crash.effect_type, crash.scenario)
        return cls(
            key=key,
            signature=crash.signature,
            error_type=crash.error_type,
            error_message=crash.error_message,
            source_location=crash.source_location,
            first_seen=crash.timestamp,
            last_tested=crash.timestamp,
            test_count=1,
            keywords=crash.keywords,
            effect_type=crash.effect_type,
            scenario=crash.scenario,
            phase=crash.phase,
            likely_cause=crash.likely_cause,
            needs_java_review=crash.needs_java_review,
            traceback=crash.traceback,
        )


@dataclass
class SkipList:
    """Persistent skip list for known-crashing combinations."""
    version: int = 2
    combinations: dict[str, SkipEntry] = field(default_factory=dict)  # key -> SkipEntry
    signatures: dict[str, list[str]] = field(default_factory=dict)  # signature -> [keys]

    def add(self, crash: "CrashReport") -> bool:
        """Add a crash to the skip list. Returns True if new."""
        entry = SkipEntry.from_crash(crash)
        key = entry.key

        is_new = key not in self.combinations
        if is_new:
            self.combinations[key] = entry
            # Track by signature for analysis
            if entry.signature not in self.signatures:
                self.signatures[entry.signature] = []
            self.signatures[entry.signature].append(key)
        else:
            # Update existing entry
            existing = self.combinations[key]
            existing.last_tested = entry.last_tested
            existing.test_count += 1

        return is_new

    def should_skip(self, keywords: tuple, effect_type: EffectType, scenario_name: str) -> bool:
        """Check if this combination should be skipped."""
        key = canonical_key(keywords, effect_type, scenario_name)
        return key in self.combinations

    def remove(self, key: str) -> bool:
        """Remove a combination from skip list (e.g., after fix). Returns True if removed."""
        if key not in self.combinations:
            return False

        entry = self.combinations[key]
        del self.combinations[key]

        # Remove from signature tracking
        if entry.signature in self.signatures:
            self.signatures[entry.signature] = [k for k in self.signatures[entry.signature] if k != key]
            if not self.signatures[entry.signature]:
                del self.signatures[entry.signature]

        return True

    def get_keys_for_signature(self, signature: str) -> list[str]:
        """Get all keys that crash with a given signature."""
        return self.signatures.get(signature, [])

    def to_dict(self) -> dict:
        return {
            "version": self.version,
            "combinations": {k: v.to_dict() for k, v in self.combinations.items()},
            "signatures": self.signatures,
            "summary": {
                "total_skipped": len(self.combinations),
                "unique_signatures": len(self.signatures),
            }
        }

    @classmethod
    def from_dict(cls, d: dict) -> "SkipList":
        combinations = {k: SkipEntry.from_dict(v) for k, v in d.get("combinations", {}).items()}
        return cls(
            version=d.get("version", 2),
            combinations=combinations,
            signatures=d.get("signatures", {}),
        )


def load_skip_list() -> SkipList:
    """Load skip list from file, or create empty one."""
    if SKIP_FILE.exists():
        try:
            data = json.loads(SKIP_FILE.read_text())
            return SkipList.from_dict(data)
        except (json.JSONDecodeError, KeyError) as e:
            print(f"Warning: Could not load skip list ({e}), starting fresh")
    return SkipList()


def save_skip_list(skip_list: SkipList):
    """Save skip list to file."""
    CRASH_LOG_DIR.mkdir(parents=True, exist_ok=True)
    SKIP_FILE.write_text(json.dumps(skip_list.to_dict(), indent=2))


def extract_source_location(tb: str) -> str:
    """Extract the most relevant source location from a traceback.

    Returns file:line from the combat/src/ directory, or the last frame if not found.
    """
    # Match lines like: File "/path/to/combat/src/fight.py", line 234
    pattern = r'File "([^"]+combat/src/[^"]+)", line (\d+)'
    matches = re.findall(pattern, tb)
    if matches:
        # Return the last match (deepest in our code)
        filepath, line = matches[-1]
        filename = Path(filepath).name
        return f"{filename}:{line}"

    # Fallback: any Python file
    pattern = r'File "([^"]+\.py)", line (\d+)'
    matches = re.findall(pattern, tb)
    if matches:
        filepath, line = matches[-1]
        filename = Path(filepath).name
        return f"{filename}:{line}"

    return "unknown"


def compute_crash_signature(error_type: str, traceback_str: str) -> str:
    """Compute a unique signature for a crash based on error type and source location.

    Crashes with the same signature likely have the same root cause.
    """
    source_loc = extract_source_location(traceback_str)
    return f"{error_type}@{source_loc}"


@dataclass
class CrashReport:
    """Machine-readable crash report for LLM consumption."""
    timestamp: str
    level: str  # "singles", "pairs", "triples"
    keywords: list[str]
    effect_type: str
    pip_value: int
    phase: str  # "creation", "use_die", "next_turn", "get_state"
    error_type: str
    error_message: str
    traceback: str
    signature: str = ""  # Computed from error_type + source location
    source_location: str = ""  # file:line where crash occurred
    scenario: str = "baseline"  # Which test scenario triggered this
    likely_cause: str = ""  # Heuristic classification
    needs_java_review: bool = False  # True if fix requires semantic decision

    def __post_init__(self):
        if not self.signature:
            self.signature = compute_crash_signature(self.error_type, self.traceback)
        if not self.source_location:
            self.source_location = extract_source_location(self.traceback)
        if not self.likely_cause:
            self.likely_cause = self._classify_crash()
            self.needs_java_review = self.likely_cause in ("semantic_decision", "param_limitation", "interaction_bug")

    def _classify_crash(self) -> str:
        """Heuristic classification of crash root cause.

        Returns:
            code_bug: Mechanical fix (null check, dict access) - safe for LLM
            param_limitation: Crash only in certain scenarios - may be expected
            semantic_decision: Fix requires understanding Java behavior
            interaction_bug: Keywords interact in unexpected ways
            logic_error: Invariant violated, needs investigation
            unknown: Can't classify automatically
        """
        # Code bugs - almost always safe mechanical fixes
        if self.error_type == "AttributeError":
            if "NoneType" in self.error_message or "has no attribute" in self.error_message:
                return "code_bug"
        if self.error_type == "KeyError":
            return "code_bug"
        if self.error_type == "IndexError":
            return "code_bug"
        if self.error_type == "TypeError":
            if "NoneType" in self.error_message or "None" in self.error_message:
                return "code_bug"

        # Recursion suggests infinite loop in keyword interaction
        if self.error_type == "RecursionError":
            return "interaction_bug"

        # Check if state-dependent keywords are involved
        has_state_dep = any(kw in STATE_DEPENDENT_KEYWORDS for kw in self.keywords)
        if has_state_dep and self.scenario != "baseline":
            # Crashed in a specific scenario - might be param limitation
            return "param_limitation"

        # Assertion failures need investigation
        if self.error_type == "AssertionError":
            return "logic_error"

        # If we have state-dependent keywords but crashed in baseline,
        # the fix might require semantic understanding
        if has_state_dep:
            return "semantic_decision"

        return "unknown"

    def to_json(self) -> str:
        return json.dumps(asdict(self), indent=2)

    def to_file(self, crash_dir: Path) -> Path:
        """Write crash report to file, return path."""
        crash_dir.mkdir(parents=True, exist_ok=True)
        keywords_str = "_".join(self.keywords)[:50]  # Truncate for filename
        filename = f"crash_{self.level}_{keywords_str}_{int(time.time())}.json"
        path = crash_dir / filename
        path.write_text(self.to_json())
        return path

    def get_test_code(self) -> str:
        """Generate copy-pasteable test code to reproduce this crash."""
        kw_set = ", ".join(f"Keyword.{kw}" for kw in self.keywords)
        safe_name = self.signature.replace("@", "_").replace(":", "_").replace(".", "_")

        # Add scenario-specific setup
        scenario_setup = ""
        if self.scenario == "src_damaged":
            scenario_setup = "\n    hero.damage(10)  # Scenario: source damaged"
        elif self.scenario == "tgt_damaged":
            scenario_setup = "\n    monster.damage(10)  # Scenario: target damaged"
        elif self.scenario == "both_damaged":
            scenario_setup = "\n    hero.damage(10)  # Scenario: both damaged\n    monster.damage(10)"
        elif self.scenario == "turn2":
            scenario_setup = "\n    # Scenario: turn 2 (simulate previous die use)\n    fight.use_die(monster, 0, hero)\n    fight.next_turn()"
        elif self.scenario == "tgt_shielded":
            scenario_setup = "\n    monster.add_shield(5)  # Scenario: target shielded"

        java_warning = ""
        if self.needs_java_review:
            java_warning = f"\n    # WARNING: likely_cause={self.likely_cause} - check Java source before fixing"

        return f'''def test_regression_{safe_name}(self):
    """Regression: {self.error_type} with {self.keywords}."""
    hero = make_hero("Tester", hp=20)
    monster = make_monster("Target", hp=20)

    side = Side(EffectType.{self.effect_type}, {self.pip_value}, {{{kw_set}}})
    hero.die = Die([side] * 6)
    monster.die = Die([Side(EffectType.DAMAGE, 1)] * 6)

    fight = FightLog([hero], [monster]){scenario_setup}{java_warning}
    fight.use_die(hero, 0, monster)  # Crashed in phase: {self.phase}
'''


@dataclass
class CrashSignature:
    """Aggregated info about all crashes with the same signature."""
    signature: str
    error_type: str
    source_location: str
    error_message: str
    phase: str
    fix_hint: str
    likely_cause: str = "unknown"  # Heuristic classification
    needs_java_review: bool = False  # True if fix requires semantic decision
    count: int = 0
    keyword_examples: list[list[str]] = field(default_factory=list)
    effect_types: set[str] = field(default_factory=set)
    scenarios: set[str] = field(default_factory=set)  # Which scenarios triggered this
    traceback: str = ""  # Keep one full traceback for debugging
    test_code: str = ""  # Copy-pasteable test

    def to_dict(self) -> dict:
        return {
            "signature": self.signature,
            "error_type": self.error_type,
            "source_location": self.source_location,
            "error_message": self.error_message,
            "phase": self.phase,
            "fix_hint": self.fix_hint,
            "likely_cause": self.likely_cause,
            "needs_java_review": self.needs_java_review,
            "count": self.count,
            "keyword_examples": self.keyword_examples[:5],  # Limit examples
            "effect_types": list(self.effect_types),
            "scenarios": list(self.scenarios),
            "test_code": self.test_code,
            "traceback": self.traceback,
        }


def get_all_keywords() -> list[Keyword]:
    """Get all keyword enum values."""
    return list(Keyword)


@dataclass
class TestResult:
    """Result of a keyword combination test."""
    crash: CrashReport | None = None
    no_effect_warning: str | None = None  # Warning if effect had no observable impact


def test_keyword_combination(
    keywords: tuple[Keyword, ...],
    effect_type: EffectType = EffectType.DAMAGE,
    pip_value: int = 3,
    scenario: TestScenario = None
) -> TestResult:
    """Test a single keyword combination. Returns TestResult with crash info and warnings.

    Args:
        keywords: Keyword combination to test
        effect_type: Effect type (DAMAGE, HEAL, SHIELD)
        pip_value: Pip value for the die
        scenario: Test scenario for state setup (default: baseline)

    Returns:
        TestResult with crash report (if crashed) and/or warnings
    """
    if scenario is None:
        scenario = FAST_SCENARIOS[0]  # baseline

    keyword_names = [kw.name for kw in keywords]

    try:
        # Phase: creation
        phase = "creation"
        side = Side(effect_type, pip_value, set(keywords))
        die = Die([side] * 6)

        # Create entities with scenario-specific HP
        hero_hp = int(20 * scenario.source_hp_pct)
        monster_hp = int(20 * scenario.target_hp_pct)

        hero_type = EntityType("TestHero", 20, EntitySize.HERO)
        monster_type = EntityType("TestMonster", 20, EntitySize.HERO)

        hero = Entity(hero_type, Team.HERO)
        hero.die = die

        monster = Entity(monster_type, Team.MONSTER)
        monster.die = Die(_MONSTER_DIE_SIDES[:])

        fight = FightLog([hero], [monster])

        # Apply scenario-specific state
        # Note: apply_damage takes (source, target, amount) - use entity as its own source for setup
        if scenario.source_hp_pct < 1.0:
            damage = 20 - hero_hp
            fight.apply_damage(hero, hero, damage)  # Self-damage for setup
        if scenario.target_hp_pct < 1.0:
            damage = 20 - monster_hp
            fight.apply_damage(monster, monster, damage)  # Self-damage for setup
        if scenario.source_shield > 0:
            fight.apply_shield(hero, scenario.source_shield)
        if scenario.target_shield > 0:
            fight.apply_shield(monster, scenario.target_shield)

        # Simulate turn 2 if needed (for COPYCAT/ECHO)
        if scenario.simulate_turn_2:
            fight.use_die(monster, 0, hero)  # Monster attacks hero
            fight.next_turn()

        # Capture state before die use
        phase = "pre_use_state"
        hero_pre = fight.get_state(hero, Temporality.PRESENT)
        monster_pre = fight.get_state(monster, Temporality.PRESENT)

        # Phase: use_die
        phase = "use_die"
        fight.use_die(hero, 0, monster)

        # Phase: get_state
        phase = "get_state"
        hero_state = fight.get_state(hero, Temporality.PRESENT)
        monster_state = fight.get_state(monster, Temporality.PRESENT)

        # Phase: next_turn
        phase = "next_turn"
        fight.next_turn()

        # Phase: post_turn_state
        phase = "post_turn_state"
        hero_post = fight.get_state(hero, Temporality.PRESENT)
        monster_post = fight.get_state(monster, Temporality.PRESENT)

        # Basic invariant checks
        phase = "invariant_check"
        assert hero_post.hp <= hero_post.max_hp, f"Hero HP {hero_post.hp} > max {hero_post.max_hp}"
        assert hero_post.shield >= 0, f"Hero negative shield: {hero_post.shield}"
        assert monster_post.hp <= monster_post.max_hp, f"Monster HP {monster_post.hp} > max {monster_post.max_hp}"
        assert monster_post.shield >= 0, f"Monster negative shield: {monster_post.shield}"

        # Semantic effect detection: did anything actually happen?
        no_effect_warning = None
        has_no_effect_keyword = any(kw.name in NO_EFFECT_OK_KEYWORDS for kw in keywords)

        # Check if state changed
        hero_changed = (
            hero_pre.hp != hero_post.hp or
            hero_pre.shield != hero_post.shield
        )
        monster_changed = (
            monster_pre.hp != monster_post.hp or
            monster_pre.shield != monster_post.shield
        )

        if not hero_changed and not monster_changed and not has_no_effect_keyword:
            # No observable effect - this might indicate:
            # 1. Test params don't exercise the keywords properly
            # 2. Keywords cancelled each other out
            # 3. Bug in implementation
            no_effect_warning = (
                f"No effect observed: effect_type={effect_type.name}, "
                f"scenario={scenario.name}, keywords={keyword_names}"
            )

        return TestResult(crash=None, no_effect_warning=no_effect_warning)

    except Exception as e:
        crash = CrashReport(
            timestamp=datetime.now().isoformat(),
            level=f"{len(keywords)}_keywords",
            keywords=keyword_names,
            effect_type=effect_type.name,
            pip_value=pip_value,
            phase=phase,
            error_type=type(e).__name__,
            error_message=str(e),
            traceback=traceback.format_exc(),
            scenario=scenario.name
        )
        return TestResult(crash=crash)


def generate_combinations(level: int) -> Iterator[tuple[Keyword, ...]]:
    """Generate all keyword combinations at given level."""
    all_keywords = get_all_keywords()
    return combinations(all_keywords, level)


def _worker_test_combination(args: tuple) -> TestResult:
    """Worker function for parallel execution. Must be at module level for pickling."""
    keywords, effect_type, scenario_dict = args
    # Reconstruct TestScenario from dict (for pickling)
    scenario = TestScenario(**scenario_dict)
    return test_keyword_combination(keywords, effect_type, scenario=scenario)


def _run_parallel_batch(test_cases: list, workers: int) -> list[TestResult]:
    """Run a batch of test cases in parallel.

    Args:
        test_cases: List of (keywords, effect_type, scenario_dict) tuples
        workers: Number of worker processes

    Returns:
        List of TestResult objects
    """
    results = []
    with ProcessPoolExecutor(max_workers=workers) as executor:
        futures = [executor.submit(_worker_test_combination, tc) for tc in test_cases]
        for future in as_completed(futures):
            try:
                results.append(future.result())
            except Exception as e:
                # Worker crashed - create error result
                results.append(TestResult(
                    crash=CrashReport(
                        timestamp=datetime.now().isoformat(),
                        level="parallel_error",
                        keywords=["UNKNOWN"],
                        effect_type="UNKNOWN",
                        pip_value=0,
                        phase="parallel_worker",
                        error_type=type(e).__name__,
                        error_message=str(e),
                        traceback=traceback.format_exc(),
                        scenario="unknown"
                    )
                ))
    return results


def count_combinations(level: int) -> int:
    """Count combinations without generating them."""
    n = len(get_all_keywords())
    from math import comb
    return comb(n, level)


def load_checkpoint() -> dict:
    """Load checkpoint file if exists."""
    if CHECKPOINT_FILE.exists():
        return json.loads(CHECKPOINT_FILE.read_text())
    return {"completed": {}, "crashes": 0}


def save_checkpoint(checkpoint: dict):
    """Save checkpoint to file."""
    CHECKPOINT_FILE.write_text(json.dumps(checkpoint, indent=2))


def aggregate_crashes(crashes: list[CrashReport]) -> dict[str, CrashSignature]:
    """Aggregate crashes by signature for LLM-efficient consumption.

    Returns a dict of signature -> CrashSignature with counts and examples.
    """
    signatures: dict[str, CrashSignature] = {}

    for crash in crashes:
        sig = crash.signature
        if sig not in signatures:
            signatures[sig] = CrashSignature(
                signature=sig,
                error_type=crash.error_type,
                source_location=crash.source_location,
                error_message=crash.error_message,
                phase=crash.phase,
                fix_hint=FIX_HINTS.get(crash.error_type, "Check traceback for details"),
                likely_cause=crash.likely_cause,
                needs_java_review=crash.needs_java_review,
                count=0,
                keyword_examples=[],
                effect_types=set(),
                scenarios=set(),
                traceback=crash.traceback,
                test_code=crash.get_test_code(),
            )

        signatures[sig].count += 1
        if len(signatures[sig].keyword_examples) < 5:
            signatures[sig].keyword_examples.append(crash.keywords)
        signatures[sig].effect_types.add(crash.effect_type)
        signatures[sig].scenarios.add(crash.scenario)

        # Update needs_java_review if any crash in this signature needs it
        if crash.needs_java_review:
            signatures[sig].needs_java_review = True

    return signatures


def write_report(signatures: dict[str, CrashSignature], total_tested: int, elapsed: float,
                 no_effect_warnings: list[str] = None):
    """Write consolidated fuzzer report for LLM consumption.

    One file with all unique crash signatures, sorted by count (most common first).
    This is much more efficient than reading individual crash files.
    """
    CRASH_LOG_DIR.mkdir(parents=True, exist_ok=True)
    no_effect_warnings = no_effect_warnings or []

    # Sort by: needs_java_review=False first (safe fixes), then by count
    # This prioritizes mechanical fixes that the LLM can do safely
    sorted_sigs = sorted(
        signatures.values(),
        key=lambda s: (s.needs_java_review, -s.count)
    )

    # Separate by fix type for LLM guidance
    code_bugs = [s for s in sorted_sigs if s.likely_cause == "code_bug"]
    needs_review = [s for s in sorted_sigs if s.needs_java_review]

    report = {
        "summary": {
            "total_tested": total_tested,
            "total_crashes": sum(s.count for s in sorted_sigs),
            "unique_signatures": len(sorted_sigs),
            "code_bugs": len(code_bugs),
            "needs_java_review": len(needs_review),
            "no_effect_warnings": len(no_effect_warnings),
            "elapsed_seconds": round(elapsed, 1),
            "tests_per_second": round(total_tested / elapsed, 1) if elapsed > 0 else 0,
        },
        "signatures": [s.to_dict() for s in sorted_sigs],
        "fix_priority": [
            f"[{s.likely_cause}] {s.signature} ({s.count} crashes){' ⚠️' if s.needs_java_review else ''}"
            for s in sorted_sigs[:10]
        ],
        "guidance": {
            "code_bugs_first": "Fix code_bug crashes first - these are safe mechanical fixes",
            "java_review_required": "Crashes marked needs_java_review require checking Java source before fixing",
            "no_effect_warnings": f"{len(no_effect_warnings)} tests had no observable effect (may indicate test param limitations)"
        }
    }

    REPORT_FILE.write_text(json.dumps(report, indent=2))
    return REPORT_FILE


def run_level(level: int, workers: int = 0, effect_types: list[EffectType] = None,
               thorough: bool = False):
    """Run all combinations at a given level, skipping known crashes.

    Args:
        level: Combination level (1=singles, 2=pairs, 3=triples)
        workers: Number of parallel workers (0 = auto-detect, 1 = single-threaded)
        effect_types: Effect types to test (default: DAMAGE, HEAL, SHIELD)
        thorough: If True, test all scenarios; if False, test baseline only

    Behavior:
    - Loads existing skip list of known crashes
    - Skips combinations in the skip list
    - Tests all other combinations
    - Adds new crashes to skip list
    - Saves updated skip list

    Performance optimizations:
    - Parallel execution with ProcessPoolExecutor
    - Batched processing to reduce overhead
    """
    if effect_types is None:
        effect_types = [EffectType.DAMAGE, EffectType.HEAL, EffectType.SHIELD]

    # Auto-detect workers if not specified
    if workers == 0:
        workers = CPU_COUNT
    use_parallel = workers > 1

    scenarios = TEST_SCENARIOS if thorough else FAST_SCENARIOS

    level_name = {1: "singles", 2: "pairs", 3: "triples", 4: "quads"}[level]
    total = count_combinations(level) * len(effect_types) * len(scenarios)

    mode_str = "thorough" if thorough else "fast"
    parallel_str = f", {workers} workers" if use_parallel else ""
    print(f"=== Testing {level_name} ({mode_str}{parallel_str}): {count_combinations(level)} combinations x {len(effect_types)} effects x {len(scenarios)} scenarios = {total} tests ===")

    # Load skip list of known crashes
    skip_list = load_skip_list()
    initial_skipped = len(skip_list.combinations)

    new_crashes = []
    no_effect_warnings = []
    seen_signatures: set[str] = set()
    tested = 0
    skipped = 0
    start_time = time.time()

    # Generate test cases, skipping known crashes
    test_cases = []
    for keywords in generate_combinations(level):
        for effect_type in effect_types:
            for scenario in scenarios:
                # Check skip list using canonical key (handles A+B == B+A)
                if skip_list.should_skip(keywords, effect_type, scenario.name):
                    skipped += 1
                    continue
                # For parallel: convert scenario to dict for pickling
                scenario_dict = asdict(scenario)
                test_cases.append((keywords, effect_type, scenario_dict))

    if skipped > 0:
        print(f"Skipping {skipped} known-crashing combinations")

    print(f"Testing {len(test_cases)} combinations...")

    if use_parallel and len(test_cases) > 100:
        # Parallel execution for large test sets
        print(f"Using {workers} parallel workers...")

        batch_size = PARALLEL_BATCH_SIZE
        for batch_start in range(0, len(test_cases), batch_size):
            batch_end = min(batch_start + batch_size, len(test_cases))
            batch = test_cases[batch_start:batch_end]

            results = _run_parallel_batch(batch, workers)

            for result in results:
                tested += 1
                if result.crash:
                    is_new = skip_list.add(result.crash)
                    if is_new:
                        new_crashes.append(result.crash)
                        seen_signatures.add(result.crash.signature)
                        cause_str = f" [{result.crash.likely_cause}]" if result.crash.likely_cause != "unknown" else ""
                        java_str = " ⚠️ NEEDS JAVA REVIEW" if result.crash.needs_java_review else ""
                        print(f"  NEW CRASH: {result.crash.signature}{cause_str}{java_str}")
                        print(f"    Keywords: {result.crash.keywords} ({result.crash.effect_type})")
                        print(f"    Location: {result.crash.source_location}")

                if result.no_effect_warning:
                    no_effect_warnings.append(result.no_effect_warning)

            # Progress update after each batch
            elapsed = time.time() - start_time
            rate = tested / elapsed if elapsed > 0 else 0
            remaining = (len(test_cases) - tested) / rate if rate > 0 else 0
            print(f"  Progress: {tested}/{len(test_cases)} ({tested/len(test_cases)*100:.1f}%) - {rate:.1f}/s - ETA: {remaining:.0f}s")

            # Save skip list after each batch
            save_skip_list(skip_list)
    else:
        # Single-threaded execution for small test sets
        for i, (keywords, effect_type, scenario_dict) in enumerate(test_cases):
            scenario = TestScenario(**scenario_dict)
            result = test_keyword_combination(keywords, effect_type, scenario=scenario)
            tested += 1

            if result.crash:
                is_new = skip_list.add(result.crash)
                if is_new:
                    new_crashes.append(result.crash)
                    seen_signatures.add(result.crash.signature)
                    cause_str = f" [{result.crash.likely_cause}]" if result.crash.likely_cause != "unknown" else ""
                    java_str = " ⚠️ NEEDS JAVA REVIEW" if result.crash.needs_java_review else ""
                    print(f"  NEW CRASH: {result.crash.signature}{cause_str}{java_str}")
                    print(f"    Keywords: {result.crash.keywords} ({result.crash.effect_type})")
                    print(f"    Scenario: {scenario.name}")
                    print(f"    Location: {result.crash.source_location}")

            if result.no_effect_warning:
                no_effect_warnings.append(result.no_effect_warning)

            # Progress update at interval
            if tested % CHECKPOINT_INTERVAL == 0:
                elapsed = time.time() - start_time
                rate = tested / elapsed
                remaining = (len(test_cases) - tested) / rate if rate > 0 else 0
                print(f"  Progress: {tested}/{len(test_cases)} ({tested/len(test_cases)*100:.1f}%) - {rate:.1f}/s - ETA: {remaining:.0f}s")
                print(f"    New crashes: {len(new_crashes)}, No-effect warnings: {len(no_effect_warnings)}")
                save_skip_list(skip_list)

    # Final save
    save_skip_list(skip_list)

    elapsed = time.time() - start_time
    print(f"\n=== {level_name} complete ===")
    print(f"  Tested: {tested}")
    print(f"  Skipped (known crashes): {skipped}")
    print(f"  New crashes found: {len(new_crashes)}")
    print(f"  Unique new signatures: {len(seen_signatures)}")
    print(f"  Total in skip list: {len(skip_list.combinations)}")
    print(f"  No-effect warnings: {len(no_effect_warnings)}")
    print(f"  Time: {elapsed:.1f}s ({tested/elapsed:.1f} tests/s)")

    if new_crashes:
        print(f"\n  New crash signatures:")
        for sig in sorted(seen_signatures):
            count = len([c for c in new_crashes if c.signature == sig])
            print(f"    {sig}: {count} crashes")

    print(f"\n  Skip list saved to: {SKIP_FILE}")

    return new_crashes


def summarize_crashes():
    """Print summary of all known crashes from skip list.

    Shows crashes grouped by signature, with example keyword combinations.
    """
    skip_list = load_skip_list()

    if not skip_list.combinations:
        print("No crashes in skip list.")
        print("Run 'pairs' or 'triples' to find crashes.")
        return

    print(f"=== Skip List Summary ===")
    print(f"  Total skipped combinations: {len(skip_list.combinations)}")
    print(f"  Unique signatures: {len(skip_list.signatures)}")
    print()

    # Group by signature
    by_signature: dict[str, list[SkipEntry]] = defaultdict(list)
    for entry in skip_list.combinations.values():
        by_signature[entry.signature].append(entry)

    # Sort by count (most common first)
    sorted_sigs = sorted(by_signature.items(), key=lambda x: -len(x[1]))

    print("=== Crash Signatures (by frequency) ===\n")

    for sig, entries in sorted_sigs:
        example = entries[0]
        java_warning = " ⚠️ NEEDS JAVA REVIEW" if example.needs_java_review else ""
        print(f"[{len(entries)} combos] [{example.likely_cause}] {sig}{java_warning}")
        print(f"  Location: {example.source_location}")
        print(f"  Phase: {example.phase}")
        print(f"  Error: {example.error_message[:80]}...")
        print(f"  Example keywords: {example.keywords}")
        print(f"  First seen: {example.first_seen}")
        print()

    # Show top crash details
    if sorted_sigs:
        top_sig, top_entries = sorted_sigs[0]
        example = top_entries[0]
        print("=== Most Common Crash ===")
        print(f"Signature: {top_sig}")
        print(f"Affects: {len(top_entries)} keyword combinations")
        print(f"Location: {example.source_location}")
        print()
        print("Example test code:")
        # Generate test code
        kw_set = ", ".join(f"Keyword.{kw}" for kw in example.keywords)
        print(f'''def test_regression(self):
    hero = make_hero("Tester", hp=20)
    monster = make_monster("Target", hp=20)
    side = Side(EffectType.{example.effect_type}, 3, {{{kw_set}}})
    hero.die = Die([side] * 6)
    monster.die = Die([Side(EffectType.DAMAGE, 1)] * 6)
    fight = FightLog([hero], [monster])
    fight.use_die(hero, 0, monster)
''')


def clear_all():
    """Clear skip list and all crash data to restart from scratch."""
    cleared = []

    if SKIP_FILE.exists():
        SKIP_FILE.unlink()
        cleared.append("skip list")

    if CHECKPOINT_FILE.exists():
        CHECKPOINT_FILE.unlink()
        cleared.append("legacy checkpoint")

    if CRASH_LOG_DIR.exists():
        for f in CRASH_LOG_DIR.glob("crash_*.json"):
            f.unlink()
        cleared.append("crash log files")

    if REPORT_FILE.exists():
        REPORT_FILE.unlink()
        cleared.append("fuzzer report")

    if cleared:
        print(f"Cleared: {', '.join(cleared)}")
    else:
        print("Nothing to clear.")


# =============================================================================
# Recheck - Re-test skipped combinations to see if they're fixed
# =============================================================================

def recheck(signature: str = None):
    """Re-test skipped combinations and remove from skip list if fixed.

    Args:
        signature: If provided, only recheck combinations with this signature.
                   If None, recheck all skipped combinations.

    Returns:
        Number of combinations that are now fixed.
    """
    skip_list = load_skip_list()

    if not skip_list.combinations:
        print("No combinations in skip list to recheck.")
        return 0

    # Determine which combinations to recheck
    if signature:
        keys_to_check = skip_list.get_keys_for_signature(signature)
        if not keys_to_check:
            print(f"No combinations found with signature: {signature}")
            return 0
        print(f"Rechecking {len(keys_to_check)} combinations with signature: {signature}")
    else:
        keys_to_check = list(skip_list.combinations.keys())
        print(f"Rechecking all {len(keys_to_check)} skipped combinations...")

    fixed = []
    still_crashing = []
    start_time = time.time()

    for i, key in enumerate(keys_to_check):
        entry = skip_list.combinations.get(key)
        if not entry:
            continue

        # Parse the key to reconstruct test parameters
        keywords = tuple(Keyword[kw] for kw in entry.keywords)
        effect_type = EffectType[entry.effect_type]
        scenario = next((s for s in TEST_SCENARIOS if s.name == entry.scenario), FAST_SCENARIOS[0])

        # Test the combination
        result = test_keyword_combination(keywords, effect_type, scenario=scenario)

        if result.crash is None:
            # Fixed!
            fixed.append(key)
            skip_list.remove(key)
            print(f"  FIXED: {entry.keywords} ({entry.effect_type}, {entry.scenario})")
        else:
            still_crashing.append(key)
            # Update last_tested
            entry.last_tested = datetime.now().isoformat()
            entry.test_count += 1

        # Progress update
        if (i + 1) % 100 == 0 or i == len(keys_to_check) - 1:
            elapsed = time.time() - start_time
            rate = (i + 1) / elapsed if elapsed > 0 else 0
            print(f"  Progress: {i+1}/{len(keys_to_check)} - Fixed: {len(fixed)}, Still crashing: {len(still_crashing)}")

    # Save updated skip list
    save_skip_list(skip_list)

    elapsed = time.time() - start_time
    print(f"\n=== Recheck complete ===")
    print(f"  Checked: {len(keys_to_check)}")
    print(f"  Fixed (removed from skip list): {len(fixed)}")
    print(f"  Still crashing: {len(still_crashing)}")
    print(f"  Remaining in skip list: {len(skip_list.combinations)}")
    print(f"  Time: {elapsed:.1f}s")

    if fixed:
        print(f"\n  Fixed combinations:")
        for key in fixed[:10]:  # Show first 10
            print(f"    {key}")
        if len(fixed) > 10:
            print(f"    ... and {len(fixed) - 10} more")

    return len(fixed)


def main():
    parser = argparse.ArgumentParser(
        description="Keyword combination fuzzer with skip list",
        epilog="""
Examples:
  %(prog)s pairs                  # Test pairs, skip known crashes
  %(prog)s recheck                # Re-test all skipped combinations
  %(prog)s recheck --signature X  # Re-test only combos with signature X
  %(prog)s summary                # Show all known crashes
  %(prog)s clear                  # Clear skip list and start fresh
        """
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    # Singles command
    singles = subparsers.add_parser("singles", help="Test single keywords (skips known crashes)")
    singles.add_argument("--workers", type=int, default=0,
                        help=f"Number of parallel workers (0=auto, uses {CPU_COUNT} cores)")
    singles.add_argument("--thorough", action="store_true",
                        help="Test all scenarios (damaged entities, turn 2, etc.)")

    # Pairs command
    pairs = subparsers.add_parser("pairs", help="Test keyword pairs (skips known crashes)")
    pairs.add_argument("--workers", type=int, default=0,
                      help=f"Number of parallel workers (0=auto, uses {CPU_COUNT} cores)")
    pairs.add_argument("--thorough", action="store_true",
                      help="Test all scenarios (damaged entities, turn 2, etc.)")

    # Triples command
    triples = subparsers.add_parser("triples", help="Test keyword triples (skips known crashes)")
    triples.add_argument("--workers", type=int, default=0,
                        help=f"Number of parallel workers (0=auto, uses {CPU_COUNT} cores)")
    triples.add_argument("--thorough", action="store_true",
                        help="Test all scenarios (damaged entities, turn 2, etc.)")

    # All command
    all_cmd = subparsers.add_parser("all", help="Run singles, pairs, then triples")
    all_cmd.add_argument("--workers", type=int, default=0,
                        help=f"Number of parallel workers (0=auto, uses {CPU_COUNT} cores)")
    all_cmd.add_argument("--thorough", action="store_true",
                        help="Test all scenarios (damaged entities, turn 2, etc.)")

    # Summary command
    subparsers.add_parser("summary", help="Show all known crashes from skip list")

    # Clear command
    subparsers.add_parser("clear", help="Clear skip list and all crash data")

    # Recheck command - re-test skipped combinations
    recheck_cmd = subparsers.add_parser("recheck", help="Re-test skipped combinations, unflag if fixed")
    recheck_cmd.add_argument("--signature", "-s", type=str, default=None,
                            help="Only recheck combinations with this error signature")

    # Test specific combination
    test_cmd = subparsers.add_parser("test", help="Test a specific keyword combination")
    test_cmd.add_argument("keywords", nargs="+", help="Keyword names (e.g., ENGAGE COPYCAT)")
    test_cmd.add_argument("--effect", default="DAMAGE", help="Effect type (DAMAGE, HEAL, SHIELD)")
    test_cmd.add_argument("--scenario", default="baseline",
                         choices=["baseline", "src_damaged", "tgt_damaged", "both_damaged", "turn2", "tgt_shielded"],
                         help="Test scenario (default: baseline)")

    args = parser.parse_args()

    if args.command == "singles":
        run_level(1, args.workers, thorough=args.thorough)
    elif args.command == "pairs":
        run_level(2, args.workers, thorough=args.thorough)
    elif args.command == "triples":
        run_level(3, args.workers, thorough=args.thorough)
    elif args.command == "all":
        run_level(1, args.workers, thorough=args.thorough)
        run_level(2, args.workers, thorough=args.thorough)
        run_level(3, args.workers, thorough=args.thorough)
    elif args.command == "summary":
        summarize_crashes()
    elif args.command == "clear":
        clear_all()
    elif args.command == "recheck":
        fixed = recheck(signature=args.signature)
        sys.exit(0 if fixed > 0 else 1)
    elif args.command == "test":
        # Parse keywords and scenario
        try:
            keywords = tuple(Keyword[kw.upper()] for kw in args.keywords)
            effect = EffectType[args.effect.upper()]
            scenario = next(s for s in TEST_SCENARIOS if s.name == args.scenario)
        except KeyError as e:
            print(f"Unknown keyword or effect type: {e}")
            sys.exit(1)
        except StopIteration:
            print(f"Unknown scenario: {args.scenario}")
            sys.exit(1)

        print(f"Testing: {[kw.name for kw in keywords]} with {effect.name} (scenario: {scenario.name})")
        result = test_keyword_combination(keywords, effect, scenario=scenario)

        if result.crash:
            print(f"\nCRASH!")
            print(f"  Phase: {result.crash.phase}")
            print(f"  Scenario: {result.crash.scenario}")
            print(f"  Likely cause: {result.crash.likely_cause}")
            print(f"  Needs Java review: {result.crash.needs_java_review}")
            print(f"  Error: {result.crash.error_type}: {result.crash.error_message}")
            print(f"\nTraceback:\n{result.crash.traceback}")
            sys.exit(1)
        else:
            if result.no_effect_warning:
                print(f"WARNING: {result.no_effect_warning}")
            print("OK - no crash")
            sys.exit(0)


if __name__ == "__main__":
    main()
