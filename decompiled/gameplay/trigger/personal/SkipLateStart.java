package com.tann.dice.gameplay.trigger.personal;

public class SkipLateStart extends Personal {
   @Override
   public boolean bannedFromLateStart() {
      return true;
   }
}
