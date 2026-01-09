package com.tann.dice.util;

import java.util.Random;

public class Maths {
   public static final float TAU = (float) (Math.PI * 2);

   public static int mult(Random r) {
      return r.nextBoolean() ? 1 : -1;
   }

   public static float factor(float arg, Random r) {
      return r.nextFloat() * arg;
   }

   public static float sin(float radians) {
      return (float)Math.sin(radians);
   }

   public static float dist(int x, int y) {
      return (float)Math.sqrt(x * x + y * y);
   }
}
