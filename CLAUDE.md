# Slice & Dice Combat Reimplementation

Reverse engineering the combat system from Slice & Dice for a fangame.

## Methodology: Spec-As-Test

Documentation rots. Tests don't. Ground truth lives in:

1. **Java source** - What the keyword should do (authoritative spec)
2. **Python tests** - Executable verification that we match the spec

**NOT** in JSON metadata, markdown descriptions, or comments.

Workflow:
1. **Observe** behavior in the real game or Java source
2. **Encode** as a failing test
3. **Implement** until test passes
4. **Repeat**

## State Tracking

`combat/KEYWORDS.json` tracks only:
- `implemented`: keywords with passing tests
- `all`: complete enum (191 keywords)

Remaining = `all` - `implemented`. Currently: 17 done, 174 remaining.

## On "continue"

1. Read `combat/KEYWORDS.json`, compute remaining keywords
2. Pick next keyword (agent's choice - related keywords, dependencies, or just enum order)
3. For each keyword:
   - Read Java source: `decompiled/gameplay/effect/eff/keyword/Keyword.java`
   - Read conditionalBonus if needed: `decompiled/gameplay/effect/eff/conditionalBonus/`
   - Write failing test first
   - Implement until green
   - Add to `implemented` array
4. Run `uv run pytest` - all tests must pass
5. Commit: `Implement <keyword> keyword` or `Implement <x>, <y>, <z> keywords`
6. Report progress

## Existing Implementations

These keywords are already implemented and tested. They stay where they are:

| Keywords | Location | Notes |
|----------|----------|-------|
| pair, copycat | `EntityState._process_meta_keywords()` | Meta-keywords that modify the side before value calculation |
| manaGain, growth, singleUse | `FightLog.use_die()` | Effects applied after/alongside main effect |
| engage, cruel, pristine, deathwish, armoured, moxie, bully, reborn | `FightLog._apply_conditional_keyword_bonuses()` | Conditional x2 multipliers |
| petrify | `FightLog` | Transforms sides to stone |
| rampage, rescue | `FightLog` | Death-trigger reuse |
| ranged | `FightLog` | Targeting modifier |

## Keyword Pipeline

Keywords run at different stages. **Check which stage before implementing.**

```
Full resolution order:
1. Targeting validation        # Can this target be selected?
2. Roll phase                  # During dice rolling
3. _process_meta_keywords()    # Modifies the Side itself
4. _apply_conditional_keyword_bonuses()  # Modifies the calculated value
5. use_die() main effect       # Apply damage/heal/shield
6. use_die() post-processing   # Side effects after resolution
7. Turn end                    # End-of-turn effects
```

| Stage | When | Example Keywords | What It Does |
|-------|------|------------------|--------------|
| Targeting | Before use | eliminate, heavy, generous, scared, picky | Restricts valid targets |
| Roll | During rolling | cantrip, sticky | Affects roll behavior |
| Meta | Before value calc | copycat, pair, echo | Transforms the Side (copy keywords/pips) |
| Conditional | Value calculation | engage, pristine, bloodlust | Multiplies (x2) or adds (+N) to value |
| Post-effect | After resolution | growth, singleUse, manaGain | Modifies die, grants mana |
| Turn-timing | Turn start/end | poison, regen, shifter, fluctuate | Per-turn effects |

**How to identify stage:**
- Restricts which targets are valid → Targeting
- Affects rolling/rerolling → Roll
- Copies/transforms the side itself → Meta
- Multiplies value (x2) or adds pips (+N) → Conditional
- Happens after the effect resolves → Post-effect
- Says "each turn" or "at end of turn" → Turn-timing

## Infrastructure Dependencies

Some keywords need systems that may not exist yet. **Check before implementing.**

| Infrastructure | Keywords That Need It | Status |
|----------------|----------------------|--------|
| Turn-end processing | poison, regen | Check `FightLog` for turn hooks |
| Turn-start processing | shifter, fluctuate, lucky, critical, fumble | Check `FightLog` for turn hooks |
| Buff/debuff system | weaken, boost, vulnerable, smith, permaBoost | Check for temporary modifier system |
| Entity summoning | boned, hyperBoned | Check for summon capability |
| Side injection | inflict* keywords | Check for modifying target's dice |
| Usage tracking | doubleUse, quadUse, hyperUse, flurry | Check for per-turn usage counter |

**If infrastructure is missing:** Either implement it first, or skip the keyword and note the dependency.

## Variant Keywords

Variants modify base keywords. **Do NOT duplicate logic - compose or reference.**

| Prefix | What It Does | Java Pattern | Example |
|--------|--------------|--------------|---------|
| `anti*` | Inverts condition | `NotRequirement` | antiEngage = x2 if target NOT full HP |
| `halve*` | x0.5 instead of x2 | `.halveVersion()` | halveEngage = x0.5 if target full HP |
| `swap*` | Swaps source/target | swapped condition | swapEngage = x2 if SOURCE full HP |
| `group*` | Applies to all allies | `groupAct` field | groupGrowth = all allies get growth |
| `minus*` | Inverts bonus (+N → -N) | `InvertBonus` | minusFlesh = -1 per HP I have |

**Combined keywords** (engarged, engine, paxin) use `KeywordCombineType` in Java:
- `ConditionBonus`: condition from A, bonus from B (engarged = engage + charged)
- `TC4X`: both conditions must be true → x4 (engine = engage AND pristine)
- `XOR`: exactly one condition true → x3 (paxin = pair XOR chain)

## Parameter (N) Source

Keywords with "N" in their description get N from the **Side's pip value** unless noted.

```
hyperGrowth  → +N pips (N = side.pips)
scared       → target must have N or less HP (N = side.pips)
pain         → I take N damage (N = side.pips)
```

**Exceptions (hardcoded values):**
- `century` → 100 HP (see `ParamCondition.OrMoreHp`)
- `terminal` → 1 HP (see `ParamCondition.ExactlyHp`)
- `sixth` → 6th die (not parameterized by pips)

## Keyword Stacking

When multiple conditional keywords apply, they **multiply independently**:

```
engage (x2) + cruel (x2) = x4 (if both conditions met)
pristine (x2) + treble = x3 (treble upgrades x2 → x3)
```

Check `_apply_conditional_keyword_bonuses()` for actual stacking behavior. Tests verify correctness.

## Project Structure

```
decompiled/           # READ-ONLY Java source (reference)
combat/
├── KEYWORDS.json     # State tracking only (implemented list)
├── src/
│   ├── dice.py       # Keyword enum, Side, Die
│   ├── fight.py      # FightLog, EntityState, keyword handling
│   └── ...
└── tests/            # Ground truth - if not tested, not verified
```

## Tooling

```bash
cd combat && uv run pytest          # Run all tests
uv run pytest tests/test_x.py -v    # Single file
uv run pytest -k "keyword_name"     # Filter by name
```

## Reference Files

| File | Purpose |
|------|---------|
| `Keyword.java` | Enum definition, rules text, conditional bonus types |
| `FightLog.java` | Combat state, die resolution, effect application |
| `EntState.java` | Entity state snapshots (HP, shields, etc.) |
| `conditionalBonus/*.java` | How keywords modify values |

## Deferred (need infrastructure)

- `TestTriggerOrdering.testTriggerHPOrdering` - needs ModifierLib
- `TestTriggerOrdering.testCreakyJointsSword` - needs Items
- `TestStates.test2hpStates` - needs Save/Load
