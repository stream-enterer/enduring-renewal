package com.tann.dice.gameplay.modifier;

import com.tann.dice.util.Tann;
import java.util.List;

public class PermStats {
   final int validCombinations;
   final boolean usedAll;
   final boolean noMandatories;

   public PermStats(int validCombinations, boolean usedAll, boolean noMandatories) {
      this.validCombinations = validCombinations;
      this.usedAll = usedAll;
      this.noMandatories = noMandatories;
   }

   public static int COMPARE(PermStats offer1Stats, PermStats offer2Stats) {
      if (offer1Stats.usedAll != offer2Stats.usedAll) {
         return offer1Stats.usedAll ? -1 : 1;
      } else if (offer1Stats.noMandatories != offer2Stats.noMandatories) {
         return offer1Stats.noMandatories ? -1 : 1;
      } else {
         return offer2Stats.validCombinations - offer1Stats.validCombinations;
      }
   }

   public boolean isFine() {
      return this.usedAll && this.noMandatories && this.validCombinations >= 6;
   }

   @Override
   public String toString() {
      return this.usedAll + ":" + this.noMandatories + ":" + this.validCombinations;
   }

   public static PermStats make(int target, List<Modifier> modifiers) {
      int validCombinations = 0;
      int usedModifiers = 0;
      int mask = (1 << modifiers.size()) - 1;
      int cannotDoWithout = mask;
      if (modifiers.size() > 12) {
         int total = 0;
         int totalNegative = 0;
         int totalPositive = 0;
         int biggestPositive = 0;
         int biggestNegative = 5000;

         for (Modifier modifier : modifiers) {
            int tier = modifier.getTier();
            if (tier > 0) {
               totalPositive += tier;
            } else {
               totalNegative += tier;
            }

            biggestNegative = Math.min(biggestNegative, tier);
            biggestPositive = Math.max(biggestPositive, tier);
            total += modifier.getTier();
         }

         if (Tann.further(total, target * 2) && biggestNegative + totalPositive >= target && biggestPositive + totalNegative <= target) {
            validCombinations = 20;
            usedModifiers = mask;
            cannotDoWithout = 0;
         }
      } else {
         for (int bitmap = 0; bitmap <= mask; bitmap++) {
            int total = 0;

            for (int j = 0; j < modifiers.size(); j++) {
               if ((bitmap & 1 << j) != 0) {
                  total += modifiers.get(j).getTier();
               }
            }

            if (total == target) {
               validCombinations++;
               usedModifiers |= bitmap;
               cannotDoWithout &= bitmap;
            }
         }
      }

      return new PermStats(validCombinations, usedModifiers == mask, cannotDoWithout == 0);
   }
}
