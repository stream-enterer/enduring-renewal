package com.tann.dice.gameplay.trigger.global;

import com.tann.dice.util.Tann;

public class GlobalReinforcementsLimitMultiply extends Global {
   final float multiple;

   public GlobalReinforcementsLimitMultiply(float multiple) {
      this.multiple = multiple;
   }

   @Override
   public String describeForSelfBuff() {
      return "+" + Tann.floatFormat((this.multiple - 1.0F) * 100.0F) + "% space for enemies (before becoming reinforcements)";
   }

   @Override
   public int affectReinforcements(int amt) {
      return (int)(amt * this.multiple);
   }
}
