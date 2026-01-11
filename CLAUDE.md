# Slice & Dice Combat Reimplementation

Reverse engineering the combat system from Slice & Dice for a fangame.

## Source of Truth

**`combat/KEYWORDS.json`** is the immutable contract. 191 keywords with:
- `id`, `category`, `condition`, `effect`, `java_hint` - READ ONLY
- `implemented`, `tested` - agent may set to `true`

## On "continue"

1. Read `combat/KEYWORDS.json`
2. Filter `implemented: false`, group by category
3. Pick category with most remaining keywords
4. Implement ALL keywords in that category:
   - Add handler to `combat/src/keywords/<category>.py`
   - Add test to `combat/tests/test_keywords.py`
5. Run `uv run pytest` until green
6. Update KEYWORDS.json: `implemented: true`, `tested: true`
7. Commit: `Implement <category> keywords: x, y, z`
8. Report progress, prompt for next continue

## Structure

```
combat/
├── KEYWORDS.json              # Contract (191 keywords)
├── src/
│   ├── keywords/              # Create on first use
│   │   ├── __init__.py        # Exports registry API
│   │   ├── registry.py        # KeywordContext, @keyword_handler
│   │   └── <category>.py      # One per category
│   ├── dice.py, fight.py...   # Core implementation
└── tests/
```

## Tooling

```bash
cd combat && uv run pytest
uv run pytest tests/test_file.py::test_name -v
```

## Reference

| File | Purpose |
|------|---------|
| `decompiled/gameplay/effect/eff/keyword/Keyword.java` | Keyword enum |
| `decompiled/gameplay/fightLog/FightLog.java` | Combat state |
| `decompiled/gameplay/effect/eff/conditionalBonus/` | Bonus system |

## Handler Patterns

```python
# Conditional x2 (source)
@keyword_handler(Keyword.PRISTINE)
def pristine(value: int, ctx: KeywordContext) -> int:
    if ctx.source_state.hp == ctx.source_state.max_hp:
        return value * 2
    return value

# Conditional x2 (target)
@keyword_handler(Keyword.ENGAGE)
def engage(value: int, ctx: KeywordContext) -> int:
    if ctx.target_state.hp == ctx.target_state.max_hp:
        return value * 2
    return value

# Pip bonus
@keyword_handler(Keyword.BLOODLUST)
def bloodlust(value: int, ctx: KeywordContext) -> int:
    damaged = sum(1 for e in ctx.fight_log.enemies if e.hp < e.max_hp)
    return value + damaged
```

## Special Cases (not in registry)

| Keyword | Location | Reason |
|---------|----------|--------|
| pair, copycat | `EntityState._process_meta_keywords()` | Modifies side before calculation |
| manaGain, growth, singleUse | `FightLog.use_die()` | Post-effect handling |

## Deferred Tests

- `TestTriggerOrdering.testTriggerHPOrdering` - needs ModifierLib
- `TestTriggerOrdering.testCreakyJointsSword` - needs Items
- `TestStates.test2hpStates` - needs Save/Load
