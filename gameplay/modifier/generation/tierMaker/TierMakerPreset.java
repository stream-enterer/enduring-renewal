package com.tann.dice.gameplay.modifier.generation.tierMaker;

public class TierMakerPreset extends TierMakerSet {
   final float[] tiers;

   public TierMakerPreset(float... tiers) {
      this.tiers = tiers;
   }

   public TierMakerPreset(int... tiers) {
      float[] rs = new float[tiers.length];

      for (int index = 0; index < tiers.length; index++) {
         rs[index] = tiers[index];
      }

      this.tiers = rs;
   }

   @Override
   public float makeTier(int index) {
      return this.tiers[index];
   }

   @Override
   public int num() {
      return this.tiers.length;
   }
}
