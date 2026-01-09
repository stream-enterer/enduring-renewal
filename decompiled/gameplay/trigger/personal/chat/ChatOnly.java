package com.tann.dice.gameplay.trigger.personal.chat;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class ChatOnly extends Personal {
   @Override
   public boolean skipCalc() {
      return true;
   }

   @Override
   public boolean showInDiePanel() {
      return false;
   }

   @Override
   public boolean skipTraitPanel() {
      return true;
   }

   @Override
   protected boolean showInEntPanelInternal() {
      return false;
   }
}
