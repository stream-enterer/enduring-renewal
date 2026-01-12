# Oracle Test Triage: Post-Triage Workflow

This document outlines the steps to take after triaging all 47 oracle test failures into their root cause categories.

---

## Triage Output Format

After triage, each failure should be categorized into one of three buckets:

| Category | Description | Fix Location |
|----------|-------------|--------------|
| **ORACLE_WRONG** | LLM hallucinated incorrect expected value | `oracle_tests/generated_tests.json` |
| **IMPL_WRONG** | Python implementation doesn't match Java | `src/fight.py` (or related) |
| **HARNESS_GAP** | Test context not set up correctly | `tools/verify_oracle.py` |

Expected distribution (rough estimate based on initial analysis):
- ORACLE_WRONG: ~50-60% (LLM-generated tests are unreliable)
- IMPL_WRONG: ~20-30% (actual bugs to fix)
- HARNESS_GAP: ~10-20% (missing context setup)

---

## Phase 1: Fix Harness Gaps

**Priority: HIGH** — These are blocking accurate test results.

### 1.1 Identify All Harness Gaps

From triage, collect all failures marked HARNESS_GAP. Common patterns:

| Missing Setup | Affected Keywords | Fix |
|---------------|-------------------|-----|
| `_targeters_this_turn` | DUEL | Record which entities targeted source |
| `_sides_used_per_turn` | DEJAVU | Record prior use of same side |
| `_first_enemy_attack_this_turn` | SPY | Set mock enemy attack |
| `_unique_effect_types_used` | RAINBOW | Track effect type history |
| Edge case handling | COPYCAT | Handle None when no previous die |

### 1.2 Implementation Pattern

For each harness gap:

```python
# In setup_context() or run_value_test()

# Example: DUEL needs targeter tracking
if "target_is_targeting_source" in context:
    if context["target_is_targeting_source"]:
        # Record that target has targeted source this turn
        if source not in fight._targeters_this_turn:
            fight._targeters_this_turn[source] = set()
        fight._targeters_this_turn[source].add(target)
```

### 1.3 Verification

After fixing harness gaps:
1. Re-run affected tests
2. If they now pass → harness was the issue
3. If they still fail → re-categorize (likely ORACLE_WRONG or IMPL_WRONG)

---

## Phase 2: Fix Oracle Test Errors

**Priority: MEDIUM** — Establishes correct ground truth.

### 2.1 Batch by Pattern

Group ORACLE_WRONG failures by the type of error:

| Error Pattern | Example | Batch Fix |
|---------------|---------|-----------|
| Visibility modifiers misunderstood | DOUBLED, SQUARED, etc. | All expect self-modification, but these only affect observers |
| Compound keyword math wrong | ENGARGED expects 14, should be 28 | Recalculate all compound keyword expectations |
| Targeting returns 0 | ELIMINATE, HEAVY, etc. | Decide on convention: 0 or calculated value |
| Multiplier stacking wrong | ENGAGE+CRUEL expects x4 | Verify stacking rules, fix expectations |

### 2.2 Fix Format

For each oracle fix in `generated_tests.json`:

```json
{
  "id": "engarged_triggers",
  "keywords": ["ENGAGE", "BERSERK"],
  "base_value": 10,
  "expected_value": 28,  // Changed from 14
  "_fix_note": "Per FightLog.java:1234, BERSERK +4 applies before ENGAGE x2: (10+4)*2=28"
}
```

### 2.3 Bulk Update Script

For large batches, create a script:

```python
# tools/fix_oracle_tests.py
import json

with open('oracle_tests/generated_tests.json') as f:
    data = json.load(f)

fixes = {
    # Visibility modifiers don't affect self
    "doubled_modifier": {"expected_value": 14, "reason": "DOUBLED only affects observers"},
    "squared_modifier": {"expected_value": 14, "reason": "SQUARED only affects observers"},
    # ... etc
}

for test in data['tests']:
    if test['id'] in fixes:
        test['expected_value'] = fixes[test['id']]['expected_value']
        test['_fix_reason'] = fixes[test['id']]['reason']

with open('oracle_tests/generated_tests.json', 'w') as f:
    json.dump(data, f, indent=2)
```

### 2.4 Documentation

Create `oracle_tests/FIXES.md` documenting all oracle corrections:

```markdown
# Oracle Test Fixes

## Visibility Modifiers (7 tests)
DOUBLED, SQUARED, ONESIE, THREESY, ZEROED, PLUS, FAULT

**Original expectation**: These modify the die's own calculated value
**Correct behavior**: These only modify how OTHER dice see this die (for PAIR matching)
**Fix**: Changed expected_value to base calculated value (no self-modification)
**Citation**: Keyword.java:456 - VisibilityType enum

## Compound Keywords (7 tests)
...
```

---

## Phase 3: Fix Implementation Bugs

**Priority: HIGH** — These are actual bugs in the combat system.

### 3.1 Pre-Fix Checklist

Before fixing any implementation bug:

- [ ] Verified against Java source (not just oracle test)
- [ ] Understood the full context of the keyword
- [ ] Checked for related keywords that might be affected
- [ ] Identified the specific file/function to modify

### 3.2 Fix Format

Each implementation fix must include:

```python
# In src/fight.py or related file

# KEYWORD: Brief description of fix
# Per {JavaFile}.java:{line}, {explanation}
def _apply_era_bonus(self, value: int, source_state: EntityState) -> int:
    """Apply ERA keyword bonus: +N where N = elapsed turns."""
    # Per FightLog.java:1234, ERA adds turn count to value
    if self._turn > 0:
        return value + self._turn
    return value
```

### 3.3 Regression Test

For each implementation fix:

1. **Add unit test** in `tests/test_keyword.py`:
   ```python
   def test_era_adds_turn_count(self):
       """ERA adds +N where N = elapsed turns. Per FightLog.java:1234."""
       # Setup
       fight = create_fight()
       fight._turn = 4

       # Execute
       result = fight._apply_era_bonus(10, source_state)

       # Verify
       assert result == 14  # 10 + 4 turns
   ```

2. **Verify oracle test passes**:
   ```bash
   uv run python tools/verify_oracle.py --filter era_bonus
   ```

3. **Run fuzzer** to check for regressions:
   ```bash
   uv run python tools/keyword_fuzzer.py test ERA
   ```

### 3.4 Commit Format

```
Fix {KEYWORD} to match Java behavior

{Brief description of the bug and fix}

Per {JavaFile}.java:{line}: {relevant quote or description}

Fixes oracle tests: {test_id_1}, {test_id_2}
```

---

## Phase 4: Verification

### 4.1 Full Test Suite

After all fixes:

```bash
# Run oracle verification
uv run python tools/verify_oracle.py

# Expected output:
# Total:   248
# Passed:  225+  (was 178)
# Failed:  0     (was 46)
# Skipped: 23    (behavioral tests)
# Errors:  0     (was 1)
```

### 4.2 Fuzzer Validation

Ensure fixes don't introduce crashes:

```bash
uv run python tools/keyword_fuzzer.py pairs
uv run python tools/keyword_fuzzer.py recheck
```

### 4.3 Unit Test Suite

```bash
uv run pytest tests/ -v
```

---

## Phase 5: Documentation Update

### 5.1 Update KEYWORDS.json

Move keywords from `implemented` to `verified` as oracle tests pass:

```json
{
  "verified": ["ENGAGE", "PRISTINE", "CRUEL", "ERA", ...],
  "implemented": [...],
  "blocked": ["PERMISSIVE", "POTION", "REMOVED"]
}
```

### 5.2 Update CLAUDE.md

Add oracle test status to the project documentation:

```markdown
## Oracle Test Status

| Category | Count | Status |
|----------|-------|--------|
| Value tests | 180 | 180 passing |
| Targeting tests | 20 | 20 passing |
| Effect tests | 25 | 25 passing |
| Behavioral tests | 23 | Skipped (not implemented) |
```

### 5.3 Create Test Coverage Map

Document which keywords have oracle test coverage:

```markdown
## Keyword Test Coverage

| Keyword | Oracle Tests | Unit Tests | Fuzzer | Status |
|---------|--------------|------------|--------|--------|
| ENGAGE | 3 | 5 | ✓ | Verified |
| PRISTINE | 2 | 3 | ✓ | Verified |
| ERA | 2 | 1 | ✓ | Verified |
| ... | ... | ... | ... | ... |
```

---

## Decision Log

Track key decisions made during triage and fixes:

| Decision | Rationale | Date |
|----------|-----------|------|
| Targeting keywords return calculated value, not 0 | Game UI prevents invalid use; value calculation is separate | YYYY-MM-DD |
| Visibility modifiers don't affect self | Per Keyword.java, these only modify observer perception | YYYY-MM-DD |
| Compound keywords: additive before multiplicative | Per FightLog.java:1234, bonuses apply then multipliers | YYYY-MM-DD |

---

## Appendix: Common Patterns

### Pattern A: Multiplicative Keywords (x2, x3, etc.)

Keywords that multiply value when condition met:
- ENGAGE, PRISTINE, CRUEL, DEATHWISH (x2)
- TRIO (x3), QUIN (x5), SEPT (x7)

**Stacking rule**: Check Java for whether multiple x2 keywords stack (x4) or not.

### Pattern B: Additive Keywords (+N)

Keywords that add a bonus:
- STEEL (+shields), FLESH (+HP), ERA (+turns)
- BLOODLUST (+damaged enemies), CHARGED (+mana)

**Application order**: Additive bonuses apply to base value before multipliers.

### Pattern C: Conditional Keywords

Keywords that only trigger under specific conditions:
- Source conditions: PRISTINE (full HP), ARMOURED (has shield)
- Target conditions: ENGAGE (full HP), CRUEL (half HP)
- Global conditions: MOXIE (least HP), BULLY (most HP)

### Pattern D: Visibility Keywords

Keywords that modify how OTHER dice perceive this die:
- DOUBLED (appear as 2x), FAULT (appear as -1)
- Used for PAIR matching, not self-calculation

---

## Checklist

### Pre-Triage
- [ ] Read this document
- [ ] Have Java source files accessible
- [ ] Understand the three categories (ORACLE_WRONG, IMPL_WRONG, HARNESS_GAP)

### During Triage
- [ ] Document each failure with Java citation
- [ ] Note patterns that appear multiple times
- [ ] Flag uncertain cases for review

### Post-Triage Fixes
- [ ] Fix harness gaps first (unblocks accurate testing)
- [ ] Batch oracle fixes by pattern
- [ ] Fix implementation bugs with unit tests
- [ ] Verify all fixes with full test suite

### Completion
- [ ] All oracle tests pass (except skipped)
- [ ] Fuzzer shows no regressions
- [ ] Unit tests pass
- [ ] Documentation updated
- [ ] KEYWORDS.json updated
