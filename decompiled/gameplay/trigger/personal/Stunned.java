package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.lang.Words;

public class Stunned extends Personal {
   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "stun";
   }

   @Override
   public boolean preventAction() {
      return true;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      switch (source.getTargetingType()) {
         case Group:
            return "Stun all " + Words.entName(source, true);
         default:
            return "Stun " + Words.entName(source, false);
      }
   }

   @Override
   public String describeForSelfBuff() {
      if (this.buff == null) {
         return "Stunned";
      } else {
         return this.buff.turns != 0 && this.buff.turns != 1 ? "Stunned for " + this.buff.turns + " turns." : "Stunned this turn";
      }
   }

   @Override
   public String[] getSound() {
      return Sounds.clangs;
   }
}
