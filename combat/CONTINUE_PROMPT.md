# Continue Keyword Implementation

Read `KEYWORDS.json` to check implemented list, then pick the first applicable:

1. **If `defy` not implemented** → Add to `_apply_conditional_keyword_bonuses()`: +N where N = incoming damage. Check if `get_incoming_damage()` exists on EntityState.

2. **If `selfPetrify` not implemented** → Add to `use_die()` post-processing: petrify self. Pattern matches existing selfShield/selfHeal.

3. **If targeting keywords missing** (eliminate, heavy, generous, scared, picky) → Add `_validate_target()` to FightLog. Check Java `TargetingRestriction`.

4. **If cleave/descend not implemented** → Modify effect application in `use_die()` to hit adjacent targets.

5. **If manacost/mandatory/fierce missing** → Add usage checks in `use_die()`.

6. **If stasis/sticky/cantrip missing** → Add behavior flags checked during side modification/reroll/usage.

7. **If value visibility keywords missing** (fault, plus, doubled, squared, onesie, threesy, zeroed) → Research Java "others see" mechanism, add to meta processing.

8. **If turn-based keywords missing** (shifter, lucky, critical, fluctuate, fumble) → Needs turn-start hooks first. Add to blocked if no hooks exist.

9. **If buff/debuff keywords missing** (poison, regen, weaken, boost, etc.) → Major system needed. Add to blocked or implement buff system first.

10. **Otherwise** → Pick any remaining from `KEYWORD_CHUNKS.md`.

## Workflow
1. Add enum to `dice.py` (SCREAMING_SNAKE_CASE)
2. Write failing test in `test_keyword.py`
3. Implement in appropriate location
4. Run `uv run pytest`
5. Add to `implemented` in `KEYWORDS.json`
6. Commit: `Implement <keyword> keyword`
