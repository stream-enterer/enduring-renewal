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

`combat/KEYWORDS.json` tracks:
- `verified`: keywords confirmed correct via human gameplay testing (subset of implemented)
- `implemented`: keywords with passing tests
- `blocked`: keywords grouped by missing infrastructure (maps reason → keyword list)
- `all`: complete enum (184 keywords) - **must match Java Keyword.java enum**

```
remaining = all - implemented - blocked
verified ⊆ implemented (verified is always a subset)
```

## Workflow

When asked to "continue":

1. **Read `combat/IMPLEMENTATION_PLAN.md`** - Check "Current" section for next task
2. **Implement** using the process below
3. **Update state** and commit

### Implementation Process

1. **Read "Current"** in `IMPLEMENTATION_PLAN.md`
2. **Verify not already done** - Check if keywords already in KEYWORDS.json `implemented`
   - If done: Update "Current" to next system, then re-read
3. **Study Java** using strategic research (3 steps max):
   - Read keyword's Keyword.java entry (identifies type: conditional/buff/trigger)
   - Grep keyword name in `decompiled/` (finds implementation files)
   - Read implementation (usually EntState.java case statement + trigger classes)
4. **Implement** - batch edits by file:
   - List all changes needed per file before editing
   - All changes to same file in single Edit call
5. **Run tests** - `cd combat && uv run pytest` (all must pass)
6. **Update state** (only after tests pass):
   - `KEYWORDS.json`: Move keywords from `blocked` → `implemented`
   - `IMPLEMENTATION_PLAN.md`: Add to "Completed Systems", update "Current" to next
7. **Commit**: `Implement <system> infrastructure with <x>, <y>, <z> keywords`

**If blocked:** Pick different system, note dependency in plan
**If complex:** Implement incrementally, update state as each piece lands
**If unclear:** Ask user about Java behavior
**If all done:** "Current" should say `**COMPLETE** - Only permanently blocked keywords remain`

## Blocked Infrastructure

See `combat/IMPLEMENTATION_PLAN.md` for:
- Dependency graph and numbered implementation order
- Per-system Java file references and complexity ratings
- Permanently blocked keywords (6 total: cantrip, sticky, tactical, permissive, potion, removed)

## Infrastructure Implementation Patterns

**Note:** These patterns are starting points, not specs. Always study the Java source first - actual implementation may differ.

### Pattern 1: Turn-Based Processing

For systems like `status_effect_system`, `turn_start_processing`:

```python
# In FightLog, extend next_turn() or add hooks:
def next_turn(self):
    self._process_turn_start_effects()  # shifter, lucky, critical
    # ... existing turn logic ...
    self._process_turn_end_effects()    # poison, regen

def _process_turn_end_effects(self):
    for entity in self.all_entities:
        state = self.get_state(entity, Temporality.PRESENT)
        for status in state.statuses:
            status.apply_end_of_turn(self, entity)
```

### Pattern 2: Status/Buff Tracking

For `buff_system`, `status_effect_system`:

```python
# In EntityState, add status tracking:
@dataclass
class EntityState:
    # ... existing fields ...
    statuses: dict[StatusType, int] = field(default_factory=dict)
    buffs: dict[BuffType, int] = field(default_factory=dict)

    def has_status(self, status_type: StatusType) -> bool:
        return self.statuses.get(status_type, 0) > 0

    def add_status(self, status_type: StatusType, stacks: int = 1):
        self.statuses[status_type] = self.statuses.get(status_type, 0) + stacks
```

### Pattern 3: Usage Tracking

For `usage_tracking`:

```python
# Track uses per die per turn/combat
@dataclass
class DieState:
    uses_this_turn: int = 0
    uses_this_combat: int = 0

# In use_die():
def use_die(self, source: Entity, side_index: int, target: Entity):
    die_state = self.get_die_state(source)
    if side.has_keyword(Keyword.DOUBLE_USE):
        if die_state.uses_this_turn >= 2:
            return  # Already used twice
    # ... normal use logic ...
    die_state.uses_this_turn += 1
```

### Pattern 4: Target Tracking

For `target_tracking`:

```python
# In FightLog, track targeting history
@dataclass
class FightLog:
    last_target: dict[Entity, Entity] = field(default_factory=dict)
    duel_target: dict[Entity, Entity] = field(default_factory=dict)

    def record_target(self, source: Entity, target: Entity):
        self.last_target[source] = target
```

## Implementation Patterns (Keywords)

These patterns are for individual keywords once infrastructure exists.

### Conditional Keywords

Add to `FightLog._apply_conditional_keyword_bonuses()`:

```python
def _apply_conditional_keyword_bonuses(
    self, value: int, side: "Side", source_state: EntityState,
    target_state: EntityState, source_entity: Entity, target_entity: Entity
) -> int:
```

**Variables available:**
- `value` - current damage/heal value (multiply this)
- `side` - the Side being used (check `side.has_keyword(...)`)
- `source_state` / `target_state` - EntityState objects
- `source_entity` / `target_entity` - Entity objects

### Meta Keywords

Add to `EntityState._process_meta_keywords()`:

```python
def _process_meta_keywords(self, side_state: "SideState", fight_log: "FightLog" = None):
```

These modify the Side itself before value calculation.

### Post-Effect Keywords

Add to `FightLog.use_die()` after main effect:

```python
# After effect applied
original_side = die.get_side(side_index)
if original_side.has_keyword(Keyword.GROWTH):
    original_side.apply_growth()
```

## Test Patterns

**Note:** Infrastructure test examples show APIs to design, not existing code. Design the API, then implement it.

### Infrastructure Tests

Test the system, not just individual keywords:

```python
class TestStatusEffectSystem:
    """Test status effect infrastructure."""

    def test_poison_stacks(self):
        """Poison stacks should accumulate."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        fight.apply_status(hero, StatusType.POISON, 2)
        fight.apply_status(hero, StatusType.POISON, 1)

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.get_status_stacks(StatusType.POISON) == 3

    def test_poison_damages_on_turn_end(self):
        """Poison deals damage equal to stacks at turn end."""
        hero = make_hero("Fighter", hp=10)
        fight = FightLog([hero], [])

        fight.apply_status(hero, StatusType.POISON, 3)
        fight.next_turn()

        state = fight.get_state(hero, Temporality.PRESENT)
        assert state.hp == 7  # 10 - 3 poison
```

### Keyword Tests

Test the keyword using the infrastructure:

```python
class TestPoison:
    """Test poison keyword."""

    def test_poison_applies_stacks(self):
        """Poison side applies poison stacks to target."""
        hero = make_hero("Fighter", hp=5)
        monster = make_monster("Goblin", hp=10)
        fight = FightLog([hero], [monster])

        hero.die = Die()
        poison_side = Side(EffectType.STATUS, 2, {Keyword.POISON})
        hero.die.set_all_sides(poison_side)

        fight.use_die(hero, 0, monster)

        state = fight.get_state(monster, Temporality.PRESENT)
        assert state.get_status_stacks(StatusType.POISON) == 2
```

### Helpers

```python
def make_hero(name: str, hp: int = 5) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)

def make_monster(name: str, hp: int = 4) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)
```

## Keyword Pipeline

Keywords run at different stages:

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
| Meta | Before value calc | copycat, pair, echo | Transforms the Side |
| Conditional | Value calculation | engage, pristine, bloodlust | Multiplies (x2) or adds (+N) |
| Post-effect | After resolution | growth, singleUse, manaGain | Modifies die, grants mana |
| Turn-timing | Turn start/end | poison, regen, shifter, fluctuate | Per-turn effects |

## Quick Reference

**Variant prefixes:** `anti*` (invert), `halve*` (x0.5), `swap*` (source↔target), `group*` (all allies), `minus*` (negate)

**Parameter (N):** From Side's pip value. Exceptions: `century`=100, `terminal`=1, `sixth`=6th die

## Project Structure

```
decompiled/           # READ-ONLY Java source (reference)
combat/
├── KEYWORDS.json     # State tracking (implemented, blocked, all)
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
| `Poison.java`, `Buff.java`, etc. | Status/buff implementations |

**How to read Keyword.java:**
```java
// Format: name(color, "rules text", ConditionType, isSourceCheck)
engage(Colours.yellow, "with full hp", StateConditionType.FullHP, false),
//                                                                ^^^^^ false = check TARGET
pristine(Colours.light, "have full hp", StateConditionType.FullHP, true),
//                                                                 ^^^^ true = check SOURCE
```

## Funnel Sieve Verification

After ALL keywords implemented, run gameplay tests:

**Layer 1: Combined/Variant keywords**
- Combined: engine, paxin, engarged, cruesh
- Variants: antiEngage, halveEngage, swapCruel, groupGrowth

**Layer 2: Edge Cases**
- Multiple keywords on same side (stacking)
- Parameterized keywords (N from pips)
- Self-targeting keywords (selfHeal, selfShield)
- Death triggers (rampage, rescue, deathwish)

## Deferred Tests

These tests need additional infrastructure beyond keyword implementation:

- `TestTriggerOrdering.testTriggerHPOrdering` - needs ModifierLib
- `TestTriggerOrdering.testCreakyJointsSword` - needs item_system
- `TestStates.test2hpStates` - needs Save/Load
