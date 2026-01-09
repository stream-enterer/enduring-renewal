package com.tann.dice.gameplay.trigger.global.weird;

import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalTemporary extends Global {
   @Override
   public boolean skipEquipImage() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "Removed after one fight";
   }
}
