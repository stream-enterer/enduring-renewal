# Slice & Dice Combat System Reverse Engineering

## Goal

Reverse engineer the combat system from Slice & Dice to create a fangame. We are reimplementing combat logic, not cloning the full game.

## Methodology: Spec-As-Test

Documentation is lossy. Instead of writing prose docs that drift, we use executable specifications:

1. **Observe** behavior in the real game
2. **Encode** the observation as a test
3. **Implement** just enough to make the test pass
4. **Repeat**

Tests ARE the documentation. If it's not in a test, it's not verified.

## Roadmap: Follow the Original Test Suite

We don't create our own roadmap or scope document - that's just more lossy documentation.

The original developer already wrote a test suite (`decompiled/test/`). It defines:
- **Scope**: What they thought worth testing
- **Ordering**: Simple → complex progression
- **Verified behaviors**: These tests passed against real code

### Test file order (from TestRunner.java)

```
 1. TestStrangeScenarios
 2. TestBasicEff
 3. TestItem
 4. TestComplexEff
 5. TestRandomBits
 6. TestKeyword
 7. TestKeywordSpell
 8. TestParty
 9. TestTriggerOrdering
10. TestBugRepro
11. TestBugReproIgnored
12. TestScattershot
13. TestBannedCombos
14. TestModifierOffer
15. TestUniqueness
16. TestValidation
17. TestCleanse
18. TestFiles
19. TestCollision
20. TestPipe
21. TestHeroes
22. TestMusic
23. TestBattleSim
24. TestAbilities
25. TestBook
26. TestModding
27. TestStates
```

No upfront filtering. When we reach a file, skim it. If clearly non-combat, skip with a note in `combat/PROGRESS`.

### Workflow (per test method)

Each test method follows this flow. Status transitions are tracked in PROGRESS.

```
┌─────────────────────────────────────────────────────────┐
│ 1. ANALYZE                                    [agent]   │
│    Read Java test, form hypothesis about behavior       │
│    Status: analyzing                                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 2. VERIFY                                     [HUMAN]   │
│    Agent presents hypothesis, human tests in real game  │
│    Human confirms, corrects, or reports "can't test"    │
│    Status: verifying                                    │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 3. IMPLEMENT                                  [agent]   │
│    Write Python test encoding verified behavior         │
│    Implement until pytest passes                        │
│    Status: implementing                                 │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ 4. COMMIT                                     [agent]   │
│    Update PROGRESS (move to completed)                  │
│    Git commit with test + implementation                │
│    Advance to next method                               │
└─────────────────────────────────────────────────────────┘
```

**The VERIFY step is a mandatory human gate.** The agent cannot proceed without human confirmation of the behavior.

### Starting a new test file

When `current.method` is `null`, we're at a file boundary. Before diving into methods:

1. **Read the Java test file** - `decompiled/decompiled/test/Test<Name>.java`
2. **List all `@Test` methods** in source order (top to bottom)
3. **Skim for relevance** - If clearly non-combat (e.g., audio, file I/O), skip entire file
4. **Set first method** as `current.method`, status to `analyzing`

Methods are processed in **source order** (order they appear in the Java file).

### File transitions

When all methods in a file are in `completed` or `skipped`:

1. Move to next file in the test file order list (1-27)
2. Set `current.file` to new file, `current.method` to `null`
3. Follow "Starting a new test file" above

When we've worked through all 27 files, we have coverage of everything that mattered to them.

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

`combat/PROGRESS` is a JSON file tracking current position. This is the single source of truth for where we are.

### Format

```json
{
  "current": {
    "file": "TestBasicEff",
    "method": "attackEnemy",
    "status": "verifying",
    "hypothesis": "Attacking an enemy with 1 damage reduces their HP by 1",
    "verified": null
  },
  "completed": [
    {
      "file": "TestBasicEff",
      "method": "basicSanityTest",
      "hypothesis": "setupFight creates 1 hero and 1 monster",
      "verified": "Confirmed in game - Fighter vs Goblin",
      "test": "tests/test_basic_eff.py::test_basic_sanity",
      "timestamp": "2025-01-09T12:00:00Z"
    }
  ],
  "skipped": [
    {
      "file": "TestMusic",
      "reason": "Audio system, not combat",
      "timestamp": "2025-01-09T14:00:00Z"
    }
  ]
}
```

### Fields

- `file` - Current test file name (without .java)
- `method` - Current test method, or `null` if at file boundary (need to list methods)
- `status` - Current phase (see status values below)
- `hypothesis` - Agent's prediction of what the test verifies (set during ANALYZE)
- `verified` - Human's confirmation or correction (set during VERIFY, before IMPLEMENT)

### Status values

- `not_started` - Haven't begun this file/method
- `analyzing` - Reading the Java test, forming hypothesis
- `verifying` - Waiting for human to verify in real game
- `implementing` - Writing Python test + implementation
- `blocked` - Can't proceed; record reason in `hypothesis` field, ask human how to proceed (skip, defer, or investigate further)

### How to use

1. **Start of session**: Read PROGRESS, continue from current status
2. **ANALYZE phase**: Set status to `analyzing`, read Java test, set `hypothesis`
3. **VERIFY phase**: Set status to `verifying`, present hypothesis to human, wait for confirmation
4. **After human confirms**: Set `verified` with human's response
5. **IMPLEMENT phase**: Set status to `implementing`, write test, implement until green
6. **COMMIT phase**: Move current to `completed` (with timestamp), git commit, set next method as current
7. **On skip**: Add to `skipped` with reason, advance to next

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

Phase 1 (test-following) is complete. We extracted 36 combat behaviors from the original test suite and have 77 passing Python tests.

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

## Starting Phase 2

To begin Phase 2:
1. Update PROGRESS to phase 2 format
2. Pick first infrastructure component (recommend: die_sides)
3. Identify simplest blocked test that needs it
4. Follow the infrastructure workflow
