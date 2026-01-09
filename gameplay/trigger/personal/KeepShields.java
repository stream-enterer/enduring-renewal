package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.TargetingType;

public class KeepShields extends Personal {
   @Override
   public boolean keepShields() {
      return true;
   }

   @Override
   public String getImageName() {
      return "keepShields";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      return source.getTargetingType() == TargetingType.Group ? "Targets keep unused shields" : "Target keeps unused shields";
   }

   @Override
   public String describeForSelfBuff() {
      return "Keep unused shields";
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      return 0.2F;
   }
}
