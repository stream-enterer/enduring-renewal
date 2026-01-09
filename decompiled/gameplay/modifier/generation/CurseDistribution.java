package com.tann.dice.gameplay.modifier.generation;

import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class CurseDistribution {
   public static final int FIRST = getBit(0);
   public static final int FOURTH = getBit(3);
   public static final int BOSSES = getBit(3) + getBit(7) + getBit(11) + getBit(15) + getBit(19);
   public static final int FIRST_TEN = 1023;
   public static final int SECOND_TEN = 1047552;
   public static final int LAST_TWO = 786432;
   public static final int FINAL_BOSS = getBit(19);
   public static final int ALL = 1048575;
   private static int[] ints = new int[]{FIRST, FOURTH, BOSSES, 1023, 1047552, 786432, FINAL_BOSS, 1048575};
   private static final int BIAS = 30;
   private static final int LEVELS = 20;
   private static final float FLAT = 0.0F;
   private static final float ACTIVE_FLAT = 0.5F;
   private static final float MAX_FLAT = 0.5F;
   private static final float ACTIVE_POW = 0.4F;

   private static int getBit(int level) {
      return 1 << level;
   }

   public static List<String> getDebugData() {
      List<String> result = new ArrayList<>();

      for (int anInt : ints) {
         List<Integer> activeLevels = getActiveLevels(anInt);
         String aStr = activeLevels + "";
         if (activeLevels.size() > 1) {
            boolean allInRow = true;

            for (int i = 1; i < activeLevels.size(); i++) {
               if (activeLevels.get(i) != activeLevels.get(i - 1) + 1) {
                  allInRow = false;
               }
            }

            if (allInRow) {
               aStr = activeLevels.get(0) + "-" + activeLevels.get(activeLevels.size() - 1);
            }
         }

         result.add(aStr + ": " + Tann.floatFormat(getMult(anInt)));
      }

      return result;
   }

   private static List<Integer> getActiveLevels(int input) {
      List<Integer> activeLevels = new ArrayList<>();

      for (int i = 0; i < 30; i++) {
         boolean active = (input & 1 << i) > 0;
         if (active) {
            activeLevels.add(i + 1);
         }
      }

      return activeLevels;
   }

   private static float getMultLevelAndAfter(int level) {
      int bit = 1048575 - (getBit(level - 1) - 1);
      return getMult(bit);
   }

   public static float getMultLevelRange(int start, int end) {
      int bit = getBit(end) - 1 - (getBit(start - 1) - 1);
      return getMult(bit);
   }

   public static float getMultLevelAndAfter(int level, float perLevelValue) {
      return getMultLevelAndAfter(level) * perLevelValue;
   }

   public static float getMultLevelUntilEndEnd(int start, int end, int perLevelValue) {
      float result = 0.0F;

      for (int i = start; i <= end; i++) {
         result += getMultUntilEnd(i);
      }

      return result * perLevelValue;
   }

   private static float getMultUntilEnd(int i) {
      return getMultLevelRange(i, 20);
   }

   public static float getMult(int inputBits) {
      check(inputBits);
      int activeScore = 0;
      int maxScore = upTo(30) - upTo(10);
      int actives = 0;

      for (int i = 0; i < 20; i++) {
         boolean active = (inputBits & 1 << i) > 0;
         if (active) {
            activeScore += 30 - i;
            actives++;
         }
      }

      float activeRatio = actives / 20.0F;
      float avgActiveWeighted = (float)activeScore / maxScore;
      float fl = (float)(0.0 + 0.5 * Math.pow(activeRatio, 0.4F));
      return fl + 0.5F * avgActiveWeighted;
   }

   private static void check(int input) {
      for (int i = 20; i <= 31; i++) {
         if ((input & 2 << i) > 0) {
            throw new RuntimeException("bad set?");
         }
      }
   }

   private static int upTo(int max) {
      return triangle(max);
   }

   private static int triangle(int i) {
      return (int)(i * (i / 2.0F + 0.5F));
   }

   public static float getEachLevelAdd(int perLevelValue) {
      float result = 0.0F;

      for (int i = 0; i < 20; i++) {
         result += getMult((1 << i) - 1) * perLevelValue;
      }

      return result;
   }

   public static float getBossLevelsAdd(int perLevelValue) {
      float result = 0.0F;

      for (int i = 0; i < 5; i++) {
         result += getMult((1 << i * 4) - 1) * perLevelValue;
      }

      return result;
   }
}
