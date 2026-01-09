package com.tann.dice.util;

import java.util.Arrays;
import java.util.List;

public class NDimension {
   final float[] vals;

   public NDimension(float[] vals) {
      this.vals = vals;
   }

   float dist(NDimension other) {
      if (other.numDimensions() != this.numDimensions()) {
         throw new RuntimeException("Different dimension numbers");
      } else {
         float totalDiff = 0.0F;

         for (int i = 0; i < this.vals.length; i++) {
            float diff = other.vals[i] - this.vals[i];
            totalDiff += diff * diff;
         }

         return (float)Math.sqrt(totalDiff);
      }
   }

   private int numDimensions() {
      return this.vals.length;
   }

   public float getMinDist(List<NDimension> others) {
      float min = Float.MAX_VALUE;

      for (int i = 0; i < others.size(); i++) {
         NDimension d = others.get(i);
         min = Math.min(min, this.dist(d));
      }

      return min;
   }

   @Override
   public String toString() {
      float total = 0.0F;

      for (int i = 0; i < this.vals.length; i++) {
         float f = this.vals[i];
         total += f;
      }

      return total + ":" + Arrays.toString(this.vals);
   }
}
