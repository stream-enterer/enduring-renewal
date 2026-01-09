package com.tann.dice.gameplay.trigger.personal;

public class ForeverYoung extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Cannot level up";
   }

   @Override
   public String getImageName() {
      return "young";
   }

   @Override
   public boolean canLevelUp() {
      return false;
   }
}
