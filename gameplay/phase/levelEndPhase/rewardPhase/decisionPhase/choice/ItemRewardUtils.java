package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.EnumChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.OrChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;

public class ItemRewardUtils {
   public static Choosable getFinalReward(int quality, DungeonContext context) {
      List<ItemRewardUtils.SpecialItemRewardType> possibles = new ArrayList<>();

      for (ItemRewardUtils.SpecialItemRewardType value : ItemRewardUtils.SpecialItemRewardType.values()) {
         if (value.validFor(quality, context.getContextConfig().mode)) {
            possibles.add(value);
         }
      }

      return getActual(quality, randomSpecial(possibles));
   }

   private static Choosable getActual(int quality, ItemRewardUtils.SpecialItemRewardType type) {
      switch (type) {
         case RandomRandom:
            List<Choosable> metas = new ArrayList<>();

            for (ItemRewardUtils.SpecialItemRewardType value : ItemRewardUtils.SpecialItemRewardType.values()) {
               if (value != ItemRewardUtils.SpecialItemRewardType.SpecialKeyword7
                  && value != ItemRewardUtils.SpecialItemRewardType.SpecialKeyword1
                  && value != ItemRewardUtils.SpecialItemRewardType.RandomRandom) {
                  metas.add(getActual(quality, value));
               }
            }

            return new OrChoosable(metas);
         case SameTier:
            return new RandomTieredChoosable(quality, 1, ChoosableType.Item);
         case PlusOneMinusOne:
            return new OrChoosable(new RandomTieredChoosable(quality - 1, 1, ChoosableType.Item), new RandomTieredChoosable(quality + 1, 1, ChoosableType.Item));
         case DoubleHalf:
            return new RandomTieredChoosable((quality + 1) / 2, 2, ChoosableType.Item);
         case TripleThird:
            return new RandomTieredChoosable((quality + 1) / 3, 3, ChoosableType.Item);
         case NRandomTierOnes:
            return new RandomTieredChoosable(1, quality, ChoosableType.Item);
         case TwoNRandomTierZeros:
            return new RandomTieredChoosable(0, quality * 2, ChoosableType.Item);
         case SpecialKeyword7:
            return EnumChoosable.RandoKeywordT7Item;
         case SpecialKeyword5:
            return EnumChoosable.RandoKeywordT5Item;
         case SpecialKeyword1:
            return EnumChoosable.RandoKeywordT1Item;
         default:
            throw new RuntimeException("Error getting item reward for :" + type);
      }
   }

   private static ItemRewardUtils.SpecialItemRewardType randomSpecial(List<ItemRewardUtils.SpecialItemRewardType> possibles) {
      float totalChance = 0.0F;

      for (ItemRewardUtils.SpecialItemRewardType sirt : possibles) {
         totalChance += sirt.chance;
      }

      float random = (float)(Math.random() * totalChance);
      if (!Float.isNaN(random) && !(random <= 0.0F)) {
         for (ItemRewardUtils.SpecialItemRewardType sirt : possibles) {
            random -= sirt.chance;
            if (random <= 0.0F) {
               return sirt;
            }
         }

         return possibles.size() == 0 ? ItemRewardUtils.SpecialItemRewardType.SameTier : possibles.get(0);
      } else {
         TannLog.error("randomerr");
         return ItemRewardUtils.SpecialItemRewardType.SameTier;
      }
   }

   public static enum SpecialItemRewardType {
      SameTier(-99, 99, 1.0F),
      PlusOneMinusOne(-5, 98, 0.6F),
      DoubleHalf(2, 99, 0.4F),
      TripleThird(6, 99, 0.2F),
      NRandomTierOnes(3, 13, 0.03F),
      SpecialKeyword7(6, 7, 0.02F),
      SpecialKeyword5(3, 4, 0.02F),
      SpecialKeyword1(1, 2, 0.02F),
      TwoNRandomTierZeros(1, 5, 0.01F),
      RandomRandom(3, 99, 0.005F);

      final int minQuality;
      final int maxQuality;
      final float chance;

      private SpecialItemRewardType(int minQuality, int maxQuality, float chance) {
         this.minQuality = minQuality;
         this.maxQuality = maxQuality;
         this.chance = chance;
      }

      public boolean validFor(int quality, Mode mode) {
         return quality >= this.minQuality
            && quality <= this.maxQuality
            && (!UnUtil.isLocked(Feature.ALTERNATE_RANDOM_ITEMS) || this.chance > 0.7F)
            && (!UnUtil.isLocked(Feature.WEIRD_RANDOM_ITEMS) || this.chance > 0.1F);
      }
   }
}
