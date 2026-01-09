package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementEveryN;
import com.tann.dice.util.Tann;

public class PipeModTurnEvery extends PipeRegexNamed<Modifier> {
   static final String PREF = "et";

   public PipeModTurnEvery() {
      super(prnS("et"), DIGIT, prnS("\\."), MODIFIER);
   }

   public Modifier example() {
      for (int i = 0; i < 100; i++) {
         Modifier m = make(Tann.randomInt(2, 6), ModifierLib.random());
         if (m != null) {
            return m;
         }
      }

      return ModifierLib.getMissingno();
   }

   protected Modifier internalMake(String[] groups) {
      String sLev = groups[0];
      String sMod = groups[1];
      return !Tann.isInt(sLev) ? null : make(Integer.parseInt(sLev), ModifierLib.byName(sMod));
   }

   private static Modifier make(int everyN, Modifier mod) {
      if (everyN > 1 && PipeModTurn.validateModifier(mod)) {
         float tier = mod.getTier() * levelMult(everyN);
         return new Modifier(
            tier, "et" + everyN + "." + mod.getName(), new GlobalTurnRequirement(new TurnRequirementEveryN(everyN), mod.getSingleGlobalOrNull())
         );
      } else {
         return null;
      }
   }

   private static float levelMult(int level) {
      if (level < 1) {
         return Float.NaN;
      } else {
         return level == 1 ? 1.0F : (float)(1.0F / level * Math.pow(0.9F, level + 1));
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
