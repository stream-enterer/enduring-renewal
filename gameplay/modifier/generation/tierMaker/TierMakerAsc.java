package com.tann.dice.gameplay.modifier.generation.tierMaker;

public class TierMakerAsc extends TierMakerPreset {
   public TierMakerAsc(int amt) {
      this(amt, false);
   }

   public TierMakerAsc(int amt, boolean negative) {
      super(getTiers(amt, negative));
   }

   private static int[] getTiers(int amt, boolean negative) {
      int[] result = new int[amt];

      for (int i = 0; i < amt; i++) {
         result[i] = (i + 1) * (negative ? -1 : 1);
      }

      return result;
   }
}
