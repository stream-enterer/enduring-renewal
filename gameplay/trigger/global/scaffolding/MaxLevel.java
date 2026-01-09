package com.tann.dice.gameplay.trigger.global.scaffolding;

import com.tann.dice.gameplay.trigger.global.Global;

public class MaxLevel extends Global {
   final int max;

   public MaxLevel(int max) {
      this.max = max;
   }

   @Override
   public int getMaxLevel(int level) {
      return this.max;
   }
}
