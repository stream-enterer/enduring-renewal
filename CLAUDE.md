# Slice & Dice Combat System Reverse Engineering

## Current Status: Phase 3 (Keyword Completeness)

**Phase 1 complete**: 36 test methods implemented from original test suite.
**Phase 2 complete**: 9 infrastructure components, 124 passing Python tests.
**Phase 3 active**: Expanding keyword coverage from 11 to 191 keywords.

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

## State Machine

```
┌──────────────────────────────────────────────────────────────────┐
│                        ON "CONTINUE"                              │
│                                                                   │
│  1. Read combat/PROGRESS                                          │
│  2. Check current_batch                                           │
│     ├─ non-empty → RESUME (skip to step 4 below)                 │
│     └─ empty → PICK NEXT BATCH                                    │
│                                                                   │
│  PICK NEXT BATCH:                                                 │
│  3. Look up Tier {current_tier} keywords below                    │
│  4. Filter out keywords already in "implemented" list             │
│  5. If tier has remaining keywords:                               │
│     → Pick 1-3 keywords, write to current_batch                   │
│  6. If tier exhausted (no remaining):                             │
│     → Increment current_tier, go to step 3                        │
│  7. If current_tier > 12:                                         │
│     → Phase 3 complete!                                           │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                     IMPLEMENT BATCH                               │
│                                                                   │
│  For each keyword in current_batch:                               │
│  1. Read Keyword.java for exact behavior                          │
│  2. Write failing test capturing behavior                         │
│  3. Implement in appropriate handler                              │
│  4. Run tests until green                                         │
│  5. Add keyword to "implemented" list in PROGRESS                 │
│                                                                   │
│  When batch complete:                                             │
│  → Clear current_batch                                            │
│  → Git commit with keyword names                                  │
└──────────────────────────────────────────────────────────────────┘
```

## PROGRESS Format

```json
{
  "phase": 3,
  "implemented": ["growth", "manaGain", "petrify", ...],
  "current_tier": 1,
  "current_batch": ["pristine", "deathwish"],
  "summary": { "done": 11, "target": 191 }
}
```

---

## Implementation Tiers

Work through tiers in order. Keywords already in `implemented` are marked ~~strikethrough~~.

### Tier 1: Conditional x2 Keywords (28)
x2 multipliers based on conditions.

| Keyword | Condition | Notes |
|---------|-----------|-------|
| ~~engage~~ | target has full HP | done |
| ~~cruel~~ | target at half HP or less | done |
| ~~pair~~ | same value as previous die | done |
| pristine | I have full HP | self-check |
| deathwish | I am dying this turn | self-check |
| armoured | I have shields | self-check |
| moxie | I have least HP of all | self-check |
| bully | I have most HP of all | self-check |
| reborn | I died this fight | self-check |
| patient | I was not used last turn | self-check |
| first | no dice used this turn | self-check |
| sixth | 6th die used this turn | self-check |
| terminal | target on 1 HP | target-check |
| wham | target has shields | target-check |
| squish | target has least HP | target-check |
| uppercut | target has most HP | target-check |
| ego | target is myself | target-check |
| serrated | target gained no shields this turn | target-check |
| century | target has 100+ HP | target-check |
| duel | target is targeting me | target-check |
| tall | target is topmost | target-check |
| underdog | target has more HP than me | comparison |
| overdog | target has less HP than me | comparison |
| dog | target has equal HP to me | comparison |
| hyena | target HP coprime with mine | comparison |
| sloth | target has fewer blank sides | comparison |
| chain | shares keyword with previous die | sequence |
| inspired | previous die had more pips | sequence |
| focus | same target as previous die | sequence |

### Tier 2: Pip Bonus Keywords (19)
+N pips based on game state.

| Keyword | Bonus |
|---------|-------|
| bloodlust | +1 per damaged enemy |
| charged | +1 per stored mana |
| steel | +1 per shield I have |
| flesh | +1 per HP I have |
| fizz | +1 per ability used this turn |
| skill | +1 per my level |
| defy | +1 per incoming damage |
| era | +1 per elapsed turn |
| rainbow | +1 per keyword on this side |
| hoard | +1 per unequipped item |
| plague | +1 per poison on all characters |
| acidic | +1 per poison on me |
| vigil | +1 per defeated ally |
| flurry | +1 per time I've been used this turn |
| fashionable | +1 per total tier of equipped items |
| equipped | +1 per equipped item |
| buffed | +1 per buff I have |
| affected | +1 per trigger affecting me |
| rite | +1 per unused ally (they become used) |

### Tier 3: Streak/Sequence Multipliers (7)
Multipliers based on die value patterns.

| Keyword | Condition | Multiplier |
|---------|-----------|------------|
| ~~pair~~ | same value as prev | x2 (done) |
| trio | same value as 2 prev | x3 |
| quin | same value as 4 prev | x5 |
| sept | same value as 6 prev | x7 |
| step | run of 2 (e.g., 1,2) | x2 |
| run | run of 3 (e.g., 1,2,3) | x3 |
| sprint | run of 5 | x5 |

### Tier 4: Buff/Debuff Application (13)
Apply status effects to targets.

| Keyword | Effect |
|---------|--------|
| ~~petrify~~ | transform N sides to stone (done) |
| poison | +N unblockable damage per turn |
| regen | heal N at end of each turn |
| weaken | -N to all pips for 1 turn |
| boost | +N to all pips for 1 turn |
| vulnerable | take +N damage for 1 turn |
| smith | +N to damage/shield sides for 1 turn |
| permaBoost | +N to all pips this fight |
| hypnotise | set target's damage sides to 0 |
| dispel | remove all traits this fight |
| vitality | +N empty HP this fight |
| wither | -N empty HP this fight |
| cleanse | remove N negative effects |

### Tier 5: Die Modification (12)
Change how dice work.

| Keyword | Effect |
|---------|--------|
| ~~growth~~ | +1 pip after use (done) |
| ~~singleUse~~ | becomes blank after use (done) |
| hyperGrowth | +N pips after use |
| undergrowth | opposite side gets +1 after use |
| groooooowth | all my sides get +1 after use |
| doubleGrowth | growth activates twice |
| decay | -1 pip after use |
| stasis | side cannot change |
| doubleUse | can be used twice per turn |
| quadUse | can be used 4 times per turn |
| hyperUse | can be used N times per turn |
| cantrip | activates during rolling if face-up |

### Tier 6: Self-Targeting (14)
Effects on self or selfX variants.

| Keyword | Effect |
|---------|--------|
| selfShield | shield myself for N |
| selfHeal | heal myself for N |
| selfPoison | poison myself |
| selfRegen | regen on myself |
| selfPetrify | petrify myself |
| selfCleanse | cleanse myself |
| selfVulnerable | vulnerable on myself |
| selfRepel | repel on myself |
| pain | I take N damage |
| death | I die |
| exert | replace all sides with blanks until end of next turn |
| manacost | costs N mana |
| mandatory | must be used if possible |
| fierce | target flees if N or less HP |

### Tier 7: Meta/Copy Keywords (13)
Copy effects from other sources.

| Keyword | Effect |
|---------|--------|
| ~~copycat~~ | copy keywords from previous die (done) |
| echo | copy pips from previous die |
| resonate | copy full effect from previous die |
| share | targets gain my keywords this turn |
| spy | copy keywords from first enemy attack |
| dejavu | copy keywords from sides I used last turn |
| annul | targets lose all keywords this turn |
| possessed | targets as if used by other side |
| shifter | random extra keyword, changes each turn |
| lucky | pips randomized 0 to current, changes each turn |
| critical | 50% chance for +1, rechecks each turn |
| fluctuate | changes to random side each turn |
| fumble | 50% chance to be blank each turn |

### Tier 8: Targeting/Multi-hit (9)
Restrict targets or hit multiple.

| Keyword | Effect |
|---------|--------|
| ~~ranged~~ | can target back row, avoids on-hit (done) |
| eliminate | target must have least HP |
| heavy | target must have most HP |
| generous | cannot target myself |
| scared | target must have N or less HP |
| picky | target must have exactly N HP |
| cleave | also hits both sides of target |
| descend | also hits below target |
| duplicate | copy this onto all allied sides this turn |
| repel | N damage to all enemies attacking target |

### Tier 9: Variant Keywords (19)
Variants of existing keywords.

**Group (5):** groupExert, groupGrowth, groupDecay, groupSingleUse, groupGroooooowth
**Halve (3):** halveEngage, halveDeathwish, halveDuel
**Anti (5):** antiEngage, antiPristine, antiDog, antiDeathwish, antiPair
**Swap (4):** swapDeathwish, swapCruel, swapEngage, swapTerminal
**Minus (2):** minusFlesh, minusEra

### Tier 10: Combined Keywords (10)
Compound effects from two keywords.

| Keyword | Combination |
|---------|-------------|
| engarged | engage + charged |
| cruesh | cruel + flesh |
| pristeel | pristine + steel |
| deathlust | deathwish + bloodlust |
| trill | trio + skill |
| duegue | duel + plague |
| engine | engage + pristine (x4) |
| underocus | underdog + focus (x4) |
| priswish | pristine + deathwish (x4) |
| paxin | pair XOR chain (x3) |

### Tier 11: Inflict Keywords (9)
Add keywords to target's sides for a turn.

inflictSelfShield, inflictBoned, inflictExert, inflictPain, inflictDeath,
inflictSingleUse, inflictNothing, inflictInflictNothing, inflictInflictDeath

### Tier 12: Remaining Keywords (38)

**Core Effects (4):** heal, shield, damage, ~~manaGain~~
**Death Triggers (4):** ~~rampage~~, ~~rescue~~, guilt, evil
**Pip Modifiers (10):** revDiff, doubDiff, fault, plus, doubled, squared, onesie, threesy, zeroed, treble
**Property (8):** permissive, sticky, enduring, dogma, resilient, unusable, tactical, lead
**Summon (3):** boned, hyperBoned, potion
**Spell (6):** singleCast, cooldown, deplete, channel, spellRescue, future
**Special (3):** nothing, removed, ~~ranged~~

---

## Implementation Patterns

### Conditional x2 Keywords
```python
if Keyword.PRISTINE in keywords:
    if source.hp == source.max_hp:
        value *= 2
```

### Pip Bonus Keywords
```python
if Keyword.BLOODLUST in keywords:
    damaged_enemies = sum(1 for e in enemies if e.hp < e.max_hp)
    value += damaged_enemies
```

### Streak Keywords
```python
# Track: fight_log._die_value_history
if Keyword.TRIO in keywords:
    if len(history) >= 2 and history[-1] == history[-2] == current_value:
        value *= 3
```

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
