# Slice & Dice Combat Library

Reverse engineering the combat system from Slice & Dice as a standalone library.

**Status:** All keywords implemented. Automated verification in progress.

## Important: Robustness vs Correctness

The fuzzer tests **robustness** (no crashes), not **correctness** (right behavior).

| Phase | Goal | Method | Output |
|-------|------|--------|--------|
| **Robustness** (fuzzer) | No crashes | Exhaustive keyword combinations | Crash-free code |
| **Correctness** (unit tests) | Spec-compliant behavior | Tests derived from Java source | Verified implementation |

**The fuzzer passing is necessary but not sufficient.** A fix that prevents a crash might still be semantically wrong. See the Fix Review Checklist below.

## Automated Workflow

The fuzzer uses a **skip list** to track known crashes:

```
┌─────────────────────────────────────────────────────────────────┐
│  1. Run fuzzer - skips known crashes, logs new ones            │
│  2. Review crashes via `summary` command                       │
│  3. Fix bugs manually (check Java source for semantics)        │
│  4. Run `recheck` to re-test and unflag fixed combinations     │
└─────────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

- **No auto-fix**: The fuzzer logs crashes but does NOT automatically fix them
- **Skip list persistence**: Known crashes stored in `crash_logs/skip_list.json`
- **Symmetric handling**: A+B and B+A treated as the same combination (canonical ordering)
- **Manual review required**: Fixes require checking Java source for correctness

### When Asked to "continue" or "fuzz"

```bash
cd combat && uv run python tools/keyword_fuzzer.py pairs
```

The fuzzer will:
1. Skip combinations already in the skip list
2. Test all other combinations
3. Add new crashes to the skip list
4. Report results

After making fixes:
```bash
# Re-test skipped combinations, remove from skip list if fixed
uv run python tools/keyword_fuzzer.py recheck

# Or recheck only a specific signature
uv run python tools/keyword_fuzzer.py recheck --signature "AttributeError@fight.py:123"
```

### Crash Classification

The fuzzer classifies crashes by likely cause:

| Classification | Description | Action |
|---------------|-------------|--------|
| `code_bug` | Missing null check, KeyError, etc. | Safe to fix mechanically |
| `param_limitation` | Crash only in specific scenarios | May be edge case, check Java |
| `semantic_decision` | Fix requires understanding behavior | **Must check Java source** |
| `interaction_bug` | Keywords interact unexpectedly | **Must check Java source** |
| `logic_error` | Invariant violated | Trace data flow carefully |

Crashes marked `needs_java_review: true` require checking Java source before fixing.

### Fix Review Checklist

Before committing a fix, verify:

1. **Is this a code bug or a semantic decision?**
   - Code bug: Missing null check, wrong dict access, etc. Fix is mechanical.
   - Semantic decision: How should COPYCAT behave with no previous die? **Requires Java source.**

2. **Does the fix add defensive code?**
   - `if x is None: return` → SUSPICIOUS. What SHOULD happen when x is None?
   - `x = y or default` → SUSPICIOUS. Is default the correct behavior?
   - Try/except that swallows error → SUSPICIOUS. Are you hiding a bug?

3. **Did you check the Java source?**
   - For semantic decisions, cite the relevant Java code in the commit.
   - Format: `Per FightLog.java:234, COPYCAT uses current die if no previous`

4. **Is this combination actually valid?**
   - Some keyword combos may be game-illegal (never appear together on real dice)
   - Check Keyword.java for exclusivity rules

### Fuzzer Commands

```bash
# === Scanning (skips known crashes) ===
uv run python tools/keyword_fuzzer.py singles              # ~500 tests, fast
uv run python tools/keyword_fuzzer.py pairs                # ~53K tests, ~6 seconds
uv run python tools/keyword_fuzzer.py triples              # ~3.2M tests, ~7 minutes
uv run python tools/keyword_fuzzer.py all                  # Run all levels

# === Thorough Mode (tests multiple scenarios) ===
uv run python tools/keyword_fuzzer.py pairs --thorough     # 6x more tests
# Scenarios: baseline, src_damaged, tgt_damaged, both_damaged, turn2, tgt_shielded

# === Crash Analysis ===
uv run python tools/keyword_fuzzer.py summary              # View skip list with crash details

# === Recheck (after making fixes) ===
uv run python tools/keyword_fuzzer.py recheck              # Re-test all skipped combinations
uv run python tools/keyword_fuzzer.py recheck -s "sig"     # Recheck only specific signature

# === Testing Specific Combos ===
uv run python tools/keyword_fuzzer.py test ENGAGE COPYCAT
uv run python tools/keyword_fuzzer.py test BLOODLUST --scenario src_damaged
uv run python tools/keyword_fuzzer.py test GROWTH POISON --effect HEAL

# === Maintenance ===
uv run python tools/keyword_fuzzer.py clear                # Clear skip list and start fresh
```

### Test Scenarios

The `--thorough` flag tests keywords under different game states:

| Scenario | State | Keywords Exercised |
|----------|-------|-------------------|
| `baseline` | Full HP, no shields, turn 1 | Default state |
| `src_damaged` | Source at 50% HP | BLOODLUST, ANTI_PRISTINE, BERSERK |
| `tgt_damaged` | Target at 50% HP | ANTI_ENGAGE, EXECUTE |
| `both_damaged` | Both at 50% HP | Combined state-dependent |
| `turn2` | Previous die used | COPYCAT, ECHO |
| `tgt_shielded` | Target has shield | SHATTER, shield interactions |

### No-Effect Warnings

The fuzzer warns when a die use produces no observable state change. This may indicate:
- Test parameters don't exercise the keywords properly
- Keywords cancelled each other out
- Bug in implementation

Keywords that may legitimately produce no effect: `SCARED`, `PICKY`, `GENEROUS`, `HEAVY`, `ELIMINATE`

### Skip List Format

The skip list is stored at `crash_logs/skip_list.json`:

```json
{
  "version": 2,
  "combinations": {
    "KEYWORD_A,KEYWORD_B:EFFECT:scenario": {
      "key": "...",           // Canonical key (sorted keywords)
      "signature": "...",     // Error signature for grouping
      "error_type": "...",
      "error_message": "...",
      "first_seen": "...",    // ISO timestamp
      "last_tested": "...",   // Updated on recheck
      "keywords": [...],
      "traceback": "..."
    }
  },
  "signatures": {
    "ErrorType@file:line": ["key1", "key2", ...]  // Groups by root cause
  }
}
```

Key features:
- **Canonical keys**: Keywords sorted alphabetically (A+B == B+A)
- **Signature grouping**: Crashes grouped by error location for bulk fixes
- **Recheck tracking**: `last_tested` updated when rechecking

**phase** indicates where the crash occurred:
- `creation` - Side/Die/Entity creation
- `use_die` - During die usage
- `get_state` - State retrieval
- `next_turn` - Turn processing
- `invariant_check` - Post-turn validation

## Ground Truth

**Java source** is authoritative spec. **Python tests** verify we match it.

```
decompiled/           # READ-ONLY Java source (reference)
combat/
├── src/              # Implementation
├── tests/            # Unit tests (spec-as-test)
├── tools/            # Automation (fuzzer, etc.)
├── examples/         # Combat loop examples
└── crash_logs/       # Machine-readable crash reports
```

## State Tracking

`combat/KEYWORDS.json` tracks implementation state:
- `verified`: keywords confirmed via human gameplay testing
- `implemented`: keywords with passing unit tests
- `blocked`: keywords that can't be implemented (UI-only, etc.)
- `all`: complete enum matching Java Keyword.java

**Permanently blocked (3):** `permissive`, `potion`, `removed`

## Keyword Pipeline

```
1. Targeting validation     → eliminate, heavy, generous, scared, picky
2. Roll phase               → cantrip, sticky
3. Meta keywords            → copycat, pair, echo (transforms Side)
4. Conditional bonuses      → engage, pristine, bloodlust (x2 or +N)
5. Main effect              → apply damage/heal/shield
6. Post-processing          → growth, singleUse, manaGain
7. Turn end                 → poison, regen, shifter, fluctuate
```

## Reference Files

| File | Purpose |
|------|---------|
| `Keyword.java` | Enum definition, rules text, conditional types |
| `FightLog.java` | Combat state, die resolution |
| `EntState.java` | Entity state snapshots |
| `conditionalBonus/*.java` | How keywords modify values |

**Reading Keyword.java:**
```java
// Format: name(color, "rules text", ConditionType, isSourceCheck)
engage(Colours.yellow, "with full hp", StateConditionType.FullHP, false),
//                                                                ^^^^^ false = check TARGET
pristine(Colours.light, "have full hp", StateConditionType.FullHP, true),
//                                                                 ^^^^ true = check SOURCE
```

## Tooling

```bash
# Run all tests
cd combat && uv run pytest

# Run specific test file
uv run pytest tests/test_keyword.py -v

# Filter by keyword name
uv run pytest -k "engage"

# Run basic combat loop
uv run python examples/basic_combat.py
```

## Adding Regression Tests

The fuzzer generates test code for each crash. To add as permanent coverage:

```python
# In tests/test_keyword.py
def make_hero(name: str, hp: int = 5) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)

def make_monster(name: str, hp: int = 4) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)

class TestRegressions:
    def test_engage_copycat_crash(self):
        """Regression: AttributeError with ['ENGAGE', 'COPYCAT']."""
        hero = make_hero("Tester", hp=20)
        monster = make_monster("Target", hp=20)

        side = Side(EffectType.DAMAGE, 3, {Keyword.ENGAGE, Keyword.COPYCAT})
        hero.die = Die([side] * 6)
        monster.die = Die([Side(EffectType.DAMAGE, 1)] * 6)

        fight = FightLog([hero], [monster])
        fight.use_die(hero, 0, monster)  # Should not crash
```

## Goals

**Short-term:** Combat simulation CLI
**Long-term:** Fan game reimplementation

The fuzzer validates robustness before building the CLI. Correctness is verified by unit tests derived from Java source.
