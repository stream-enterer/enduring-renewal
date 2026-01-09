package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.util.Tann;

public class PipeModTurn extends PipeRegexNamed<Modifier> {
   static final String PREF = "t";

   public PipeModTurn() {
      super(prnS("t"), DIGIT, prnS("\\."), MODIFIER);
   }

   public Modifier example() {
      for (int i = 0; i < 100; i++) {
         Modifier m = make(Tann.randomInt(1, 9), ModifierLib.random());
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

   private static Modifier make(int turn, Modifier mod) {
      if (turn != 0 && validateModifier(mod)) {
         float tier = mod.getTier() * levelMult(turn);
         return new Modifier(tier, "t" + turn + "." + mod.getName(), new GlobalTurnRequirement(new TurnRequirementN(turn), mod.getSingleGlobalOrNull()));
      } else {
         return null;
      }
   }

   private static float levelMult(int level) {
      return (float)(1.0 / Math.pow(2.0, level));
   }

   public static boolean validateModifier(Modifier mod) {
      if (mod != null && !mod.isMissingno() && !mod.isOnPick()) {
         Global glob = mod.getSingleGlobalOrNull();
         return glob != null && !glob.allTurnsOnly();
      } else {
         return false;
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }
}
