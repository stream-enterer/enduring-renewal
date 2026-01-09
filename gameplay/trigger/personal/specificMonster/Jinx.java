package com.tann.dice.gameplay.trigger.personal.specificMonster;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class Jinx extends Personal {
   final Global global;
   final Modifier m;

   public Jinx(Modifier c) {
      this.m = c;
      Global g = c.getSingleGlobalOrNull();
      if (g == null) {
         throw new RuntimeException("Error, blight with >1 global from curse: " + c.getName());
      } else {
         this.global = g;
      }
   }

   @Override
   public Global getGlobalFromPersonalTrigger() {
      return this.global;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "blight";
   }

   @Override
   public String describeForSelfBuff() {
      return this.global == null ? "A random tier 2 or 3 curse" : this.global.describeForSelfBuff();
   }

   private float jFactor() {
      return this.m.getFloatTier() / -3.0F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp + 1.5F * this.jFactor();
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 1.8F * this.jFactor();
   }
}
