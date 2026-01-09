package com.tann.dice.gameplay.trigger.personal;

public class TraitsRemoved extends Personal {
   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "dispelled";
   }

   @Override
   public String describeForSelfBuff() {
      return "Traits removed";
   }

   @Override
   public boolean allowTraits() {
      return false;
   }

   @Override
   public boolean singular() {
      return true;
   }
}
