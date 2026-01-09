package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class ClearIcons extends Personal {
   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean clearIcons() {
      return true;
   }

   @Override
   public boolean skipEquipImage() {
      return true;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }
}
