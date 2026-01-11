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
- `blocked`: keywords skipped due to missing infrastructure (maps reason → keyword list)
- `all`: complete enum (191 keywords) - **must match Java Keyword.java enum**

```
remaining = all - implemented - blocked
verified ⊆ implemented (verified is always a subset)
```

**Two-tier verification:**
1. **Implemented** = automated tests pass (required for progress)
2. **Verified** = human confirmed in-game behavior matches (batch at end)

Agent implements all keywords → tests pass → added to `implemented`.
After ALL keywords implemented → human runs funnel sieve verification (see below).

**Safety invariant:** Every keyword in `all` is either implemented, blocked, or remaining. Nothing is lost.

## On "continue"

**Token efficiency:** Do NOT read fight.py, dice.py, or test_keyword.py in full. Patterns below are sufficient. Only grep/read specific sections if debugging.

1. Read `combat/KEYWORDS.json`, compute remaining keywords
2. Pick next keyword using **soft priority**:
   - **First**: Base keywords before variants (implement `engage` before `antiEngage`)
   - **Second**: Simple conditionals (x2 multipliers) before complex (combined, parameterized)
   - **Third**: Keywords whose infrastructure exists before those that need new systems
   - **Flexible**: Can deviate if related keywords make sense to batch
3. For each keyword:
   - **Read Java source first** - `Keyword.java` and `conditionalBonus/` are authoritative. Patterns in this doc are examples, not specs.
   - Add enum value to `src/dice.py` (SCREAMING_SNAKE_CASE: `antiEngage` → `ANTI_ENGAGE`)
     ```python
     # In dice.py, add to class Keyword(Enum): around line 10
     ANTI_ENGAGE = auto()  # x2 if target NOT at full HP
     ```
   - Write failing test in `tests/test_keyword.py`
   - Implement in appropriate location (see Pipeline section)
   - Run `uv run pytest` - must pass
   - Add to `implemented` array in `KEYWORDS.json`
4. Commit: `Implement <keyword> keyword` or `Implement <x>, <y>, <z> keywords`
5. Report: keywords implemented, keywords skipped (and why), remaining count

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

## Implementation Patterns

### Conditional Keywords (most common)

Add to `FightLog._apply_conditional_keyword_bonuses()` at line ~1267 in fight.py.

**Method signature (available variables):**
```python
def _apply_conditional_keyword_bonuses(
    self, value: int, side: "Side", source_state: EntityState,
    target_state: EntityState, source_entity: Entity, target_entity: Entity
) -> int:
```

**Variables you can use:**
- `value` - current damage/heal value (multiply this)
- `side` - the Side being used (check `side.has_keyword(...)`)
- `source_state` - EntityState of attacker (hp, max_hp, shield, is_dead, etc.)
- `target_state` - EntityState of target
- `source_entity` / `target_entity` - Entity objects (for identity checks)

**Pattern:**
```python
# In fight.py:_apply_conditional_keyword_bonuses()
# Add after existing keywords, before "return value"

if side.has_keyword(Keyword.ENGAGE):
    if target_state.hp == target_state.max_hp:
        value *= 2

if side.has_keyword(Keyword.CRUEL):
    if target_state.hp <= target_state.max_hp // 2:
        value *= 2

# Add new conditional keywords here following this pattern
```

### Meta Keywords

Add to `EntityState._process_meta_keywords()` - these modify the Side before value calculation.

### Post-Effect Keywords

Add to `FightLog.use_die()` after the main effect is applied.

## Test Patterns

Helper functions (defined at top of each test file):

```python
def make_hero(name: str, hp: int = 5) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.HERO)

def make_monster(name: str, hp: int = 4) -> Entity:
    return Entity(EntityType(name, hp, EntitySize.HERO), Team.MONSTER)
```

**Note:** These are duplicated per test file (minor DRY violation, acceptable for test isolation).

**Where to add tests:** Add a new `class Test<Keyword>:` to `tests/test_keyword.py` (one class per keyword).

**Common FightLog setup methods:**
- `fight.apply_damage(source, target, amount, is_pending=False)` - deal damage
- `fight.apply_shield(target, amount)` - grant shields
- `fight.get_state(entity, Temporality.PRESENT)` - get current EntityState

### Pattern 1: Full Integration Test (preferred)

```python
def test_pristine_full_hp(self):
    """Pristine deals x2 damage when source is at full HP."""
    hero = make_hero("Fighter", hp=5)
    monster = make_monster("Goblin", hp=10)
    fight = FightLog([hero], [monster])

    # Setup: Die with pristine keyword
    hero.die = Die()
    pristine_side = Side(EffectType.DAMAGE, 2, {Keyword.PRISTINE})
    hero.die.set_all_sides(pristine_side)

    # Act: Use the die
    fight.use_die(hero, 0, monster)

    # Assert: x2 damage (2 * 2 = 4)
    state = fight.get_state(monster, Temporality.PRESENT)
    assert state.hp == 6
```

### Pattern 2: Condition Not Met

```python
def test_pristine_no_bonus_when_damaged(self):
    """Pristine deals normal damage when source is below full HP."""
    hero = make_hero("Fighter", hp=5)
    monster = make_monster("Goblin", hp=10)
    fight = FightLog([hero], [monster])

    # Damage the hero first (no longer at full HP)
    fight.apply_damage(monster, hero, 1, is_pending=False)

    hero.die = Die()
    pristine_side = Side(EffectType.DAMAGE, 2, {Keyword.PRISTINE})
    hero.die.set_all_sides(pristine_side)

    fight.use_die(hero, 0, monster)
    # No x2, just base damage
    state = fight.get_state(monster, Temporality.PRESENT)
    assert state.hp == 8  # 10 - 2 = 8
```

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

**If infrastructure is missing:**
1. Add keyword to `blocked` in KEYWORDS.json under the infrastructure reason:
   ```json
   "blocked": {
     "turn_end_processing": ["poison", "regen"],
     "buff_system": ["weaken", "boost"]
   }
   ```
2. Continue with other keywords that don't need the missing infrastructure
3. In final report, mention blocked keywords and why

## Variant Keywords

Variants modify base keywords. **Invert/modify the condition inline:**

| Prefix | What It Does | Example |
|--------|--------------|---------|
| `anti*` | Inverts condition | antiEngage = x2 if target NOT full HP |
| `halve*` | x0.5 instead of x2 | halveEngage = x0.5 if target full HP |
| `swap*` | Swaps source/target | swapEngage = x2 if SOURCE full HP |
| `group*` | Applies to all allies | groupGrowth = all allies get growth |
| `minus*` | Inverts bonus (+N → -N) | minusFlesh = -1 per HP I have |

```python
# antiEngage → ANTI_ENGAGE: invert the condition
if side.has_keyword(Keyword.ANTI_ENGAGE):
    if target_state.hp != target_state.max_hp:  # NOT full HP
        value *= 2

# halveEngage → HALVE_ENGAGE: same condition, different multiplier
if side.has_keyword(Keyword.HALVE_ENGAGE):
    if target_state.hp == target_state.max_hp:
        value //= 2  # integer division

# swapEngage → SWAP_ENGAGE: check SOURCE instead of TARGET
if side.has_keyword(Keyword.SWAP_ENGAGE):
    if source_state.hp == source_state.max_hp:  # SOURCE full HP
        value *= 2
```

**Combined keywords** (engarged, engine, paxin):

```python
# engine → ENGINE: engage AND pristine → x4
if side.has_keyword(Keyword.ENGINE):
    engage_met = target_state.hp == target_state.max_hp
    pristine_met = source_state.hp == source_state.max_hp
    if engage_met and pristine_met:
        value *= 4
```

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

## Funnel Sieve Verification

After ALL keywords are implemented, run minimal gameplay tests that maximize coverage:

**Layer 1: Combined Keywords** (test multiple base keywords at once)
| Test | Implicitly Verifies |
|------|---------------------|
| `engine` | engage, pristine, x4 stacking |
| `paxin` | pair, chain, XOR logic |
| `engarged` | engage, charged, condition+bonus |
| `cruesh` | cruel, flesh, HP-based bonuses |

**Layer 2: Variant Keywords** (test modification system)
| Test | Implicitly Verifies |
|------|---------------------|
| `antiEngage` | anti* prefix, condition inversion |
| `halveEngage` | halve* prefix, x0.5 multiplier |
| `swapCruel` | swap* prefix, source/target swap |
| `groupGrowth` | group* prefix, multi-target effects |

**Layer 3: Infrastructure Keywords** (test new systems)
| Test | Implicitly Verifies |
|------|---------------------|
| `poison` | turn-end processing, DoT |
| `weaken` | buff/debuff system |
| `boned` | entity summoning |
| `inflictPain` | side injection |

**Layer 4: Edge Cases** (test interactions)
- Multiple keywords on same side (stacking)
- Parameterized keywords (N from pips)
- Self-targeting keywords (selfHeal, selfShield)
- Death triggers (rampage, rescue, deathwish)

**Process:**
1. If Layer 1 passes → base keywords likely correct
2. If Layer 2 passes → variant system works
3. If Layer 3 passes → infrastructure works
4. If Layer 4 passes → edge cases handled

Failures at any layer indicate which system needs investigation. Fix and re-test that layer before proceeding.

## Deferred (need infrastructure)

- `TestTriggerOrdering.testTriggerHPOrdering` - needs ModifierLib
- `TestTriggerOrdering.testCreakyJointsSword` - needs Items
- `TestStates.test2hpStates` - needs Save/Load
