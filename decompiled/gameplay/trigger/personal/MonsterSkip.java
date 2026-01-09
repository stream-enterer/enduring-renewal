package com.tann.dice.gameplay.trigger.personal;

public class MonsterSkip extends Personal {
   @Override
   public boolean preventAction() {
      return true;
   }

   @Override
   protected boolean showInEntPanelInternal() {
      return false;
   }
}
