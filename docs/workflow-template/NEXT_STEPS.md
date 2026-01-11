# Next Steps After Keywords Complete

## Current Status

- **Implemented:** 88 keywords
- **Blocked:** 23 keywords (on 10 infrastructure systems)
- **Remaining:** 80 keywords

## Phase 1: Complete Remaining Keywords

Continue with "continue" workflow until `remaining = 0` (excluding blocked).

## Phase 2: Funnel Sieve Verification

Run ~16 gameplay tests to verify implementations:

**Layer 1: Combined Keywords**
- `engine` → verifies engage, pristine, x4 stacking
- `paxin` → verifies pair, chain, XOR logic
- `engarged` → verifies engage, charged
- `cruesh` → verifies cruel, flesh

**Layer 2: Variant Keywords**
- `antiEngage` → verifies anti* prefix
- `halveEngage` → verifies halve* prefix
- `swapCruel` → verifies swap* prefix
- `groupGrowth` → verifies group* prefix

**Layer 3-4:** Depends on infrastructure being implemented first.

## Phase 3: Implement Blocked Infrastructure

Each infrastructure system unlocks keywords:

| Infrastructure | Blocked Keywords | Priority |
|----------------|------------------|----------|
| turn_tracking | patient, era, minusEra | Medium |
| target_tracking | focus, duel | Medium |
| item_system | hoard, fashionable, equipped | Low |
| poison_tracking | plague, acidic | Medium |
| buff_tracking | buffed, affected, skill | High |
| usage_tracking | doubleUse, quadUse, hyperUse, rite | High |
| entity_summoning | boned, hyperBoned | Low |

**Dependency chain:**
- `duel` blocks → halveDuel, duegue
- `focus` blocks → underocus
- `skill` blocks → trill

**Suggested order:**
1. buff_tracking (unlocks 3 keywords + enables skill → trill)
2. usage_tracking (unlocks 4 keywords)
3. turn_tracking (unlocks 3 keywords)
4. target_tracking (unlocks 2 keywords + enables duel variants)
5. poison_tracking (unlocks 2 keywords)
6. entity_summoning (unlocks 2 keywords)
7. item_system (unlocks 3 keywords, lowest priority)

## Phase 4: Complete Blocked Keywords

After each infrastructure is implemented:
1. Move keywords from `blocked` to remaining
2. Implement using standard workflow
3. Update KEYWORDS.json

## Phase 5: Full Funnel Sieve

Run complete verification including:
- Layer 3: Infrastructure keywords (poison, weaken, boned, inflictPain)
- Layer 4: Edge cases (stacking, parameterized, self-targeting, death triggers)

## Phase 6: Other Systems

After keywords complete, potential next tasks:
- **Items** - Equipment, consumables, artifacts
- **Heroes** - Character abilities, classes
- **Monsters** - Enemy types, behaviors
- **Levels** - Dungeon generation, encounters

Each would use the workflow template with its own:
- Authoritative source (Item.java, Hero.java, etc.)
- State JSON (ITEMS.json, HEROES.json, etc.)
- Implementation patterns
- Test patterns

## Notes

- The workflow template in this folder can be adapted for any of these systems
- Use ADAPTATION_GUIDE.md to verify adaptations
- Prioritize systems that unblock other work
