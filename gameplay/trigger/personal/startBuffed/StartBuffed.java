package com.tann.dice.gameplay.trigger.personal.startBuffed;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class StartBuffed extends Personal {
   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }
}
