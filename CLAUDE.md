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

New keywords can be added to existing locations OR to a new `keywords/` registry - whatever makes sense for each keyword.

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
