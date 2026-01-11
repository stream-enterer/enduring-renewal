# Slice & Dice Combat System Reverse Engineering

## Current Status: Phase 3 (Keyword Completeness)

**Phase 1 complete**: 36 test methods implemented from original test suite.
**Phase 2 complete**: 9 infrastructure components, 124 passing Python tests.
**Phase 3 active**: Expanding keyword coverage from ~11 to full game library (~150).
**On "Continue"**: Read PROGRESS, follow the keyword workflow below.

---

## Goal

Reverse engineer the combat system from Slice & Dice to create a fangame. We are reimplementing combat logic, not cloning the full game.

## Methodology: Spec-As-Test

Documentation is lossy. Instead of writing prose docs that drift, we use executable specifications:

1. **Observe** behavior in the real game
2. **Encode** the observation as a test
3. **Implement** just enough to make the test pass
4. **Repeat**

Tests ARE the documentation. If it's not in a test, it's not verified.

## Project Structure

```
CLAUDE.md         # This file - agent instructions
HUMAN.md          # Human operator guide (prompts, problem handling)

decompiled/       # READ-ONLY Java decompilation (chmod -R a-w)
                  # Reference only. Never modify. Never import from.

combat/           # Python reimplementation + test harness
  src/            # Combat system implementation
  tests/          # Spec-as-test files
  PROGRESS        # JSON state file (see below)

notes/            # Scratch space (gitignored)
```

## Tooling

- **Python 3.13** with **uv** (not pip)
- **pytest** for test harness
- Run all tests: `uv run pytest` from `combat/`
- Run single test: `uv run pytest tests/test_file.py::test_name -v`

## Key Reference Files

Keywords source:
- `decompiled/gameplay/effect/eff/keyword/Keyword.java` - Full keyword enum (~150 keywords)

Combat logic:
- `decompiled/gameplay/fightLog/FightLog.java` - Central combat state manager
- `decompiled/gameplay/fightLog/EntState.java` - Entity state snapshots

Conditional bonus system:
- `decompiled/gameplay/effect/eff/conditionalBonus/` - How keywords modify values

## Conventions

- Keep tests atomic: one behavior per test
- Name tests after what they verify: `test_bloodlust_adds_pip_per_damaged_enemy`
- When uncertain about behavior, verify in the real game first
- Don't guess. Test.

---

# Phase 3: Keyword Completeness

The game has ~150 keywords. We have ~11 implemented. Phase 3 expands coverage.

## Currently Implemented Keywords

```python
GROWTH      # After use, side value increases by +1
MANA        # Effect also grants mana equal to value
PETRIFY     # Turns target's sides to stone (Blank)
RESCUE      # Die recharged if heal saves a dying hero
RAMPAGE     # Die recharged if attack kills an enemy
ENGAGE      # x2 effect vs full HP targets
RANGED      # Can hit back row, avoids on-hit passives
SINGLE_USE  # Side becomes blank after use
COPYCAT     # Copies keywords from previous die
CRUEL       # x2 effect vs targets at half HP or less
PAIR        # x2 effect if previous die had same value
```

## Keyword Categories (Priority Order)

### Tier 1: Conditional x2 Keywords (high impact, common)
Similar to ENGAGE/CRUEL - double effect based on condition.

| Keyword | Condition | Target/Self |
|---------|-----------|-------------|
| pristine | I have full HP | self |
| deathwish | I am dying this turn | self |
| armoured | I have shields | self |
| wham | target has shields | target |
| terminal | target on 1 HP | target |
| moxie | I have least HP of all | self |
| bully | I have most HP of all | self |
| squish | target has least HP | target |
| uppercut | target has most HP | target |

### Tier 2: Pip Bonus Keywords (dynamic value modification)
Add pips based on game state.

| Keyword | Bonus Source |
|---------|--------------|
| bloodlust | +1 per damaged enemy |
| charged | +1 per stored mana |
| steel | +1 per shield I have |
| flesh | +1 per HP I have |
| fizz | +1 per ability used this turn |
| skill | +1 per my level/tier |
| defy | +1 per incoming damage |
| era | +1 per elapsed turn |

### Tier 3: Streak/Combo Keywords
Based on die sequences.

| Keyword | Condition | Multiplier |
|---------|-----------|------------|
| trio | 3 dice same value | x3 |
| quin | 5 dice same value | x5 |
| step | run of 2 (eg 1,2) | x2 |
| run | run of 3 (eg 1,2,3) | x3 |
| sprint | run of 5 | x5 |
| chain | shares keyword with prev | x2 |
| inspired | prev die had more pips | x2 |

### Tier 4: Buff/Debuff Application
Apply effects to target.

| Keyword | Effect |
|---------|--------|
| regen | heal N at end of each turn |
| poison | (already have) |
| weaken | -N to all pips for 1 turn |
| boost | +N to all pips for 1 turn |
| vulnerable | take +N damage for 1 turn |
| cleanse | remove N negative effects |

### Tier 5: Die Modification
Change how dies work.

| Keyword | Effect |
|---------|--------|
| doubleUse | can be used twice per turn |
| stasis | side cannot change |
| sticky | cannot be rerolled |
| decay | -1 pip after each use |
| hyperGrowth | +N pips after use |

### Tier 6: Self-Targeting Variants
selfX versions of existing keywords.

| Keyword | Effect |
|---------|--------|
| selfShield | shield myself for N |
| selfHeal | heal myself for N |
| selfPoison | poison myself |
| selfRegen | regen on myself |

### Tier 7: Meta/Copy Keywords
Copy effects from other sources.

| Keyword | Effect |
|---------|--------|
| echo | copy pips from previous die |
| resonate | copy full effect from previous die |
| share | targets gain my keywords |
| spy | copy keywords from first enemy attack |

### Tier 8: Targeting Restrictions
Limit valid targets.

| Keyword | Restriction |
|---------|-------------|
| eliminate | target must have least HP |
| heavy | target must have most HP |
| generous | cannot target myself |
| scared | target must have N or less HP |

## Keyword Workflow

```
┌─────────────────────────────────────────────────────────┐
│ 1. SELECT                                      [agent]   │
│    Pick next keyword(s) from priority tier               │
│    Read Keyword.java for exact behavior                  │
│    Check ConditionalBonus if applicable                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 2. TEST                                        [agent]   │
│    Write failing test capturing keyword behavior         │
│    Test edge cases (stacking, ordering, etc)             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 3. VERIFY                                      [HUMAN]   │
│    Human confirms behavior matches real game             │
│    (Skip if behavior is obvious from source)             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 4. IMPLEMENT                                   [agent]   │
│    Add keyword to Keyword enum                           │
│    Implement in appropriate handler                      │
│    Run tests until green                                 │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 5. COMMIT                                      [agent]   │
│    Update PROGRESS with new keyword count                │
│    Git commit with keyword name(s)                       │
└─────────────────────────────────────────────────────────┘
```

## PROGRESS Format for Phase 3

```json
{
  "phase": 3,
  "keywords": {
    "implemented": ["GROWTH", "MANA", "PETRIFY", ...],
    "current_tier": 1,
    "current_batch": ["pristine", "deathwish"],
    "count": 11
  }
}
```

## Implementation Patterns

### Conditional x2 Keywords
Most x2 keywords use the same pattern - check condition, double value:

```python
# In use_die or effect application:
if Keyword.PRISTINE in keywords:
    if source_state.hp == source_state.max_hp:
        value *= 2
```

### Pip Bonus Keywords
Add to calculated value based on game state:

```python
# In get_side_state or effect calculation:
if Keyword.BLOODLUST in keywords:
    damaged_enemies = sum(1 for e in enemies if e.hp < e.max_hp)
    value += damaged_enemies
```

### Streak Keywords
Track die history and check patterns:

```python
# Need to track: fight_log._die_value_history
if Keyword.TRIO in keywords:
    if len(history) >= 2 and history[-1] == history[-2] == current_value:
        value *= 3
```

## On "Continue"

1. Read `combat/PROGRESS` - check `keywords.current_tier` and `current_batch`
2. If batch is empty → pick next keywords from current tier
3. Follow the keyword workflow above
4. When tier complete → move to next tier
5. Commit after each batch (1-3 keywords)

---

# Completed Phases

## Phase 1: Test-Following (Complete)
36 test methods from original test suite implemented.

## Phase 2: Infrastructure (Complete)
9 components built:
- die_sides, side_mutation, used_state, mana
- triggers, engage_keyword, copycat_keyword, pair_keyword
- poison_cleanse

124 Python tests passing. 3 tests deferred (need ModifierLib or save/load).
