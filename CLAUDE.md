# Slice & Dice Combat Reference

Building a new dice-based combat game using Slice & Dice as a reference implementation.

## Goal

Use S&D's decompiled Java as a **behavioral reference** to understand how a complex keyword-based dice combat system works. Not a direct port - building a new game with clean architecture.

## What We Have

```
decompiled/           # READ-ONLY Java source (ground truth for behavior)
combat/
├── src/fight.py      # Python rules engine (~4000 lines)
├── src/dice.py       # Keyword enum, Side/Die classes
├── src/triggers.py   # Buff/debuff system
├── tools/            # Verification tools
└── oracle_tests/     # Test cases from Java analysis
```

### fight.py: Partial Oracle

`fight.py` is a reverse-engineered rules engine. It answers: "If I use die X on target Y, what happens?"

**Verified** (via oracle tests):
- Individual keyword value calculations (184/188 keywords)
- Effect application (damage, heal, shield)
- Targeting rules
- Self-effects, multi-target effects

**Implemented but unverified**:
- Full turn cycle
- Buff duration/expiry
- Multi-turn state
- Keyword interactions (only tested in isolation)

**Not implemented**:
- Hero/monster content definitions
- Items, tactics, spells with mana costs
- Game modes, progression
- Most of `gameplay/` packages

Run `uv run python tools/verify_oracle.py` for current verification status.

## Architecture Note

fight.py mirrors Java structure - large if/elif chains for 188 keywords. For a new game, prefer **data-driven architecture**:

```python
# Keywords as data, not code branches
KEYWORDS = {
    "engage": {"phase": "conditional", "type": "multiplier", "value": 2,
               "condition": {"check": "target_hp_percent", "equals": 100}},
}
```

This makes keywords testable in isolation and easy to add/remove.

## Key Java Files

| File | Contains |
|------|----------|
| `Keyword.java` | All 188 keywords, rules text |
| `FightLog.java` | Combat state machine |
| `EntState.java` | Entity state, keyword resolution |
| `EntSideState.java` | Die side calculations |

## Keyword Pipeline

```
1. Targeting validation  → eliminate, heavy, generous
2. Roll phase            → cantrip, sticky
3. Meta keywords         → copycat, pair, echo
4. Conditional bonuses   → engage, pristine, bloodlust
5. Main effect           → damage/heal/shield
6. Post-processing       → growth, singleUse, manaGain
7. Turn end              → poison, regen
```

## Commands

```bash
cd combat

# See what's verified vs unverified
uv run python tools/verify_oracle.py

# Test robustness (no crashes)
uv run python tools/keyword_fuzzer.py pairs

# Run unit tests
uv run pytest tests/ -v
```

## Principles

1. **Java is behavioral reference** - when unsure how something works, read Java
2. **Spec as source** - oracle tests ARE the spec, no separate docs to rot
3. **Verify before trusting** - fight.py has gaps; don't assume it's complete
