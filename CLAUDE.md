# Slice & Dice Combat System Reverse Engineering

## Current Status: Phase 2 (Infrastructure)

**Phase 1 complete**: 36 test methods implemented, 77 passing Python tests.
**Phase 2 active**: Building infrastructure to unblock 16 remaining tests.
**On "Continue"**: Read PROGRESS, follow the infrastructure workflow below.

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

## Phase 1 (Complete): Test-Following

We followed the original developer's test suite (`decompiled/test/`), processing each test method:
1. **Analyze** - Read Java test, form hypothesis
2. **Verify** - Human confirms behavior in real game
3. **Implement** - Write Python test + code until green
4. **Commit** - Update PROGRESS, git commit

**Results**: 36 tests implemented across 7 files. 18 files skipped (non-combat). 16 tests blocked (need infrastructure). Details in `combat/PROGRESS`.

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

## State Tracking: PROGRESS

`combat/PROGRESS` is a JSON file tracking current position. Single source of truth.

**On session start**: Read PROGRESS, check `phase`, follow the appropriate workflow.

## Tooling

- **Python 3.13** with **uv** (not pip)
- **pytest** for test harness
- Run all tests: `uv run pytest` from `combat/`
- Run single test: `uv run pytest tests/test_file.py::test_name -v`

## Key Reference Files (in decompiled/decompiled/)

Combat logic:
- `decompiled/decompiled/gameplay/fightLog/FightLog.java` - Central combat state manager
- `decompiled/decompiled/gameplay/fightLog/EntState.java` - Entity state snapshots
- `decompiled/decompiled/gameplay/effect/` - Effects, buffs, damage

Test patterns (how the original dev tested):
- `decompiled/decompiled/test/util/TestUtils.java` - Fight setup helpers
- `decompiled/decompiled/test/TestBasicEff.java` - Basic effect tests
- `decompiled/decompiled/test/TestTriggerOrdering.java` - Trigger/ordering tests

Entity types:
- `decompiled/decompiled/gameplay/content/ent/` - Hero, Monster, Die, Side

## Conventions

- Keep tests atomic: one behavior per test
- Name tests after what they verify: `test_shield_blocks_before_damage`
- When uncertain about behavior, verify in the real game first
- Don't guess. Test.

---

# Phase 2: Infrastructure

**16 tests remain blocked** because they require infrastructure we haven't built:
- Die/Sides system (getSideState, turnInto, getCalculatedEffect)
- Mana system (getTotalMana, shieldMana)
- Trigger system (getActivePersonals, addTrigger)

Phase 2 uses the **same methodology** (spec-as-test, verify before implement) but the source changes:
- Phase 1: Test methods → told us what behaviors to implement
- Phase 2: Blocked tests → tell us what infrastructure to build

## Infrastructure Components

Based on analysis of blocked tests and decompiled source, we need:

### 1. Die/Sides System (unblocks ~12 tests)

**What it is**: Each entity has a Die with 6 Sides. Each Side has a base effect that can be modified by triggers/items.

**Key concepts**:
- `Die` - Has 6 `EntSide` objects, tracks which side is "up" (lockedSide)
- `EntSide` - Base effect + texture + keywords
- `EntSideState` - Calculated effect after applying all triggers/bonuses
- `getSideState(index)` - Returns the calculated state for a side
- `getCalculatedEffect()` - The final effect after all modifiers
- `turnInto(side, newEffect)` - Replace a side's effect (for growth, petrify, etc.)
- `isUsed()` - Whether the die has been used this turn

**Key source files**:
- `decompiled/gameplay/content/ent/die/Die.java`
- `decompiled/gameplay/content/ent/die/side/EntSide.java`
- `decompiled/gameplay/fightLog/EntSideState.java`

### 2. Mana System (unblocks ~3 tests)

**What it is**: Resource pool for casting spells. Some effects grant mana, others consume it.

**Key concepts**:
- `getTotalMana()` - Current mana pool
- `shieldMana(N)` - Grants N shields AND N mana
- Mana persists across turns (unlike shields by default)

### 3. Trigger System (unblocks ~6 tests)

**What it is**: Personal triggers that modify sides or respond to events.

**Key concepts**:
- `Personal` - A trigger attached to an entity
- `getActivePersonals()` - List of active triggers
- `addTrigger(trigger)` - Add a new trigger
- `AffectSides` - Trigger that modifies die sides (add keywords, flat bonus, etc.)

## Infrastructure Workflow

```
┌─────────────────────────────────────────────────────────┐
│ 1. IDENTIFY                                    [agent]   │
│    Pick a blocked test                                   │
│    Read what infrastructure it needs                     │
│    Research decompiled Java classes                      │
│    Status: researching                                   │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 2. DESIGN                                      [agent]   │
│    Propose minimal Python implementation                 │
│    Write failing test based on blocked test behavior     │
│    Status: designing                                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 3. VERIFY                                      [HUMAN]   │
│    Human reviews design, tests in game if needed         │
│    Confirms approach or suggests changes                 │
│    Status: verifying                                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 4. IMPLEMENT                                   [agent]   │
│    Build infrastructure                                  │
│    Run tests until green                                 │
│    Status: implementing                                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 5. UNBLOCK                                     [agent]   │
│    Move blocked tests to ready                           │
│    Commit infrastructure + tests                         │
│    Status: complete                                      │
└─────────────────────────────────────────────────────────┘
```

## Recommended Order

Build infrastructure in dependency order, maximizing unblocked tests:

1. **Die + Side basics** - Die class, Side class, entity.sides array
2. **EntSideState** - getSideState(), getCalculatedEffect()
3. **Side mutation** - turnInto(), side replacement
4. **Used state** - isUsed(), marking sides as used
5. **Mana** - mana pool, shieldMana
6. **Triggers** - Personal, AffectSides, addTrigger

## PROGRESS Format for Phase 2

```json
{
  "phase": 2,
  "infrastructure": {
    "current": {
      "component": "die_sides",
      "status": "implementing",
      "design": "Add Die class with sides array, EntSideState for calculated effects",
      "unblocks": ["TestKeyword.growth", "TestTriggerOrdering.testBuffReplacedSides"]
    },
    "completed": [
      {
        "component": "die_sides",
        "tests_added": ["tests/test_die.py::TestDieSides"],
        "tests_unblocked": ["TestKeyword.growth"],
        "timestamp": "2025-01-11T..."
      }
    ]
  },
  "blocked": [...],
  "completed": [...],
  "skipped": [...]
}
```

## Key Differences from Phase 1

| Aspect | Phase 1 | Phase 2 |
|--------|---------|---------|
| Source of truth | Original test methods | Blocked tests + decompiled source |
| Granularity | One test method at a time | One infrastructure component at a time |
| Verification | "Does X behave like Y in game?" | "Does this design enable the blocked tests?" |
| Success metric | Test passes | Previously-blocked tests pass |

## On "Continue"

1. Read `combat/PROGRESS` - check `infrastructure.current`
2. If `status: "not_started"` → begin IDENTIFY step for that component
3. If `status: "verifying"` → present design to human, wait for approval
4. If `status: "implementing"` → continue building until tests pass
5. Follow the infrastructure workflow above
