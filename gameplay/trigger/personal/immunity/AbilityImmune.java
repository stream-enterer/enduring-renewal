package com.tann.dice.gameplay.trigger.personal.immunity;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.lang.Words;

public class AbilityImmune extends Immunity {
   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return describeImmuneToAbilities();
   }

   public static String describeImmuneToAbilities() {
      return "Immune to " + Words.spab(true);
   }

   @Override
   public boolean immuneToAbilities() {
      return true;
   }

   @Override
   public String getImageName() {
      return "refractive";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 1.25F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL | Collision.TACTIC;
   }
}
