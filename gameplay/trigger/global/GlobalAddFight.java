package com.tann.dice.gameplay.trigger.global;

import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalAddFight extends Global {
   private final int delta;

   public GlobalAddFight(int delta) {
      this.delta = delta;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.delta) + " " + Words.plural("fight", this.delta);
   }

   @Override
   public int affectTotalLength(int base) {
      return base + this.delta;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
