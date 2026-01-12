# Oracle Test Verification Workflow

Iterable workflow for triaging and fixing oracle test failures.

## Current Status

| Metric | Count |
|--------|-------|
| Total | 248 |
| Passed | 178 |
| Failed | 46 |
| Errors | 1 |
| Skipped | 23 |

Run `uv run python tools/triage.py status` to see progress.

## State Machine

```
         ┌─────────────────────────────────────────────────────┐
         │                                                     │
         v                                                     │
    ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
    │  SCAN   │────>│ TRIAGE  │────>│   FIX   │────>│ VERIFY  │
    └─────────┘     └─────────┘     └─────────┘     └─────────┘
         │               │               │               │
         v               v               v               v
    triage.py        triage.py       Edit files      triage.py
    scan             set verdict     per verdict     verify
```

## Quick Start

```bash
cd combat

# Scan failures into state file
uv run python tools/triage.py scan

# See what needs triaging
uv run python tools/triage.py next

# Record verdict for a failure
uv run python tools/triage.py set patient_triggers IMPL_WRONG "Check used_last_turn" --citation "Keyword.java:59"

# After fixing, verify
uv run python tools/triage.py verify patient_triggers
```

## State Persistence

State persists in `combat/triage_state.json`:

```json
{
  "version": 1,
  "last_scan": "2025-01-12T10:30:00Z",
  "stats": {"total": 248, "passed": 178, "failed": 46, "errors": 1},
  "failures": {
    "patient_triggers": {
      "status": "triaged",
      "verdict": "IMPL_WRONG",
      "notes": "Check used_last_turn default",
      "citation": "Keyword.java:59",
      "priority": "P2"
    }
  }
}
```

Statuses: `untriaged` → `triaged` → `fixed` → `verified`

## Priority Levels

| Priority | Criteria | Action |
|----------|----------|--------|
| **P0** | Errors (crashes) | Fix immediately - blocks testing |
| **P1** | HARNESS_GAP | Quick wins - unblocks accurate testing |
| **P2** | ORACLE_WRONG | Batch fixable - update expected values |
| **P3** | IMPL_WRONG | Requires Java analysis - most effort |

Start with P0, then P1, then batch P2, then tackle P3.

## Verdicts

| Verdict | Meaning | Fix Location |
|---------|---------|--------------|
| `ORACLE_WRONG` | LLM hallucinated expected value | `oracle_tests/generated_tests.json` |
| `IMPL_WRONG` | Python doesn't match Java | `src/fight.py` |
| `HARNESS_GAP` | Test context incomplete | `tools/verify_oracle.py` |

## Phase 1: SCAN

Populate state file with current failures:

```bash
uv run python tools/triage.py scan
```

Output: Creates/updates `triage_state.json` with all failures.

**Artifact**: `triage_state.json` with status=`untriaged` for new failures.

## Phase 2: TRIAGE

Pick next untriaged failure and investigate:

```bash
# See next failure to triage
uv run python tools/triage.py next

# See details of a specific test
uv run python tools/verify_oracle.py --filter patient_triggers -v
```

### Investigation Checklist

1. **Read the test** - What keywords? What's expected vs actual?
2. **Check Java source** - Find relevant code in `decompiled/`
3. **Determine verdict** - Which component is wrong?
4. **Record decision**:

```bash
uv run python tools/triage.py set <test_id> <VERDICT> "<notes>" --citation "<file:line>"
```

### Decision Patterns

| Pattern | Verdict | Verified Source |
|---------|---------|-----------------|
| Visibility modifier expects self-modification | ORACLE_WRONG | KUtils.java:1114 - "Other keywords see..." |
| Got 0 for targeting keyword | HARNESS_GAP | Missing validation context |
| Additive bonus not applied | Check tracking | May be IMPL or HARNESS |
| Compound math off by x2 | Check order | Java: additive then multiplicative |

**Artifact**: Updated `triage_state.json` with verdict and citation.

## Phase 3: FIX

Based on verdict, fix the appropriate component.

### HARNESS_GAP Fix

Add context setup in `tools/verify_oracle.py`:

```python
if "target_is_targeting_source" in context:
    if context["target_is_targeting_source"]:
        fight._targeters_this_turn.setdefault(source, set()).add(target)
```

### ORACLE_WRONG Fix

Update expected value in `oracle_tests/generated_tests.json`:

```json
{
  "id": "doubled_modifier",
  "expected_value": 14,
  "_fix_note": "DOUBLED only affects observers per KUtils.java:1114"
}
```

### IMPL_WRONG Fix

Fix Python with Java citation in comment:

```python
# Per Keyword.java:59, PATIENT triggers when UnusedLastTurn
if Keyword.PATIENT in keywords and not source._used_last_turn:
    multiplier *= 2
```

**Artifact**: Code changes with citations.

## Phase 4: VERIFY

Run test and update state:

```bash
uv run python tools/triage.py verify <test_id>
```

This runs the specific test and:
- If passes: marks status=`verified`
- If fails: marks status=`triaged` (re-investigate)

### Regression Check

After each fix, run full suite to catch regressions:

```bash
uv run python tools/verify_oracle.py
```

If new failures appear, investigate before proceeding.

**Artifact**: Updated pass/fail counts, verified status.

## Triage Tool Commands

```bash
# State management
uv run python tools/triage.py scan                    # Refresh failures from oracle tests
uv run python tools/triage.py status                  # Show progress summary
uv run python tools/triage.py next                    # Show next untriaged failure
uv run python tools/triage.py next --priority P1     # Show next P1 failure

# Recording verdicts
uv run python tools/triage.py set <id> <verdict> "<notes>" [--citation "file:line"]
uv run python tools/triage.py show <id>              # Show details of a failure

# Verification
uv run python tools/triage.py verify <id>            # Verify single test
uv run python tools/triage.py verify --all           # Verify all fixed

# Batch operations
uv run python tools/triage.py list --status triaged  # List by status
uv run python tools/triage.py list --verdict ORACLE_WRONG  # List by verdict
```

## Failure Categories

| Category | Count | Priority | Keywords |
|----------|-------|----------|----------|
| Errors | 1 | P0 | COPYCAT |
| State-Conditional | 5 | P3 | PATIENT, SERRATED, SLOTH, TALL |
| Sequence | 5 | P3 | RUN, SPRINT, DUEL, DEJAVU, SPY |
| Additive Bonuses | 8 | P2/P3 | ERA, RAINBOW, SKILL, etc. |
| Visibility Modifiers | 7 | P2 | DOUBLED, SQUARED, ONESIE, etc. |
| Targeting | 5 | P2 | ELIMINATE, HEAVY, GENEROUS, etc. |
| Compound | 7 | P3 | ENGARGED, PRISTEEL, etc. |
| DOUB_DIFF/REV_DIFF | 6 | P3 | Complex value modifiers |
| Misc | 3 | P3 | SWAP_TERMINAL, HALVE_DUEL, FIERCE |

### Category Notes

**Visibility Modifiers (P2 - likely ORACLE_WRONG)**
Per KUtils.java:1114, these use `describeOthersSeeingNPips()` = "Other keywords see N as X".
They affect PAIR matching only, not self-calculation. Oracle tests expecting self-modification are wrong.

**Targeting (P2 - likely ORACLE_WRONG)**
Targeting validity is separate from value calculation.
Game UI prevents invalid use; we calculate value regardless.

**State-Conditional / Sequence / Compound (P3 - needs Java analysis)**
Check `Keyword.java` for condition definitions, `FightLog.java` for state tracking.

## Completion Criteria

- [ ] All P0 errors fixed (0 remaining)
- [ ] All failures triaged (47/47)
- [ ] All fixes verified (0 failures)
- [ ] Fuzzer shows no regressions
- [ ] Each IMPL_WRONG fix has Java citation

## Reference

```bash
# Oracle tests
uv run python tools/verify_oracle.py              # Run all
uv run python tools/verify_oracle.py --filter X   # Filter by ID

# Triage state
uv run python tools/triage.py status              # Progress
uv run python tools/triage.py next                # Next task

# Fuzzer (robustness)
uv run python tools/keyword_fuzzer.py pairs       # Crash testing

# Unit tests
uv run pytest tests/ -v
```
