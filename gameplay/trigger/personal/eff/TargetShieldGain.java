package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.ShieldsRemaining;

public class TargetShieldGain extends PersonalEffContainer {
   final Eff eff;
   final int value;

   public TargetShieldGain(Eff eff, int value) {
      super(eff);
      this.eff = eff;
      this.value = value;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void targetGainsShield(EntState me, EntState shieldGainer) {
      if (shieldGainer.getShields() >= this.value) {
         ShieldsRemaining.useEffMaybeUntargeted(me, this.eff);
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("If an enemy I target gets " + this.value + "+ shields, ") + com.tann.dice.Main.t(this.eff.describe());
   }

   @Override
   public String getImageName() {
      return "shieldGain";
   }
}
