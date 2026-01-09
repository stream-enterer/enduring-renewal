package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;

public class Generated extends Personal {
   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public boolean showInDiePanel() {
      return false;
   }

   @Override
   public boolean isGenerated() {
      return true;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }
}
