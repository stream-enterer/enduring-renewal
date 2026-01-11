# Unlocked Prompts for Keyword Implementation

Copy-paste any of these into a new conversation.

---

## Generic Continue

```
Continue implementing keywords. Read combat/KEYWORDS.json to see what's done, then pick the next unimplemented keyword from this priority list:

1. defy (conditional +N bonus)
2. selfPetrify (self-targeting)
3. targeting keywords: eliminate, heavy, generous, scared, picky
4. effect modifiers: cleave, descend, duplicate, repel
5. usage keywords: manacost, mandatory, fierce

For each: add enum to dice.py, write test, implement, run pytest, update KEYWORDS.json, commit.
```

---

## Chunk 1: defy

```
Implement the `defy` keyword.

Location: _apply_conditional_keyword_bonuses() in combat/src/fight.py
Pattern: +N pips where N = incoming damage to source
Java ref: ConditionalBonusType.IncomingDamage returns sourceState.getIncomingDamage()

1. Add DEFY = auto() to Keyword enum in dice.py
2. Write test in test_keyword.py
3. Implement in _apply_conditional_keyword_bonuses()
4. Run pytest, update KEYWORDS.json, commit
```

---

## Chunk 2: Targeting Keywords

```
Implement targeting restriction keywords: eliminate, heavy, generous, scared, picky

These restrict valid targets:
- eliminate: can only target enemies that would die from this attack
- heavy: can only target enemies with 5+ HP
- generous: cannot target myself
- scared: target must have N or less HP (N = pips)
- picky: target must have exactly N HP (N = pips)

Location: Add _validate_target() method to FightLog or check in use_die()
Java ref: TargetingRestriction enum in Keyword.java

Add enums, write tests, implement, run pytest, update KEYWORDS.json, commit.
```

---

## Chunk 3: selfPetrify

```
Implement the `selfPetrify` keyword.

Pattern: Apply petrify to self (petrify system already exists)
Location: use_die() post-processing in combat/src/fight.py
Reference: Look at how selfShield and selfHeal are implemented

1. Add SELF_PETRIFY = auto() to Keyword enum in dice.py
2. Write test in test_keyword.py
3. Implement in use_die() - apply petrify to source entity
4. Run pytest, update KEYWORDS.json, commit
```

---

## Chunk 4: cleave/descend

```
Implement multi-target effect keywords: cleave, descend, duplicate, repel

- cleave: also hits adjacent enemies (above and below target)
- descend: also hits below the target
- duplicate: effect happens twice
- repel: N damage to all enemies attacking the target

Location: Modify effect application in use_die() in combat/src/fight.py
Java ref: Check cleave handling in FightLog.java

Add enums, write tests, implement, run pytest, update KEYWORDS.json, commit.
```

---

## Chunk 5: Usage Keywords

```
Implement usage/cost keywords: manacost, mandatory, fierce

- manacost: costs N mana to use (N = pips)
- mandatory: must be used if possible
- fierce: target flees if they have N or less HP after attack

Location: use_die() pre-check or post-processing in combat/src/fight.py

Add enums, write tests, implement, run pytest, update KEYWORDS.json, commit.
```

---

## Chunk 6: Side Behavior Keywords

```
Implement side/die behavior keywords: stasis, cantrip, sticky, permissive

- stasis: this side cannot change (blocks growth, petrify, etc.)
- cantrip: can be used even when die is "used"
- sticky: cannot be rerolled
- permissive: any keyword can be added to this

Location: Add checks in relevant modification methods in fight.py and dice.py

Add enums, write tests, implement, run pytest, update KEYWORDS.json, commit.
```

---

## Chunk 7: Value Visibility Keywords

```
Implement value visibility keywords: fault, plus, doubled, squared, onesie, threesy, zeroed

These modify how OTHER keywords (pair, chain, echo) see this side's pip value:
- fault: others see -1
- plus: others see N+1
- doubled: others see 2*N
- squared: others see N^2
- onesie: others see 1
- threesy: others see 3
- zeroed: others see 0

First research Java "describeOthersSeeingNPips" mechanism, then implement.
Location: Likely _process_meta_keywords() or new _get_visible_value() method

Add enums, write tests, implement, run pytest, update KEYWORDS.json, commit.
```
