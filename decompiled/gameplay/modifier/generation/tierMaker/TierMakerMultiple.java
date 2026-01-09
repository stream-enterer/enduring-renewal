package com.tann.dice.gameplay.modifier.generation.tierMaker;

public class TierMakerMultiple extends TierMakerSet {
   final float mult;
   private final int amt;

   public TierMakerMultiple(float mult, int amt) {
      this.mult = mult;
      this.amt = amt;
   }

   @Override
   public float makeTier(int i) {
      return (i + 1) * this.mult;
   }

   @Override
   public int num() {
      return this.amt;
   }
}
