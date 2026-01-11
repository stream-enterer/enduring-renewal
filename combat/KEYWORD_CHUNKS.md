# Keyword Implementation Chunks

Pre-analyzed groups of remaining keywords, ordered by implementation complexity.
Pick a chunk and implement it. Each chunk is ~3-8 keywords with similar patterns.

**Current stats:** 95 implemented, ~23 blocked, ~70 remaining

---

## CHUNK 1: Conditional Bonus - defy (Easy, 1 keyword)
**Location:** `_apply_conditional_keyword_bonuses()` in fight.py
**Pattern:** +N pip bonus based on state

```
defy: +N where N = incoming damage to source
```

**Implementation:**
- Requires `get_incoming_damage()` on EntityState (check if exists)
- Java: `ConditionalBonusType.IncomingDamage` returns `sourceState.getIncomingDamage()`

---

## CHUNK 2: Targeting Restrictions (Medium, 5 keywords)
**Location:** New targeting validation in FightLog or use_die
**Pattern:** Restrict valid targets based on conditions

```
eliminate: can only target enemies that would die from this attack
heavy: can only target enemies with 5+ HP
generous: cannot target myself
scared: target must have N or less HP (N = pips)
picky: target must have exactly N HP (N = pips)
```

**Java reference:** `TargetingRestriction` enum in Keyword.java
**Implementation:** Add `_validate_target()` method or check in `use_die()`

---

## CHUNK 3: Self-Targeting Variants (Easy, 6 keywords)
**Location:** `use_die()` post-processing in fight.py
**Pattern:** Apply base effect to self instead of target

```
selfPoison: apply poison N to self (needs poison system)
selfRegen: apply regen N to self (needs regen system)
selfPetrify: petrify self (petrify exists!)
selfCleanse: cleanse self
selfVulnerable: apply vulnerable to self
selfRepel: apply repel N to self
```

**Note:** selfShield and selfHeal already implemented. selfPetrify can be done now.
Most others need buff/debuff system first.

---

## CHUNK 4: Effect Modifiers - Multi-target (Medium, 4 keywords)
**Location:** `use_die()` effect application in fight.py
**Pattern:** Modify how/where effect is applied

```
cleave: also hits adjacent enemies (above and below target)
descend: also hits below the target
duplicate: effect happens twice
repel: N damage to all enemies attacking the target
```

**Java reference:** Check `cleave` handling in FightLog.java

---

## CHUNK 5: Value Visibility Modifiers (Medium, 9 keywords)
**Location:** New `_get_visible_value()` or in `_process_meta_keywords()`
**Pattern:** Other keywords see modified pip values

```
fault: others see -1 pips
plus: others see N+1 pips
doubled: others see 2*N pips
squared: others see N^2 pips
onesie: others see 1 pip
threesy: others see 3 pips
zeroed: others see 0 pips
revDiff: inverted pip delta (for pair/chain checks)
doubDiff: doubled pip delta
```

**Note:** These affect how PAIR, CHAIN, ECHO etc. see this side's value.
Need to understand "others seeing" mechanism from Java.

---

## CHUNK 6: Side/Die Behavior Modifiers (Medium, 8 keywords)
**Location:** Various - roll phase, side replacement, usage
**Pattern:** Modify how die/side behaves

```
stasis: this side cannot change (blocks growth, petrify, etc.)
cantrip: can be used even when die is "used"
sticky: cannot be rerolled
enduring: keywords remain when side is replaced
dogma: only pips change when side is replaced
resilient: pips remain when side is replaced
permissive: any keyword can be added to this
tactical: counts twice for tactic costs
```

**Implementation:** Add checks in relevant modification methods

---

## CHUNK 7: Usage/Cost Keywords (Medium, 3 keywords)
**Location:** `use_die()` pre-check or post-processing
**Pattern:** Modify usage requirements or costs

```
manacost: costs N mana to use (N = pips)
mandatory: must be used if possible
fierce: target flees if they have N or less HP after attack
```

---

## CHUNK 8: Turn-Based Random Keywords (Hard, 5 keywords)
**Location:** Turn start processing (need turn hooks)
**Pattern:** Re-randomize each turn

```
shifter: random extra keyword, changes each turn
lucky: pips randomized 0 to N each turn
critical: 50% chance +1 pip each turn
fluctuate: changes to random side each turn (keeps keywords/pips)
fumble: 50% chance to be blank each turn
```

**Requires:** Turn-start hook infrastructure

---

## CHUNK 9: Meta Keywords - Copy/Share (Hard, 5 keywords)
**Location:** `_process_meta_keywords()` in fight.py
**Pattern:** Copy or share keywords/effects

```
share: targets gain all my keywords this turn (except share)
spy: copy all keywords from first enemy attack
dejavu: copy keywords from sides I used last turn
annul: targets lose all keywords this turn
possessed: targets as if used by the other side
```

**Note:** echo, resonate, copycat already done. These need more state tracking.

---

## CHUNK 10: Buff/Debuff System (Hard, 12 keywords)
**Location:** New buff/debuff system needed
**Pattern:** Apply temporary status effects

```
poison: N damage at end of turn
regen: heal N at end of turn
weaken: target deals -N damage this turn
boost: target deals +N damage this turn
vulnerable: target takes +N damage this turn
smith: target gains +N to shield values this turn
permaBoost: target gets +N to all pips this fight
hypnotise: set target's damage sides to 0 this turn
dispel: remove all traits from target this fight
vitality: grants +N empty HP this fight
wither: grants -N empty HP this fight
cleanse: remove debuffs
```

**Requires:** Full buff/debuff tracking system with turn processing

---

## CHUNK 11: Spell Management (Hard, 6 keywords)
**Location:** Spell/mana system
**Pattern:** Modify spell casting behavior

```
singleCast: can only cast once per fight
cooldown: can only cast once per turn
deplete: costs +1 mana each cast
channel: costs -1 mana each cast (min 1)
spellRescue: mana refunded if saves a hero
future: effect delayed until start of next turn
```

**Requires:** Spell tracking infrastructure

---

## CHUNK 12: Inflict Keywords (Hard, 9 keywords)
**Location:** New side injection system
**Pattern:** Add keywords to target's dice

```
inflictSelfShield, inflictBoned, inflictExert, inflictPain,
inflictDeath, inflictSingleUse, inflictNothing,
inflictInflictNothing, inflictInflictDeath
```

**Pattern:** `inflictX` adds keyword X to target's die sides
**Requires:** Side modification system for enemies

---

## CHUNK 13: Misc/Special (Easy, 2 keywords)
```
potion: discard topmost 'potion' item (needs item system)
removed: special marker, shouldn't appear in gameplay
```

---

## Recommended Order

1. **CHUNK 1** (defy) - Single keyword, familiar pattern
2. **CHUNK 3** (selfPetrify only) - Uses existing petrify
3. **CHUNK 2** (targeting) - New but straightforward system
4. **CHUNK 4** (cleave/descend) - Builds on existing effect application
5. **CHUNK 7** (manacost/mandatory/fierce) - Small, varied
6. **CHUNK 6** (stasis/sticky/etc) - Side behavior flags
7. **CHUNK 5** (value visibility) - Needs Java research
8. **CHUNK 8** (turn-based) - Needs turn hooks
9. **CHUNK 9** (meta copy/share) - Needs state tracking
10. **CHUNK 10** (buffs) - Major new system
11. **CHUNK 11** (spells) - Needs spell infrastructure
12. **CHUNK 12** (inflict) - Needs side injection

---

## Quick Reference: Java Files

- `Keyword.java` - Enum definition, rules text, condition types
- `FightLog.java` - Combat state, die resolution
- `EntState.java` / `EntSideState.java` - Entity/side state
- `conditionalBonus/*.java` - How keywords modify values
- `TargetingRestriction.java` - Target validation
