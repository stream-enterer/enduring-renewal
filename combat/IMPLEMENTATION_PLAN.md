## Master Implementation Plan

### Completed Systems

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
| `side_injection` | inflictSelfShield, inflictBoned, inflictExert, inflictPain, inflictDeath, inflictSingleUse, inflictNothing, inflictInflictNothing, inflictInflictDeath | 9 |
| `meta_copy_advanced` | share, spy, dejavu, annul, possessed | 5 |
| `meta_copy_buff` | duplicate | 1 |

**Total implemented: 57 keywords**

### Dependency Graph

```
Buff System Tree (depends on buff_system ✅):
    ├─► #1 side_modification (hypnotise)           [1 kw] ✅
    ├─► #2 side_replacement (stasis, enduring...)  [4 kw] ✅
    ├─► #3 side_injection (inflict*)               [9 kw] ✅
    ├─► #4 meta_copy_advanced (share, spy...)      [5 kw] ✅
    ├─► #5 meta_copy_buff (duplicate)              [1 kw] ✅
    └─► #6 group_buff_system (lead)                [1 kw]

Independent Systems (no prerequisites):
    ├─► #7 trait_system (dispel)                   [1 kw, needs trait tracking]
    ├─► #8 spell_tracking (singleCast, cooldown...)  [6 kw, needs spell infra]
    └─► #9 item_system (hoard, fashionable...)     [3 kw, needs item mocking]
```

### Implementation Order

1. **side_modification** (1 keyword: hypnotise)
   - Java: EntState.java:512-514, AffectSides.java
   - Requires: Buff with AffectSides(TypeCondition(DAMAGE), SetValue(0))
   - Dependencies: buff_system (done)
   - Complexity: LOW - Simple buff application, already patterned

2. **side_replacement** (4 keywords: stasis, enduring, dogma, resilient)
   - Java: ReplaceWith.java:109-145, EntSideState.java:106
   - Requires: Hook in side replacement pipeline; stasis blocks all changes, enduring keeps keywords, dogma keeps all but pips, resilient keeps pips
   - Dependencies: buff_system (done)
   - Complexity: MEDIUM - Need ReplaceWith.replaceSide() logic

3. **side_injection** (9 keywords: inflictSelfShield, inflictBoned, inflictExert, inflictPain, inflictDeath, inflictSingleUse, inflictNothing, inflictInflictNothing, inflictInflictDeath)
   - Java: Inflicted.java:1-88, Keyword.java:342-350,678-679
   - Requires: Inflicted trigger class that adds keyword to all target's sides via Buff
   - Dependencies: buff_system (done)
   - Complexity: MEDIUM - Straightforward buff + trigger pattern

4. **meta_copy_advanced** (5 keywords: share, spy, dejavu, annul, possessed)
   - Java: EntState.java:546-552, EntSideState.java:237-248
   - Requires: share/annul use Buff+AffectSides+AddKeyword/RemoveAllKeywords; spy/dejavu already work in meta keyword recursion
   - Dependencies: buff_system (done), meta keyword processing (exists)
   - Complexity: MEDIUM - 4/5 already implemented in Java, need to port

5. **meta_copy_buff** (1 keyword: duplicate)
   - Java: Keyword.java:267-273
   - Requires: Post-effect that applies Buff with AffectSides+AddKeyword to ALL allied sides
   - Dependencies: buff_system (done), allied targeting
   - Complexity: MEDIUM - Similar to share but targets all allies

6. **group_buff_system** (1 keyword: lead)
   - Java: Keyword.java:151-159
   - Requires: Post-effect that gives +N pips to allied sides matching this side's EffType (damage/heal/shield)
   - Dependencies: buff_system (done), effect type comparison, post-effect hooks
   - Complexity: MEDIUM - Multi-target with type filtering

7. **trait_system** (1 keyword: dispel)
   - Java: Trait.java, EntState.java (removeTraits)
   - Requires: Add `traits: list[Personal]` to Entity; TraitsRemoved trigger blocking trait application
   - Dependencies: None (independent system)
   - Complexity: MEDIUM - New infrastructure but isolated

8. **spell_tracking** (6 keywords: singleCast, cooldown, deplete, channel, spellRescue, future)
   - Java: Keyword.java:351-356, Snapshot.java:63,167,278-279 (future ability system)
   - Requires: Per-spell usage tracking (fight/turn level); spell cost modifiers; future ability queue
   - Dependencies: Spell infrastructure (currently minimal)
   - Complexity: HIGH - Needs spell system integration

9. **item_system** (3 keywords: hoard, fashionable, equipped)
   - Java: ConditionalBonusType.java:95-96,123-133
   - Requires: Item mocking in tests; conditional bonus reading unequipped count, equipped count, total tier
   - Dependencies: Item/inventory mocking
   - Complexity: MEDIUM - Infrastructure exists in Java, need test scaffolding

### Permanently Blocked

- **roll_phase** (2 keywords: cantrip, sticky): Requires graphics/rolling UI layer; cantrip activates during dice animation, sticky prevents reroll at UI level - not combat logic
- **tactic_system** (1 keyword: tactical): Requires tactic ability system with dice cost selection UI - game-level feature, not combat calculation
- **validation_only** (1 keyword: permissive): Generator constraint only; allows any keyword on blank sides during item creation - no combat effect
- **item_system/potion** (1 keyword): Requires inventory modification during combat - out of combat-only scope
- **not_implementable** (1 keyword: removed): Deprecated

**Total blocked: 6 keywords**

### Current

<!-- Format: "Next: #N (name)" or "COMPLETE - Only permanently blocked remain" -->

**Next: #6 (group_buff_system)**
- Implement 1 keyword: lead
- Java: Keyword.java:151-159
- Post-effect that gives +N pips to allied sides matching this side's EffType (damage/heal/shield)
- Estimated: 1 keyword unblocked
