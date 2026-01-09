package com.tann.dice.gameplay.trigger.personal.affectSideModular.named;

import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveAllKeywords;

public class Divested extends RemoveAllKeywords {
   @Override
   public boolean showInPanel() {
      return true;
   }

   @Override
   public String getImageName() {
      return "divested";
   }
}
