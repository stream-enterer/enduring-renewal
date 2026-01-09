package com.tann.dice.gameplay.trigger.personal.immunity;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class Immunity extends Personal {
   @Override
   public boolean isMultiplable() {
      return false;
   }

   @Override
   public boolean singular() {
      return true;
   }
}
