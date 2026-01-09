package com.tann.dice.gameplay.battleTest.testProvider;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.util.TannLog;

public class TierStats implements BattleTestProvider {
   float avgSingleHeroHealth;
   float avgDamage;
   float avgMitigation;
   public final Difficulty difficulty;
   public final int playerTier;
   static final float buffer = 1.5F;
   static final float adjustedBuffer = 0.3F;

   public TierStats(int levelIndex, Difficulty difficulty) {
      this.playerTier = levelIndex;
      this.difficulty = difficulty;
      this.avgSingleHeroHealth = getTierHp(levelIndex);
      this.avgDamage = getStrength(levelIndex);
      this.avgMitigation = this.avgDamage * getDefenceRatio();
   }

   public static float getLevelRatio(int currentLevelNumber) {
      return getStrength(currentLevelNumber) / getStrength(1);
   }

   private static float getStrength(int levelIndex) {
      switch (levelIndex) {
         case 0:
            TannLog.error("level index 0");
            return 1.0F;
         case 1:
            return 2.7F;
         case 2:
            return 3.1F;
         case 3:
            return 3.3F;
         case 4:
            return 4.6F;
         case 5:
            return 3.7F;
         case 6:
            return 4.4F;
         case 7:
            return 5.1F;
         case 8:
            return 6.2F;
         case 9:
            return 5.8F;
         case 10:
            return 6.5F;
         case 11:
            return 7.0F;
         case 12:
            return 8.1F;
         case 13:
            return 7.25F;
         case 14:
            return 8.2F;
         case 15:
            return 9.05F;
         case 16:
            return 10.5F;
         case 17:
            return 10.2F;
         case 18:
            return 11.3F;
         case 19:
            return 12.5F;
         case 20:
            return 18.8F;
         default:
            return (float)(getStrength(20) * 0.9F * Math.pow(2.0, (levelIndex - 20) * 0.3F));
      }
   }

   public static float getDefenceRatio() {
      return 0.82F;
   }

   private static float getTierHp(int levelIndex) {
      int t1 = Math.min(2, levelIndex / 10);
      int t2 = Math.min(2, levelIndex / 10 + 1);
      return getLevelHp(t1) + (getLevelHp(t2) - getLevelHp(t1)) * (levelIndex % 10 / 10.0F);
   }

   private static float getLevelHp(int level) {
      switch (level) {
         case 0:
            return 4.4F;
         case 1:
            return 7.65F;
         case 2:
            return 9.73F;
         default:
            throw new RuntimeException("no hp define for level " + level);
      }
   }

   public float getAvgSingleHeroHealth() {
      return this.avgSingleHeroHealth;
   }

   @Override
   public float getTotalHealth() {
      return this.getAvgSingleHeroHealth() * 5.0F;
   }

   @Override
   public float getAvgDamage() {
      return this.avgDamage;
   }

   @Override
   public float getAvgMitigation() {
      return this.avgMitigation;
   }

   public static float getLivingHeroesMultiplier(float heroDamageTakenRatio) {
      float adjusted = Math.max(0.0F, heroDamageTakenRatio - 0.3F);
      float deads = adjusted / 0.7F;
      return 1.0F - deads;
   }

   @Override
   public String toString() {
      return this.playerTier + ":" + this.difficulty;
   }
}
