# Slice & Dice Combat System Reverse Engineering

## Current Status: Phase 3 (Keyword Completeness)

**Phase 1 complete**: 36 test methods implemented from original test suite.
**Phase 2 complete**: 9 infrastructure components, 124 passing Python tests.
**Phase 3 active**: Expanding keyword coverage from 11 to full game library (191 keywords).
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
- `decompiled/gameplay/effect/eff/keyword/Keyword.java` - Full keyword enum (191 keywords)

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

The game has 191 keywords. We have 11 implemented. Phase 3 expands coverage.
See `combat/PROGRESS` for complete categorization and tracking.

## Currently Implemented (11)

```
GROWTH, MANA, PETRIFY, RESCUE, RAMPAGE, ENGAGE, RANGED, SINGLE_USE, COPYCAT, CRUEL, PAIR
```

## Complete Keyword Categories (191 total)

### Core Effects (4)
`heal`, `shield`, `damage`, `manaGain`

### Conditional x2 - Self (9)
x2 based on source state:
`pristine`, `deathwish`, `armoured`, `moxie`, `bully`, `reborn`, `patient`, `first`, `sixth`

### Conditional x2 - Target (11)
x2 based on target state:
`engage`, `cruel`, `terminal`, `wham`, `squish`, `uppercut`, `ego`, `serrated`, `century`, `duel`, `tall`

### Conditional x2 - Comparison (5)
x2 comparing source vs target HP:
`underdog`, `overdog`, `dog`, `hyena`, `sloth`

### Conditional x2 - Sequence (3)
x2 based on previous die:
`chain`, `inspired`, `focus`

### Streak Multipliers (7)
Value-based sequences:
`pair`, `trio`, `quin`, `sept`, `step`, `run`, `sprint`

### Pip Modifiers (10)
Modify how N is calculated:
`revDiff`, `doubDiff`, `fault`, `plus`, `doubled`, `squared`, `onesie`, `threesy`, `zeroed`, `treble`

### Pip Bonus (19)
+N pips from game state:
`fizz`, `skill`, `bloodlust`, `defy`, `charged`, `steel`, `flesh`, `rainbow`, `hoard`, `plague`, `acidic`, `vigil`, `flurry`, `fashionable`, `equipped`, `buffed`, `affected`, `rite`, `era`

### Death Triggers (4)
`rampage`, `rescue`, `guilt`, `evil`

### Die Modification (7)
`growth`, `hyperGrowth`, `undergrowth`, `groooooowth`, `doubleGrowth`, `decay`, `stasis`

### Use Modifiers (5)
`singleUse`, `doubleUse`, `quadUse`, `hyperUse`, `cantrip`

### Buff/Debuff (13)
`vulnerable`, `regen`, `poison`, `weaken`, `boost`, `smith`, `permaBoost`, `petrify`, `hypnotise`, `dispel`, `vitality`, `wither`, `cleanse`

### Targeting Restrictions (5)
`eliminate`, `heavy`, `generous`, `scared`, `picky`

### Multi-Hit (4)
`cleave`, `descend`, `duplicate`, `repel`

### Self Effects (6)
`pain`, `death`, `exert`, `manacost`, `mandatory`, `fierce`

### Property Keywords (8)
`permissive`, `sticky`, `enduring`, `dogma`, `resilient`, `unusable`, `tactical`, `lead`

### Meta/Copy (8)
`spy`, `dejavu`, `echo`, `copycat`, `resonate`, `share`, `annul`, `possessed`

### Random Effects (5)
`shifter`, `lucky`, `critical`, `fluctuate`, `fumble`

### Summon/Consume (3)
`boned`, `hyperBoned`, `potion`

### Self Variants (8)
`selfRepel`, `selfPetrify`, `selfPoison`, `selfRegen`, `selfCleanse`, `selfVulnerable`, `selfShield`, `selfHeal`

### Group Variants (5)
`groupExert`, `groupGrowth`, `groupDecay`, `groupSingleUse`, `groupGroooooowth`

### Halve Variants (3)
`halveEngage`, `halveDeathwish`, `halveDuel`

### Anti Variants (5)
`antiEngage`, `antiPristine`, `antiDog`, `antiDeathwish`, `antiPair`

### Swap Variants (4)
`swapDeathwish`, `swapCruel`, `swapEngage`, `swapTerminal`

### Minus Variants (2)
`minusFlesh`, `minusEra`

### Combined Keywords (10)
`engarged`, `cruesh`, `pristeel`, `deathlust`, `trill`, `duegue`, `engine`, `underocus`, `priswish`, `paxin`

### Inflict Keywords (9)
`inflictSelfShield`, `inflictBoned`, `inflictExert`, `inflictPain`, `inflictDeath`, `inflictSingleUse`, `inflictNothing`, `inflictInflictNothing`, `inflictInflictDeath`

### Spell Keywords (6)
`singleCast`, `cooldown`, `deplete`, `channel`, `spellRescue`, `future`

### Special (3)
`nothing`, `removed`, `ranged`

## Implementation Tiers (Priority Order)

| Tier | Name | Keywords | Count |
|------|------|----------|-------|
| 1 | Conditional x2 | Self + Target + Comparison + Sequence | 28 |
| 2 | Pip Bonus | +N based on state | 19 |
| 3 | Streak/Sequence | pair, trio, step, run, etc | 7 |
| 4 | Buff/Debuff | Status effects | 13 |
| 5 | Die Modification | growth, decay, use modifiers | 12 |
| 6 | Self-Targeting | selfX + self effects | 14 |
| 7 | Meta/Copy | echo, resonate, copy keywords | 13 |
| 8 | Targeting/Multi-hit | Restrictions + cleave | 9 |
| 9 | Variants | group/halve/anti/swap/minus | 19 |
| 10 | Combined | Compound keywords | 10 |
| 11 | Inflict | Add keywords to targets | 9 |
| 12 | Other | Spell, property, pip mods, etc | 38 |

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
