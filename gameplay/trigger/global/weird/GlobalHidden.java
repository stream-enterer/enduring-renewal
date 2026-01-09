package com.tann.dice.gameplay.trigger.global.weird;

import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalHidden extends Global {
   @Override
   public boolean skipEquipImage() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "Hidden unless you enable 'show hidden'";
   }

   @Override
   public boolean isHidden() {
      return true;
   }

   @Override
   public boolean skipNotifyRandomReveal() {
      return true;
   }
}
