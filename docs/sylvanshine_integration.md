# S&D Combat → Sylvanshine Integration Map

Mapping the Slice & Dice combat system to Sylvanshine's C++20 tactics engine.

## Tech Stack

| S&D | Sylvanshine |
|-----|-------------|
| Python (`fight.py`, ~4000 lines) | C++20 with SDL3 |

**Decision needed**: Port to C++, embed Python, or use fight.py as reference-only?

## Concept Mapping

| S&D Concept | Sylvanshine Equivalent | Status |
|-------------|------------------------|--------|
| `Entity` | `Entity` | Close match |
| `hp`, `max_hp` | `hp`, `max_hp` | Exact |
| `shields` | — | Missing |
| `Die` (6 sides) | — | Core addition |
| `Side` (effect + value + keywords) | `attack_power` (single int) | Needs replacement |
| 188 Keywords | 3 Modifiers | Major expansion |
| `FightLog` (state machine) | `game_logic.cpp` | Different model |
| `PendingDamage` | `PendingDamage` | Exists |
| Buffs (poison, regen, etc.) | `Modifier` | Partial |
| Turn end processing | — | Missing |

## Sylvanshine's Current Combat

```cpp
// entity.hpp - simple stats
int hp;
int max_hp;
int attack_power;

// Damage flow in game_logic.cpp
int damage = attacker.apply_damage_dealt(attacker.attack_power);
int final = target.apply_damage_taken(damage);
target.take_damage(final);
```

Simple: `attack_power` → modifier chain → HP. No dice, no keywords.

## S&D Combat Flow

```
Die Selection → Side Resolution → Keyword Pipeline → Effect Application
     ↓              ↓                    ↓                   ↓
  Which die?    Base value      ENGAGE x2, CRUEL x2    Damage/Heal/Shield
                + effect type   POISON, GROWTH, etc.   + status effects
```

Complexity is in keyword resolution (188 keywords, interaction rules, phase ordering).

## Integration Points

| Sylvanshine Location | Integration |
|---------------------|-------------|
| `Entity` struct (`entity.hpp`) | Add `Die`, `shields`, buff system |
| `Modifier` (`modifier.hpp`) | Expand to keyword system or replace |
| `try_ai_attack()` (`game_logic.cpp:415`) | Replace `attack_power` with die resolution |
| `process_pending_damage()` (`game_logic.cpp:549`) | Add keyword post-processing |
| `TurnPhase` enum (`game_state.hpp`) | Add turn-end phase for poison/regen |

## Gaps

### Missing Concepts

1. **Dice** — No concept of die sides with different effects
2. **Shields** — Only HP exists
3. **Status effects** — No poison, regen, petrify, vulnerable, etc.
4. **Keyword system** — 3 hardcoded modifiers vs 188 data-driven keywords
5. **Turn-end processing** — No poison tick, regen heal, buff expiry

### Architecture Pattern

Sylvanshine's `Modifier` is close to S&D's keyword concept:

```cpp
// Current Sylvanshine (modifier.hpp)
struct ArmorModifier : Modifier {
    int modify_damage_taken(int dmg) const override {
        return std::max(0, dmg - armor_value);
    }
};

// S&D equivalent pattern
struct EngageKeyword : Keyword {
    int modify_value(int base, Context& ctx) const override {
        return ctx.target_at_full_hp() ? base * 2 : base;
    }
};
```

The pattern exists — needs expansion and data-driving.

## What Fits Easily

- Entity model (add fields to existing struct)
- Pending damage (system already exists)
- Turn structure (extend existing `TurnPhase`)
- Animation scheduler (already supports attack/damage sequences)

## What Needs Design

- **Dice system** — New UI and data model
- **Keyword resolution engine** — The core complexity
- **Status effect system** — Buffs/debuffs with duration
- **Data format** — How keywords are defined (code vs data)

## Core Question

Two paths:

1. **Port fight.py to C++** — Translate 188 if/elif branches, maintain parity
2. **Data-driven C++ engine** — Use fight.py as behavioral reference, build clean keyword system

Option 2 is more work upfront but avoids inheriting fight.py's reverse-engineered structure.

## Reference

- `fight.py` — Partial oracle for keyword behavior (see `verify_oracle.py` for coverage)
- `decompiled/gameplay/effect/eff/keyword/Keyword.java` — Authoritative keyword definitions
- `decompiled/gameplay/fightLog/EntState.java` — Java combat state machine
