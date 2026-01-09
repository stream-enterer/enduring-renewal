package com.tann.dice.gameplay.trigger.global.spell;

import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalSpecificSpellCostChange extends Global {
   final int delta;
   final String spellName;

   public GlobalSpecificSpellCostChange(int delta, String spellName) {
      this.delta = delta;
      this.spellName = spellName;
   }

   @Override
   public int affectSpellCost(Spell s, int cost, Snapshot snapshot) {
      return s.getTitle().equalsIgnoreCase(this.spellName) ? Math.max(1, cost + this.delta) : cost;
   }
}
