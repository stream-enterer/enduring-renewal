# Slice & Dice Combat Reference

Reverse-engineered combat system from Slice & Dice, used as behavioral reference for Sylvanshine (C++20 tactics game).

## Goal

Port S&D's keyword-based dice combat to Sylvanshine. Use `fight.py` as a **behavioral oracle** — not as code to copy, but as a reference for how keywords should behave.

See `docs/sylvanshine_integration.md` for the full integration map.

## Structure

```
decompiled/           # READ-ONLY Java source (ground truth)
combat/
├── src/fight.py      # Python rules engine (~4000 lines)
├── src/dice.py       # Keyword enum, Side/Die classes
├── tools/            # Verification tools
└── oracle_tests/     # Test cases from Java analysis
```

## fight.py: Partial Oracle

Answers: "If I use die X with keywords Y on target Z, what happens?"

**Verified** (run `uv run python tools/verify_oracle.py`):
- Individual keyword value calculations
- Effect application (damage, heal, shield)
- Targeting rules, self-effects, multi-target

**Not verified**:
- Full turn cycle, multi-turn state
- Keyword interactions beyond isolated tests
- Content (heroes, monsters, items)

## Key Files

| File | Purpose |
|------|---------|
| `Keyword.java` | All 188 keywords, rules text |
| `EntState.java` | Keyword resolution logic |
| `fight.py` | Python port (partial oracle) |
| `docs/sylvanshine_integration.md` | Concept mapping to Sylvanshine |

## Commands

```bash
cd combat

# Verification status
uv run python tools/verify_oracle.py

# Robustness (no crashes)
uv run python tools/keyword_fuzzer.py pairs
```

## Principles

1. **Java is ground truth** — when behavior is unclear, read Java
2. **fight.py is reference, not foundation** — has gaps, don't assume completeness
3. **Data-driven for Sylvanshine** — don't port if/elif chains, build clean keyword engine
