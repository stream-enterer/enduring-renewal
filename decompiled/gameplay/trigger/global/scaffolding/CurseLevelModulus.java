package com.tann.dice.gameplay.trigger.global.scaffolding;

import com.tann.dice.gameplay.trigger.global.Global;

public class CurseLevelModulus extends Global {
   final int mod;

   public CurseLevelModulus(int mod) {
      this.mod = mod;
   }

   @Override
   public String describeForSelfBuff() {
      return "Looping difficulty like cursed mode";
   }

   @Override
   public int getLevelNumberForGameplay(int level) {
      return (level - 1) % this.mod + 1;
   }
}
