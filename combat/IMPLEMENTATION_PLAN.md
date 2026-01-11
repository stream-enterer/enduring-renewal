## Library Completion Roadmap

### Current Phase

**Phase 2: Verification Pass** - Gameplay-verify implemented keywords

### Phase Overview

| Phase | Goal | Keywords | Status |
|-------|------|----------|--------|
| 1. Spell System | Complete keyword coverage | +6 | **Complete** |
| 2. Verification | Gameplay-verify implementations | - | **Current** |
| 3. API Design | Clean public interface | - | Blocked |
| 4. Combat Loop | Full fight simulation | - | Blocked |

---

## Phase 1: Spell System

**Goal:** Implement spell infrastructure to unblock 6 remaining keywords.

### Keywords

| Keyword | Behavior | Complexity |
|---------|----------|------------|
| `singleCast` | Spell can only be cast once per fight | LOW |
| `cooldown` | N turns between casts | LOW |
| `deplete` | Spell removed after casting | MEDIUM |
| `channel` | Effect continues over multiple turns | MEDIUM |
| `spellRescue` | Trigger when spell would be depleted | MEDIUM |
| `future` | Queue ability for future turn | HIGH |

### Infrastructure Required

```python
@dataclass
class SpellState:
    cast_count_this_fight: int = 0
    cast_count_this_turn: int = 0
    cooldown_remaining: int = 0
    is_depleted: bool = False
    channel_turns_remaining: int = 0

# FightLog additions:
spells: dict[Entity, list[SpellState]]
future_queue: list[QueuedAbility]

def cast_spell(self, caster: Entity, spell_index: int, target: Entity): ...
def _process_spell_cooldowns(self): ...  # Called in next_turn()
def _process_future_queue(self): ...     # Called at turn start
```

### Java References

| File | Purpose |
|------|---------|
| `Spell.java` | Core spell class |
| `SpellLib.java` | Spell definitions |
| `SpellUtils.java` | Casting utilities |
| `Keyword.java:351-356` | Spell keyword definitions |
| `Snapshot.java:63,167,278-279` | Future ability system |

### Implementation Steps

- [x] Add Spell infrastructure (study Java, design SpellState class, add to FightLog)
- [x] Implement `singleCast` with tests
- [x] Implement `cooldown` with tests
- [x] Implement `deplete` with tests
- [x] Implement `channel` with tests
- [x] Implement `spellRescue` with tests
- [x] Implement `future` with tests (requires QueuedAbility)
- [x] Update KEYWORDS.json (6/6 keywords moved from blocked → implemented)

---

## Phase 2: Verification Pass

**Goal:** Gameplay-verify implemented keywords to build confidence.

### Categories

| Category | Examples | Priority |
|----------|----------|----------|
| Combined keywords | engine, paxin, engarged, cruesh | HIGH |
| Variant prefixes | anti*, halve*, swap*, group*, minus* | HIGH |
| Death triggers | rampage, rescue, deathwish | HIGH |
| Multi-keyword stacking | Multiple keywords on same side | MEDIUM |
| Parameterized keywords | N from pips | MEDIUM |
| Self-targeting | selfHeal, selfShield, selfPoison | LOW |

### Process

1. Set up scenario in real game
2. Observe behavior
3. Compare to implementation
4. Mark as `verified` in KEYWORDS.json or fix discrepancy

### Deliverable

Move keywords from `implemented` → `verified` in KEYWORDS.json.

---

## Phase 3: API Design

**Goal:** Clean public interface for game integration.

### Deliverables

- [ ] Define public API surface (what consumers can call)
- [ ] Hide internal implementation details
- [ ] Add comprehensive type annotations
- [ ] Write usage examples
- [ ] Document integration patterns

### Considerations

- Immutability guarantees for EntityState
- Error handling patterns
- Event/callback hooks for UI integration
- Serialization support for save/load

---

## Phase 4: Combat Loop

**Goal:** Full fight simulation capability.

### Components

- [ ] Turn ordering / initiative system
- [ ] Monster AI (action selection)
- [ ] Win/lose condition detection
- [ ] Multi-round state management
- [ ] Fight result reporting

### Enables

- Automated regression testing against real scenarios
- Monte Carlo simulation for balance analysis
- Full game integration

---

## Permanently Blocked

These keywords will never be implemented in the combat library:

| Category | Keywords | Reason |
|----------|----------|--------|
| `roll_phase` | cantrip, sticky | UI layer (dice animation/reroll) |
| `tactic_system` | tactical | UI layer (dice cost selection) |
| `validation_only` | permissive | Generation constraint only |
| `potion` | potion | Inventory modification (scope violation) |
| `deprecated` | removed | No longer in game |

**Total: 6 keywords**

---

## Historical: Keyword Phase (Complete)

The keyword implementation phase completed with 179 keywords implemented across 16 infrastructure systems.

### Systems Implemented

| System | Keywords | Count |
|--------|----------|-------|
| `status_effect_system` | poison, regen, cleanse, selfPoison, selfRegen, selfCleanse, plague, acidic | 8 |
| `buff_system` | weaken, boost, vulnerable, smith, permaBoost, selfVulnerable, buffed, affected, skill | 9 |
| `usage_tracking` | doubleUse, quadUse, hyperUse, rite, trill | 5 |
| `turn_tracking` | patient, era, minusEra | 3 |
| `target_tracking` | focus, duel, halveDuel, duegue, underocus | 5 |
| `effect_types` | heal, shield, damage | 3 |
| `max_hp_modification` | vitality, wither | 2 |
| `entity_summoning` | boned, hyperBoned | 2 |
| `side_modification` | hypnotise | 1 |
| `side_replacement` | stasis, enduring, dogma, resilient | 4 |
| `side_injection` | inflict* (9 keywords) | 9 |
| `meta_copy_advanced` | share, spy, dejavu, annul, possessed | 5 |
| `meta_copy_buff` | duplicate | 1 |
| `group_buff_system` | lead | 1 |
| `trait_system` | dispel | 1 |
| `item_system` | hoard, fashionable, equipped | 3 |

Plus ~117 keywords implemented as conditionals, meta keywords, post-effects, and turn-timing effects.

**Final state: 179 keywords implemented, 549 tests passing, ~20k lines of Python**
