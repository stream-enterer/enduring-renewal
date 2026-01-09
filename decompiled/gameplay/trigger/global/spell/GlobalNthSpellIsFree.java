package com.tann.dice.gameplay.trigger.global.spell;

import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.lang.Words;

public class GlobalNthSpellIsFree extends Global {
   final int n;
   final boolean turn;

   public GlobalNthSpellIsFree(int n, boolean turn) {
      this.n = n;
      this.turn = turn;
   }

   @Override
   public String describeForSelfBuff() {
      return "The " + Words.ordinal(this.n) + " spell you cast each " + (this.turn ? "turn" : "fight") + " is free.";
   }

   @Override
   public int affectSpellCost(Spell s, int cost, Snapshot snapshot) {
      int amt;
      if (this.turn) {
         amt = snapshot.getTotalSpellsUsedThisTurn();
      } else {
         amt = snapshot.getTotalSpellsUsedThisFight();
      }

      return amt == this.n - 1 ? 0 : super.affectSpellCost(s, cost, snapshot);
   }

   @Override
   public float getPriority() {
      return 1.0F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }

   @Override
   public String hyphenTag() {
      return this.n + "";
   }
}
