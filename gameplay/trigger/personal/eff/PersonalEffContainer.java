package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class PersonalEffContainer extends Personal {
   private final Eff[] contents;

   public PersonalEffContainer(Eff content) {
      this.contents = new Eff[]{content};
   }

   public PersonalEffContainer(Eff[] contents) {
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
