package com.tann.dice.gameplay.effect.eff;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffUtils {
   public static Eff random(Random r) {
      return random(r, null);
   }

   public static Eff random(Random r, Boolean needsTarget) {
      return random(r, needsTarget, true);
   }

   public static Eff random(Random r, Boolean needsTarget, boolean stripKeywords) {
      int MAX = 50;
      Eff e = null;

      for (int i = 0; i < 50; i++) {
         e = Tann.random(HeroTypeUtils.getSidesWithColour(Tann.random(HeroCol.basics(), r), true, true), r).getBaseEffect();
         if (isGoodForRand(e, needsTarget)) {
            if (stripKeywords) {
               e.clearKeywords();
            }

            return e;
         }
      }

      return new EffBill().damage(1000).bEff();
   }

   private static boolean isGoodForRand(Eff e, Boolean needsTarget) {
      return needsTarget != null && e.needsTarget() != needsTarget ? false : e.getType() != EffType.Blank;
   }

   public static List<ConditionalRequirement> getRestrictionsForTog(Eff eff) {
      List<ConditionalRequirement> result = new ArrayList<>(eff.getRestrictions(false));

      for (Keyword k : eff.getKeywordForGameplay()) {
         ConditionalRequirement cr = k.getTargetingConditionalRequirement();
         if (cr != null) {
            result.add(cr);
         }

         ConditionalBonus cb = k.getConditionalBonus();
         if (cb != null && cb.requirement != null) {
            result.add(cb.requirement);
         }
      }

      return result;
   }

   public static String describe(Eff[] effs) {
      return describe(effs, true);
   }

   public static String describe(Eff[] effs, boolean withThisTurn) {
      String result = "";

      for (Eff e : effs) {
         String desc = e.describe(false, withThisTurn);
         if (!desc.isEmpty()) {
            if (result.length() > 0) {
               result = result + com.tann.dice.Main.t(", then ");
            }

            result = result + e.describe(false, withThisTurn).toLowerCase();
         }
      }

      return Eff.addKeywordsToString(result, effs[0]);
   }
}
