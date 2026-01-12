
Oracle Test Triage: Comprehensive Next Steps

  Current State
  ┌─────────────┬───────┐
  │   Metric    │ Count │
  ├─────────────┼───────┤
  │ Total tests │ 248   │
  ├─────────────┼───────┤
  │ Passed      │ 178   │
  ├─────────────┼───────┤
  │ Failed      │ 46    │
  ├─────────────┼───────┤
  │ Errors      │ 1     │
  ├─────────────┼───────┤
  │ Skipped     │ 23    │
  └─────────────┴───────┘
  Ground truth: Java source is authoritative (per CLAUDE.md). Oracle tests are LLM-generated and unverified.

  ---
  Objective

  For each of the 47 failures/errors, determine:
  1. Is the oracle test wrong (LLM hallucinated the expected value)?
  2. Is the implementation wrong (fight.py doesn't match Java)?
  3. Is the test harness incomplete (context not set up correctly)?

  Then fix the appropriate component with Java citations.

  ---
  Category 1: State-Conditional Keywords (5 failures)

  Failures
  ┌─────────────────────┬──────────┬─────┬──────────┬──────────────────────────────────────────┐
  │       Test ID       │ Expected │ Got │ Keyword  │                Condition                 │
  ├─────────────────────┼──────────┼─────┼──────────┼──────────────────────────────────────────┤
  │ serrated_no_trigger │ 10       │ 20  │ SERRATED │ Target gained no shields this turn       │
  ├─────────────────────┼──────────┼─────┼──────────┼──────────────────────────────────────────┤
  │ patient_triggers    │ 20       │ 10  │ PATIENT  │ Source not used last turn                │
  ├─────────────────────┼──────────┼─────┼──────────┼──────────────────────────────────────────┤
  │ sloth_triggers      │ 20       │ 0   │ SLOTH    │ Target has fewer blank sides than source │
  ├─────────────────────┼──────────┼─────┼──────────┼──────────────────────────────────────────┤
  │ sloth_no_trigger    │ 10       │ 0   │ SLOTH    │ Target has more blank sides              │
  ├─────────────────────┼──────────┼─────┼──────────┼──────────────────────────────────────────┤
  │ tall_no_trigger     │ 10       │ 20  │ TALL     │ Target is not topmost                    │
  └─────────────────────┴──────────┴─────┴──────────┴──────────────────────────────────────────┘
  Investigation Steps

  1. SERRATED (serrated_no_trigger)
    - Check Keyword.java for SERRATED condition type
    - Verify _shields_gained_this_turn tracking in EntityState
    - Test harness sets shields_gained_this_turn: 3 but keyword still triggers
    - Question: Is the condition "gained shields" or "has shields"?
  2. PATIENT (patient_triggers)
    - Check how used_last_turn is defined in Java
    - Verify EntityState._used_last_turn default value
    - Test sets used_last_turn: false, expects x2, but we return base value
    - Question: What's the default state? Is our flag inverted?
  3. SLOTH (sloth_triggers, sloth_no_trigger)
    - Both return 0, suggesting SLOTH zeroes the value entirely
    - Check Java for SLOTH behavior - is it a conditional x2, or something else?
    - The test harness modifies entity.die.sides[i] to create blanks
    - Question: Does SLOTH count blank sides on the die, or something else?
  4. TALL (tall_no_trigger)
    - Check Java for "topmost" definition
    - Currently using entity.position = 0 for topmost
    - Question: Is position 0 topmost, or is there a separate topmost check?

  Java Files to Check

  - Keyword.java - condition definitions for SERRATED, PATIENT, SLOTH, TALL
  - EntState.java - usedLastTurn, shieldsGainedThisTurn fields
  - FightLog.java - how "topmost" is determined

  ---
  Category 2: Sequence Keywords (5 failures)

  Failures
  ┌─────────────────┬──────────┬─────┬─────────┬────────────────────────────────────────────┐
  │     Test ID     │ Expected │ Got │ Keyword │                  Context                   │
  ├─────────────────┼──────────┼─────┼─────────┼────────────────────────────────────────────┤
  │ run_triggers    │ 15       │ 10  │ RUN     │ previous_die_values: [3, 4], current: 5    │
  ├─────────────────┼──────────┼─────┼─────────┼────────────────────────────────────────────┤
  │ sprint_triggers │ 25       │ 10  │ SPRINT  │ previous_die_values: [1,2,3,4], current: 5 │
  ├─────────────────┼──────────┼─────┼─────────┼────────────────────────────────────────────┤
  │ duel_triggers   │ 20       │ 10  │ DUEL    │ target is targeting source                 │
  ├─────────────────┼──────────┼─────┼─────────┼────────────────────────────────────────────┤
  │ dejavu_basic    │ 20       │ 10  │ DEJAVU  │ same side used twice this turn             │
  ├─────────────────┼──────────┼─────┼─────────┼────────────────────────────────────────────┤
  │ spy_basic       │ 20       │ 10  │ SPY     │ copy first enemy attack value              │
  └─────────────────┴──────────┴─────┴─────────┴────────────────────────────────────────────┘
  Investigation Steps

  1. RUN/SPRINT (run_triggers, sprint_triggers)
    - RUN = x3 if straight of length 3, SPRINT = x5 if straight of length 5
    - Check _apply_conditional_keyword_bonuses for RUN/SPRINT logic
    - Verify get_last_n_die_effects() returns correct history
    - Question: Does our straight detection work? Are values being recorded?
  2. DUEL (duel_triggers)
    - DUEL = x2 if target is targeting source this turn
    - Check _targeters_this_turn tracking
    - Test harness doesn't set up targeting relationship
    - Likely harness gap: Need to populate _targeters_this_turn[source].add(target)
  3. DEJAVU (dejavu_basic)
    - DEJAVU = x2 if this exact side was used earlier this turn
    - Check _sides_used_per_turn tracking
    - Test harness doesn't set up previous side use
    - Likely harness gap: Need to record prior use of same side
  4. SPY (spy_basic)
    - SPY = copy value of first enemy attack this turn
    - Check _first_enemy_attack_this_turn tracking
    - Test harness doesn't set up enemy attack
    - Likely harness gap: Need to set _first_enemy_attack_this_turn

  Java Files to Check

  - FightLog.java - checkForStraight(), targeter tracking, spy implementation
  - Keyword.java - RUN, SPRINT, DUEL, DEJAVU, SPY definitions

  ---
  Category 3: Additive Bonuses (8 failures)

  Failures
  ┌──────────────────────┬──────────┬─────┬─────────────┬──────────────────────────────┐
  │       Test ID        │ Expected │ Got │   Keyword   │           Context            │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ era_bonus            │ 14       │ 10  │ ERA         │ elapsed_turns: 4             │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ minusEra_penalty     │ 6        │ 10  │ MINUS_ERA   │ elapsed_turns: 4             │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ rainbow_bonus        │ 13       │ 12  │ RAINBOW     │ (unique effect types used)   │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ skill_bonus          │ 13       │ 11  │ SKILL       │ (times_used_this_turn)       │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ fashionable_bonus    │ 15       │ 10  │ FASHIONABLE │ equipped_items_count: 5      │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ equipped_bonus       │ 12       │ 10  │ EQUIPPED    │ equipped_items_total_tier: 2 │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ affected_bonus       │ 14       │ 11  │ AFFECTED    │ effects_on_me: 3             │
  ├──────────────────────┼──────────┼─────┼─────────────┼──────────────────────────────┤
  │ bloodlust_no_damaged │ 10       │ 11  │ BLOODLUST   │ damaged_enemies: 0           │
  └──────────────────────┴──────────┴─────┴─────────────┴──────────────────────────────┘
  Investigation Steps

  1. ERA/MINUS_ERA (era_bonus, minusEra_penalty)
    - ERA adds +N where N = elapsed turns
    - Test sets elapsed_turns: 4, expects +4
    - Check if fight._turn is being read correctly
    - Verify _apply_conditional_keyword_bonuses handles ERA
  2. RAINBOW (rainbow_bonus)
    - RAINBOW adds +N where N = unique effect types used this fight
    - Expected 13 (base 10 + 3), got 12 (base 10 + 2)
    - Check tracking of unique effect types
    - Likely harness gap: Need to set up effect type history
  3. SKILL (skill_bonus)
    - SKILL adds +N where N = times this entity used this turn
    - Expected 13 (base 10 + 3), got 11 (base 10 + 1?)
    - Check times_used_this_turn tracking
    - Verify test harness sets this correctly
  4. FASHIONABLE (fashionable_bonus)
    - FASHIONABLE adds +N where N = equipped items count
    - Test sets equipped_items_count: 5, expects +5
    - Check if entity.equipped_items is being read
    - Likely implementation gap: May not be reading equipped items
  5. EQUIPPED (equipped_bonus)
    - EQUIPPED adds +N where N = total tier of equipped items
    - Test sets equipped_items_total_tier: 2, expects +2
    - Check if entity.equipped_item_tier_total is being read
  6. AFFECTED (affected_bonus)
    - AFFECTED adds +N where N = effects targeting me
    - Expected 14 (base 10 + 4?), got 11 (base 10 + 1?)
    - Check effect tracking on entity
    - Test harness applies weaken effects - verify they're counted
  7. BLOODLUST (bloodlust_no_damaged)
    - BLOODLUST adds +N where N = damaged enemies
    - Test sets damaged_enemies: 0, expects +0, but got +1
    - Check count_damaged_enemies() implementation
    - Possible off-by-one or counting target as damaged

  Java Files to Check

  - FightLog.java - bonus calculation methods
  - conditionalBonus/*.java - specific bonus implementations
  - EntState.java - equipped items, effect tracking

  ---
  Category 4: Visibility Modifiers (7 failures)

  Failures
  ┌──────────────────┬──────────┬─────┬─────────┬────────────────────────────────┐
  │     Test ID      │ Expected │ Got │ Keyword │              Base              │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ doubled_modifier │ 18       │ 14  │ DOUBLED │ 7 (×2=14? or display 14×2=28?) │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ squared_modifier │ 26       │ 14  │ SQUARED │ 7 (7²-7=42? or 7+7²?)          │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ onesie_modifier  │ 11       │ 14  │ ONESIE  │ 7 (display as 1?)              │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ threesy_modifier │ 13       │ 14  │ THREESY │ 7 (display as 3?)              │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ zeroed_modifier  │ 10       │ 14  │ ZEROED  │ 7 (display as 0?)              │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ plus_modifier    │ 15       │ 14  │ PLUS    │ 7 (+1 to display?)             │
  ├──────────────────┼──────────┼─────┼─────────┼────────────────────────────────┤
  │ fault_modifier   │ 9        │ 14  │ FAULT   │ 7 (-1 to display?)             │
  └──────────────────┴──────────┴─────┴─────────┴────────────────────────────────┘
  Key Question

  Do visibility modifiers affect the die's own value, or only how OTHER dice see it (for PAIR)?

  The oracle expects these to modify the actual calculated value. Our implementation returns 14 for all (base 7 + some bonus?). This suggests:
  - Either the oracle misunderstands visibility modifiers
  - Or our implementation doesn't apply them to self

  Investigation Steps

  1. Check Keyword.java for visibility modifier definitions
  2. Check Side.get_visible_value() vs calculated_value
  3. Determine if visibility affects self or only observers
  4. Look for VisibilityType or similar in Java

  Java Files to Check

  - Keyword.java - DOUBLED, SQUARED, ONESIE, THREESY, ZEROED, PLUS, FAULT
  - FightLog.java - how visible value is used
  - Side.java - getVisibleValue() method

  ---
  Category 5: Targeting Keywords (5 failures)

  Failures
  ┌───────────────────┬──────────┬─────┬───────────┬───────────────────────┐
  │      Test ID      │ Expected │ Got │  Keyword  │       Scenario        │
  ├───────────────────┼──────────┼─────┼───────────┼───────────────────────┤
  │ eliminate_invalid │ 0        │ 10  │ ELIMINATE │ Target not lowest HP  │
  ├───────────────────┼──────────┼─────┼───────────┼───────────────────────┤
  │ heavy_invalid     │ 0        │ 10  │ HEAVY     │ Target not highest HP │
  ├───────────────────┼──────────┼─────┼───────────┼───────────────────────┤
  │ generous_invalid  │ 0        │ 10  │ GENEROUS  │ Target is self        │
  ├───────────────────┼──────────┼─────┼───────────┼───────────────────────┤
  │ scared_invalid    │ 0        │ 3   │ SCARED    │ Target HP > pip value │
  ├───────────────────┼──────────┼─────┼───────────┼───────────────────────┤
  │ picky_invalid     │ 0        │ 5   │ PICKY     │ Target HP ≠ pip value │
  └───────────────────┴──────────┴─────┴───────────┴───────────────────────┘
  Key Question

  Should invalid targeting return value 0, or is targeting validity separate from value calculation?

  Our implementation calculates value regardless of targeting validity. The game UI would prevent the use, but we compute the value anyway.

  Investigation Steps

  1. Check if Java returns 0 for invalid targets or just prevents use
  2. Determine if oracle expectation is correct
  3. If targeting should affect value, modify run_value_test to return 0 for invalid
  4. If targeting is separate, fix oracle tests

  Java Files to Check

  - FightLog.java - target validation
  - Keyword.java - ELIMINATE, HEAVY, GENEROUS, SCARED, PICKY conditions

  ---
  Category 6: Compound Keywords (7 failures)

  Failures
  ┌─────────────────────┬──────────┬─────┬─────────────────────┬──────────────────────────────────┐
  │       Test ID       │ Expected │ Got │      Keywords       │             Analysis             │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ engarged_triggers   │ 14       │ 28  │ ENGAGE+BERSERK      │ (10+4)×2=28 vs 10+4=14           │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ engarged_no_trigger │ 10       │ 14  │ ENGAGE+BERSERK      │ 10+4=14 (BERSERK always applies) │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ cruesh_triggers     │ 17       │ 34  │ CRUEL+SMASH         │ Similar pattern                  │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ pristeel_triggers   │ 14       │ 28  │ PRISTINE+STEEL      │ Similar pattern                  │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ deathlust_triggers  │ 13       │ 26  │ DEATHWISH+BLOODLUST │ Similar pattern                  │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ trill_triggers      │ 13       │ 31  │ TRIO+?              │ Complex interaction              │
  ├─────────────────────┼──────────┼─────┼─────────────────────┼──────────────────────────────────┤
  │ engage_cruel_combo  │ 40       │ 20  │ ENGAGE+CRUEL        │ Both should x2 = x4?             │
  └─────────────────────┴──────────┴─────┴─────────────────────┴──────────────────────────────────┘
  Key Question

  What is the correct order of operations for compound keywords?

  Pattern observed:
  - Oracle expects: additive bonus only, no multiplication (or single x2)
  - Our implementation: applies both multiplication AND additive

  For engarged_triggers (ENGAGE + BERSERK):
  - Oracle: 10 + 4 = 14 (no ENGAGE trigger?)
  - Ours: (10 + 4) × 2 = 28

  For engage_cruel_combo (ENGAGE + CRUEL):
  - Oracle: 10 × 2 × 2 = 40 (both trigger)
  - Ours: 10 × 2 = 20 (only one triggers?)

  Investigation Steps

  1. Check Java for compound keyword application order
  2. Verify if multiplicative keywords stack (x2 × x2 = x4?)
  3. Check if additive bonuses apply before or after multipliers
  4. Look for applyConditionalBonuses() method ordering

  Java Files to Check

  - FightLog.java - applyConditionalBonuses() or similar
  - conditionalBonus/*.java - application order

  ---
  Category 7: DOUB_DIFF/REV_DIFF Interactions (6 failures)

  Failures
  ┌──────────────────────────┬──────────┬─────┬───────────────────────┐
  │         Test ID          │ Expected │ Got │       Keywords        │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ doubDiff_with_steel      │ 18       │ 14  │ DOUB_DIFF+STEEL       │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ doubDiff_with_bloodlust  │ 16       │ 13  │ DOUB_DIFF+BLOODLUST   │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ doubDiff_with_minusFlesh │ 4        │ 7   │ DOUB_DIFF+MINUS_FLESH │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ revDiff_with_steel       │ 6        │ 14  │ REV_DIFF+STEEL        │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ revDiff_with_bloodlust   │ 7        │ 13  │ REV_DIFF+BLOODLUST    │
  ├──────────────────────────┼──────────┼─────┼───────────────────────┤
  │ revDiff_with_minusFlesh  │ 13       │ 7   │ REV_DIFF+MINUS_FLESH  │
  └──────────────────────────┴──────────┴─────┴───────────────────────┘
  Key Question

  How do DOUB_DIFF and REV_DIFF modify values?

  - DOUB_DIFF likely doubles the difference from some baseline
  - REV_DIFF likely reverses/negates something

  The pattern suggests these interact with additive bonuses in complex ways.

  Investigation Steps

  1. Find DOUB_DIFF and REV_DIFF in Java source
  2. Understand their calculation formula
  3. Verify interaction with STEEL, BLOODLUST, MINUS_FLESH
  4. Check if oracle tests have correct expectations

  Java Files to Check

  - Keyword.java - DOUB_DIFF, REV_DIFF definitions
  - FightLog.java - how these modify calculations

  ---
  Category 8: Miscellaneous (3 failures + 1 error)

  Failures
  ┌───────────────────────┬──────────┬───────┬───────────────────────────┐
  │        Test ID        │ Expected │  Got  │           Issue           │
  ├───────────────────────┼──────────┼───────┼───────────────────────────┤
  │ swapTerminal_triggers │ 20       │ 10    │ SWAP_TERMINAL compound    │
  ├───────────────────────┼──────────┼───────┼───────────────────────────┤
  │ halveDuel_triggers    │ 5        │ 10    │ HALVE_DUEL compound       │
  ├───────────────────────┼──────────┼───────┼───────────────────────────┤
  │ fierce_no_flee        │ dmg 5    │ dmg 6 │ FIERCE damage calculation │
  └───────────────────────┴──────────┴───────┴───────────────────────────┘
  Error
  ┌─────────────────────┬───────────────────────────────────┐
  │       Test ID       │               Error               │
  ├─────────────────────┼───────────────────────────────────┤
  │ copycat_no_previous │ 'NoneType' object is not iterable │
  └─────────────────────┴───────────────────────────────────┘
  Investigation Steps

  1. SWAP_TERMINAL, HALVE_DUEL: Find these compound keywords in Java
  2. FIERCE: Check if FIERCE modifies damage calculation
  3. COPYCAT edge case: Handle None when no previous die exists

  Java Files to Check

  - Keyword.java - SWAP_TERMINAL, HALVE_DUEL, FIERCE
  - FightLog.java - COPYCAT handling when no previous die

  ---
  Execution Plan

  Phase 1: Quick Wins (Harness Gaps)

  Fix obvious test harness gaps that don't require Java verification:

  1. DUEL: Add _targeters_this_turn setup
  2. DEJAVU: Add _sides_used_per_turn setup
  3. SPY: Add _first_enemy_attack_this_turn setup
  4. COPYCAT error: Handle None case gracefully

  Phase 2: Java Verification (Sample)

  Pick one representative from each category and verify against Java:

  1. PATIENT (state-conditional)
  2. RUN (sequence)
  3. ERA (additive)
  4. DOUBLED (visibility)
  5. ELIMINATE (targeting)
  6. ENGARGED (compound)
  7. DOUB_DIFF (complex interaction)

  Phase 3: Batch Fixes

  Based on Phase 2 findings:
  - Fix oracle tests with incorrect expectations
  - Fix implementation bugs with Java citations
  - Complete harness gaps identified

  Phase 4: Remaining Tests

  Address remaining failures using patterns established in Phase 2-3.

  ---
  Output Format

  For each investigated failure, document:

  ## Test: {test_id}

  ### Oracle Expectation
  - Expected: {value}
  - Keywords: {keywords}
  - Context: {relevant context}

  ### Java Source Analysis
  - File: {filename}:{line}
  - Relevant code: {snippet}
  - Behavior: {description}

  ### Verdict
  [ ] Oracle is correct, implementation is wrong
  [ ] Oracle is wrong, implementation is correct
  [ ] Test harness gap (missing context setup)

  ### Fix
  - Location: {file to fix}
  - Change: {description}
  - Citation: {Java reference}

  ---
  Files Reference
  ┌──────────────────────────────────────────┬──────────────────────────────────────┐
  │                   File                   │               Purpose                │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ decompiled/Keyword.java                  │ Keyword enum, conditions, types      │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ decompiled/FightLog.java                 │ Combat resolution, bonus application │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ decompiled/EntState.java                 │ Entity state, condition checks       │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ decompiled/conditionalBonus/*.java       │ Bonus calculation logic              │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ combat/src/fight.py                      │ Python implementation                │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ combat/oracle_tests/generated_tests.json │ Oracle test expectations             │
  ├──────────────────────────────────────────┼──────────────────────────────────────┤
  │ combat/tools/verify_oracle.py            │ Test harness                         │
  └──────────────────────────────────────────┴──────────────────────────────────────┘
