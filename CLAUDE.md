# Slice & Dice Combat Library

Reverse engineering the combat system from Slice & Dice.

## Ground Truth

**Java source is authoritative.** Python implementation must match it.

```
decompiled/           # READ-ONLY Java reference
combat/
├── src/              # Python implementation
├── tests/            # Unit tests
├── tools/            # Fuzzer, oracle verifier
└── oracle_tests/     # LLM-generated test cases
```

## Two Verification Layers

| Layer | Tool | Tests | Goal |
|-------|------|-------|------|
| **Robustness** | `keyword_fuzzer.py` | Keyword combinations | No crashes |
| **Correctness** | `verify_oracle.py` | Value calculations | Match Java behavior |

Both must pass. Fuzzer passing is necessary but not sufficient.

## Current Task: Oracle Test Triage

See **`docs/WORKFLOW.md`** for the complete workflow.

```bash
cd combat

# Check triage progress
uv run python tools/triage.py status

# Get next failure to investigate
uv run python tools/triage.py next

# Record verdict after investigation
uv run python tools/triage.py set <test_id> <VERDICT> "<notes>" --citation "<file:line>"

# Verify fix worked
uv run python tools/triage.py verify <test_id>
```

Priority order: P0 (errors) → P1 (harness gaps) → P2 (oracle fixes) → P3 (impl fixes)

## Fix Review Checklist

Before committing any fix:

1. **Is this mechanical or semantic?**
   - Mechanical (null check, KeyError): fix directly
   - Semantic (behavior decision): **check Java source first**

2. **Cite Java source** for semantic fixes:
   ```
   Per FightLog.java:234, COPYCAT uses current die if no previous
   ```

3. **Avoid defensive hacks:**
   - `if x is None: return` - What SHOULD happen?
   - `try/except: pass` - Are you hiding a bug?

## Reference Files

| File | Contains |
|------|----------|
| `Keyword.java` | Enum, rules text, condition types |
| `FightLog.java` | Combat state, die resolution |
| `EntState.java` | Entity state, condition checks |
| `conditionalBonus/*.java` | Bonus calculations |

Reading keyword definitions:
```java
// name(color, "rules", ConditionType, isSourceCheck)
engage(..., StateConditionType.FullHP, false),  // check TARGET
pristine(..., StateConditionType.FullHP, true), // check SOURCE
```

## Keyword Pipeline

```
1. Targeting validation  → eliminate, heavy, generous, scared, picky
2. Roll phase            → cantrip, sticky
3. Meta keywords         → copycat, pair, echo
4. Conditional bonuses   → engage, pristine, bloodlust
5. Main effect           → damage/heal/shield
6. Post-processing       → growth, singleUse, manaGain
7. Turn end              → poison, regen, shifter
```

## Commands

```bash
cd combat

# Oracle verification (correctness)
uv run python tools/verify_oracle.py              # All tests
uv run python tools/verify_oracle.py --filter X   # Specific test
uv run python tools/verify_oracle.py -v           # Verbose

# Fuzzer (robustness)
uv run python tools/keyword_fuzzer.py pairs       # Test combinations
uv run python tools/keyword_fuzzer.py recheck     # Re-test after fixes
uv run python tools/keyword_fuzzer.py summary     # View crash list

# Unit tests
uv run pytest tests/ -v
uv run pytest -k "engage"
```

## State Tracking

- `combat/triage_state.json` - Oracle test triage progress
- `combat/KEYWORDS.json` - Implementation status per keyword
- `combat/crash_logs/skip_list.json` - Known fuzzer crashes

## Blocked Keywords

These cannot be implemented (UI-only): `permissive`, `potion`, `removed`
