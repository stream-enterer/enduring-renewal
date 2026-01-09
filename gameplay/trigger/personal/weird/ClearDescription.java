package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class ClearDescription extends Personal {
   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   protected boolean clearDescription() {
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
