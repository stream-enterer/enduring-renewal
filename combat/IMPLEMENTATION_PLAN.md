## Master Implementation Plan

### Current: **COMPLETE** - Only blocked keywords remain

---

### Completed Systems

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

### Out of Scope (Future Work)

| System | Keywords | Dependency |
|--------|----------|------------|
| `tactic_system` | tactical | Tactic cost calculation, dice selection UI |

### Permanently Blocked

| Keyword | Reason |
|---------|--------|
| `permissive` | Generator constraint - no runtime effect |
| `potion` | Inventory system - out of combat scope |
| `removed` | Deprecated in source game |

**Total: 3 permanently blocked + 1 out of scope = 4 keywords**
