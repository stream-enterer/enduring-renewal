package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class PersonalTextureReplaced extends Personal {
   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean skipEquipImage() {
      return true;
   }
}
