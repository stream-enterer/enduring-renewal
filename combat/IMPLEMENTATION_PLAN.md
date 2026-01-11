## Master Implementation Plan

### Current: **COMPLETE** - Only permanently blocked keywords remain

All implementable keywords have been completed. The following keywords are permanently blocked:
- `permissive` - Generator constraint, no runtime effect
- `potion` - Inventory system, out of combat scope
- `removed` - Deprecated in source game

---

### Completed Systems

#### tactic_system (1 keyword: tactical)

**Implementation:**

1. **TacticCostType enum** (in `tactic.py`):
   - BASIC_SWORD, BASIC_SHIELD, BASIC_HEAL, BASIC_MANA, WILD (pippy types)
   - BLANK, PIPS_1-4, KEYWORD, TWO_KEYWORDS, FOUR_KEYWORDS (non-pippy)
   - `is_valid(side)` - check if side matches cost type
   - `get_valid_types(side)` - returns all matching cost types

2. **TacticCost class**:
   - `is_usable(fight_log)` - check if rolled, unused hero dice satisfy all costs
   - `get_contributing_entities(fight_log)` - return entities whose dice would be consumed
   - `_calculate_fulfillment()` - pippy types accumulate by pip value, tactical keyword doubles

3. **Tactic class**: name, cost (TacticCost), effect (SpellEffect)

4. **FightLog methods**:
   - `is_tactic_usable(tactic)` - check if tactic can be used
   - `use_tactic(tactic, target)` - consume dice and apply effect

5. **TACTICAL keyword**: doubles the contribution amount for tactic costs

---

#### roll_phase (2 keywords: sticky, cantrip)

**Implementation:**

1. **sticky** - `is_auto_lock()` method in FightLog checks if current side has STICKY keyword. Returns true to block rerolling.

2. **cantrip** - `activate_cantrip()` method in FightLog:
   - Auto-activates when die lands on side with CANTRIP keyword
   - Die is NOT consumed (can still be used normally)
   - Randomly selects target from valid targets (damage→enemies, heal/shield→allies, mana→self)
   - Excluded effect types: BLANK (Resurrect/Summon not yet implemented)
   - Skip if entity is dead or no valid targets

---

### Permanently Blocked

| Keyword | Reason |
|---------|--------|
| `permissive` | Generator constraint - no runtime effect |
| `potion` | Inventory system - out of combat scope |
| `removed` | Deprecated in source game |

**Total: 3 permanently blocked keywords**
