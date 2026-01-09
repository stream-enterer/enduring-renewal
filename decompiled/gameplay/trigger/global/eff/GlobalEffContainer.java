package com.tann.dice.gameplay.trigger.global.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalEffContainer extends Global {
   final Eff[] contents;

   public GlobalEffContainer(Eff content) {
      this.contents = new Eff[]{content};
   }

   public GlobalEffContainer(Eff[] contents) {
      this.contents = contents;
   }

   @Override
   public final boolean isMultiplable() {
      for (int i = 0; i < this.contents.length; i++) {
         if (!this.contents[i].isMultiplable()) {
            return false;
         }
      }

      return true;
   }
}
