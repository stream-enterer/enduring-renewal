package com.tann.dice.gameplay.trigger.personal.equipRestrict;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class EquipRestrict extends Personal {
   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public boolean skipMultiplable() {
      return true;
   }
}
