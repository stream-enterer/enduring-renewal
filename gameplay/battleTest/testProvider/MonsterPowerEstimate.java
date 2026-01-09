package com.tann.dice.gameplay.battleTest.testProvider;

import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.battleTest.BattleTest;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import java.util.Arrays;

public class MonsterPowerEstimate implements BattleTestProvider {
   final float hp;
   final float dmg;
   final float mit;
   static final int ITER = 15;

   public MonsterPowerEstimate(float testStrength) {
      this(testStrength, MonsterPowerEstimate.EstimateType.Factors);
   }

   public MonsterPowerEstimate(float testStrength, MonsterPowerEstimate.EstimateType et) {
      float globalScale = 1.0F;
      float scaledStrength = testStrength * 1.0F;
      this.dmg = scaledStrength;
      this.mit = scaledStrength * TierStats.getDefenceRatio();
      switch (et) {
         case Factors:
            this.hp = scaledStrength * 2.5F;
            break;
         case FactorsFiveHP:
            this.hp = Interpolation.linear.apply(5.0F, 50.0F, Math.min(1.0F, testStrength / 50.0F));
            break;
         case FactorsNPlusHp:
            this.hp = scaledStrength * 1.8F + 2.0F;
            break;
         default:
            throw new RuntimeException();
      }
   }

   public MonsterPowerEstimate(float hp, float dmg, float mit) {
      this.hp = hp;
      this.dmg = dmg;
      this.mit = mit;
   }

   @Override
   public float getTotalHealth() {
      return this.hp;
   }

   @Override
   public float getAvgDamage() {
      return this.dmg;
   }

   @Override
   public float getAvgMitigation() {
      return this.mit;
   }

   public static float getValue(MonsterType mt) {
      return getValue(mt, MonsterPowerEstimate.EstimateType.Factors);
   }

   public static float getValue(MonsterType mt, MonsterPowerEstimate.EstimateType et) {
      if (!Float.isNaN(mt.getEffectiveHp()) && !Float.isNaN(mt.getAvgEffectTier(true))) {
         int numToFight = 1;
         MonsterType[] types = new MonsterType[1];
         Arrays.fill(types, mt);
         float lowerBound = -1.0F;
         float upperBound = 200.0F;

         for (int i = 0; i < 15; i++) {
            float toCheck = lowerBound + (upperBound - lowerBound) / 2.0F;
            MonsterPowerEstimate mpe = new MonsterPowerEstimate(toCheck, et);
            BattleTest bt = new BattleTest(mpe, types);
            if (bt.runBattle().isPlayerVictory()) {
               upperBound = toCheck;
            } else {
               lowerBound = toCheck;
            }
         }

         return mt.calcBackRow(1) ? lowerBound * 1.05F : lowerBound;
      } else {
         return Float.NaN;
      }
   }

   public static enum EstimateType {
      Factors,
      FactorsFiveHP,
      FactorsNPlusHp;
   }
}
