package com.tann.dice.gameplay.modifier.modBal;

import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.util.TannLog;
import java.util.Arrays;
import java.util.List;

public class TierUtils {
   static final float ZENITH_FACTOR = 3.5F;

   private static float combineModTier(Choosable[] th) {
      return combineModTier(Arrays.asList(th));
   }

   private static float combineModTier(List<Choosable> th) {
      float result = 0.0F;

      for (int i = 0; i < th.size(); i++) {
         result += th.get(i).getModTier();
      }

      return result;
   }

   public static float toModTier(ChoosableType tht, int tier) {
      switch (tht) {
         case Modifier:
            return tier;
         case Item:
            return itemModTier(tier);
         case Hero:
            return extraHeroModTier(tier);
         default:
            TannLog.error("unimmp " + tht);
            return 489.0F;
      }
   }

   public static float effectTierToModTier(float effectTier) {
      return effectTier * 3.5F;
   }

   public static float modTierToHeroEffectTier(float modTier, int heroTier) {
      return modTier * 0.82F;
   }

   public static float extraHeroModTier(int tier) {
      float val = 1.0F + HeroTypeUtils.getEffectTierFor(tier) * 3.1F;
      return tier == 0 ? val * 0.54F : val;
   }

   public static float levelupHeroChoosable(int tier) {
      return levelupHeroChoosable(tier - 1, tier);
   }

   public static float levelupHeroChoosable(int from, int to) {
      return (extraHeroModTier(to) - extraHeroModTier(from)) * 1.1F;
   }

   public static float itemModTier(int tier) {
      if (tier == -69) {
         return 0.0F;
      } else if (tier == 0) {
         return 0.05F;
      } else {
         return tier < 0 ? tier : tier / 3.0F;
      }
   }

   public static float totalModTier(List<Choosable> result) {
      float total = 0.0F;

      for (int i = 0; i < result.size(); i++) {
         total += result.get(i).getModTier();
      }

      return total;
   }

   public static int fromModTier(ChoosableType cht, float targetModTier) {
      float lower = cht.getLower();
      float upper = cht.getUpper();
      int diff = (int)(upper - lower);
      int maxSteps = (int)(Math.log(Integer.highestOneBit(diff)) / Math.log(2.0) + 1.0);

      for (int i = 0; i < maxSteps; i++) {
         int testTier = Math.round((upper + lower) / 2.0F);
         float val = toModTier(cht, testTier);
         if (val > targetModTier) {
            upper = testTier;
         } else {
            if (!(val < targetModTier)) {
               return testTier;
            }

            lower = testTier;
         }

         if (i == maxSteps - 1) {
            if (Math.abs(toModTier(cht, testTier + 1) - targetModTier) < Math.abs(val - targetModTier)) {
               return testTier + 1;
            }

            if (Math.abs(toModTier(cht, testTier - 1) - targetModTier) < Math.abs(val - targetModTier)) {
               return testTier - 1;
            }

            return testTier;
         }
      }

      return -999;
   }

   public static int equivalent(ChoosableType tht, Choosable th) {
      return fromModTier(tht, th.getModTier());
   }

   public static int equivalent(ChoosableType tht, List<Choosable> th) {
      return fromModTier(tht, combineModTier(th));
   }

   public static int equivalentInverse(ChoosableType tht, List<Choosable> th) {
      return fromModTier(tht, -combineModTier(th));
   }

   public static float totalHeroEffectTierForTactic(int tier) {
      return tier * 0.13F;
   }

   public static float doubleLoot() {
      return itemModTier(5) * 5.0F;
   }

   public static float doubleXp() {
      return levelupHeroChoosable(2) * 5.0F;
   }
}
