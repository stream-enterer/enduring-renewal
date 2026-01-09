package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class MimicDescription extends Personal {
   @Override
   public boolean showInDiePanel() {
      return false;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public String describeForSelfBuff() {
      return "[text]My top side is a copy of the top hero's top side, similar for my bottom side";
   }

   @Override
   public String getImageName() {
      return "big/mimic";
   }
}
