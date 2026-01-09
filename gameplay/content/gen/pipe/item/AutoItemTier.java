package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;

public class AutoItemTier {
   private static float ITEM_GEN_TIER_SCALING_FACTOR = 1.5F;

   public static float guessPowerReplace(int assumedTier, EntSide es, SpecificSidesType sst) {
      float pw = es.getApproxTotalEffectTier(HeroTypeUtils.defaultHero(assumedTier));
      float result = 0.0F;
      result += pw * sst.sideIndices.length;
      float tierPower = HeroTypeUtils.getEffectTierFor(assumedTier);
      result -= getPainCausedFromReplacing(sst) * tierPower * 0.9F;
      return result * ITEM_GEN_TIER_SCALING_FACTOR;
   }

   private static float getPainCausedFromReplacing(SpecificSidesType sst) {
      float total = 0.0F;

      for (int side : sst.sideIndices) {
         float val;
         switch (side) {
            case 0:
            case 1:
               val = 0.5F;
               break;
            case 2:
               val = 1.0F;
               break;
            case 3:
               val = 0.3F;
               break;
            case 4:
               val = 0.8F;
               break;
            case 5:
               val = 0.03F;
               break;
            default:
               throw new RuntimeException("uhoh: " + sst);
         }

         total += val;
      }

      return total;
   }
}
