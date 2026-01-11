# Next Steps: Infrastructure Implementation Phase

All unblocked keywords complete. 112 implemented, ~72 blocked across 22 infrastructure categories.

## Phase 1: Layers 1-2 Verification (Optional)

Can verify now without infrastructure:

- **Layer 1 (Combined):** engine, paxin, engarged, cruesh
- **Layer 2 (Variants):** antiEngage, halveEngage, swapCruel, groupGrowth

These test base + variant systems already implemented.

## Phase 2: Infrastructure Implementation

### Recommended Order

Based on: keyword count, dependency chains, implementation complexity.

| Order | System | Keywords | Unlocks | Complexity |
|-------|--------|----------|---------|------------|
| 1 | `buff_system` | weaken, boost, vulnerable, smith, permaBoost, selfVulnerable, duplicate, buffed, affected, skill (10) | skill → trill | Medium |
| 2 | `status_effect_system` | poison, regen, cleanse, selfPoison, selfRegen, selfCleanse, plague, acidic (8) | - | Medium |
| 3 | `usage_tracking` | doubleUse, quadUse, hyperUse, rite (4) | - | Low |
| 4 | `turn_start_processing` | shifter, lucky, critical, fluctuate, fumble (5) | - | Medium |
| 5 | `target_tracking` | focus, duel (2) | duel → halveDuel, duegue | Low |
| 6 | `turn_tracking` | patient, era, minusEra (3) | era → minusEra | Low |
| 7 | `spell_tracking` | singleCast, cooldown, deplete, channel, spellRescue, future (6) | - | Medium |
| 8 | `side_replacement` | stasis, enduring, dogma, resilient (4) | - | Medium |
| 9 | `meta_copy_advanced` | share, spy, dejavu, annul, possessed (5) | - | High |
| 10+ | Remaining systems | See CLAUDE.md Blocked Infrastructure Catalog | - | Varies |

### Dependency Chains

```
buff_system.skill → trill
target_tracking.duel → halveDuel, duegue
target_tracking.focus → underocus
```

### Per-System Workflow

For each system:

1. **Study Java** - Find relevant classes (Poison.java, Buff.java, etc.)
2. **Design Python** - Minimal implementation matching Java behavior
3. **Test infrastructure** - Test system mechanics independent of keywords
4. **Implement keywords** - Standard keyword workflow
5. **Update tracking** - Move from `blocked` to `implemented` in KEYWORDS.json
6. **Commit** - `Implement <system> infrastructure with <keywords>`

## Phase 3: Layers 3-4 Verification

After infrastructure complete:

- **Layer 3 (Infrastructure):** poison, weaken, boned, inflictPain
- **Layer 4 (Edge cases):** stacking, parameterized, self-targeting, death triggers

## Phase 4: Other Systems

After keywords complete:

| System | Source Files | Notes |
|--------|--------------|-------|
| Items | Item.java, Equipment.java | hoard, fashionable, equipped, potion keywords depend on this |
| Heroes | Hero.java, HeroType.java | Character abilities |
| Monsters | Monster.java, MonsterType.java | Enemy types |
| Levels | Level.java, Encounter.java | Dungeon structure |

Each uses workflow template with its own:
- Authoritative source (Java files)
- State JSON (ITEMS.json, etc.)
- Implementation patterns
- Test patterns

## Quick Reference

**Current blocked count:** ~72 keywords

**Highest-impact systems:**
1. buff_system (10 keywords + enables trill)
2. status_effect_system (8 keywords)
3. spell_tracking (6 keywords)
4. turn_start_processing (5 keywords)
5. meta_copy_advanced (5 keywords)

**Special cases:**
- `effect_types`: heal, shield, damage - verify if already exist
- `not_implementable`: removed - skip (deprecated)
- `validation_only`: permissive - targeting validation only
