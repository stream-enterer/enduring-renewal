package com.tann.dice.gameplay.battleTest;

import com.tann.dice.gameplay.battleTest.testProvider.BattleTestProvider;
import com.tann.dice.gameplay.battleTest.testProvider.TierStats;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleTest {
   static final boolean print = false;
   BattleTestProvider tierStats;
   static final List<MonsterType> types = new ArrayList<>();
   static List<MonsterType> attackers = new ArrayList<>();
   private static final boolean partialDamageCountsAsReduction = true;

   public BattleTest(int playerTier, Difficulty difficulty, MonsterType... types) {
      this(new TierStats(playerTier, difficulty), types);
   }

   public BattleTest(BattleTestProvider btp, MonsterType... types) {
      this(btp, Arrays.asList(types));
   }

   public BattleTest(BattleTestProvider btp, List<MonsterType> types) {
      this.setup(btp, types);
   }

   void setup(BattleTestProvider ts, List<MonsterType> types) {
      BattleTest.types.clear();
      BattleTest.types.addAll(types);
      this.tierStats = ts;
   }

   public BattleResult runBattle() {
      float remainingMonsterHp = 0.0F;

      for (int i = 0; i < types.size(); i++) {
         MonsterType t = types.get(i);
         remainingMonsterHp += t.getEffectiveHp();
      }

      float startingMonsterHp = remainingMonsterHp;
      float totalDamageToPlayer = 0.0F;
      float unspentPlayerDamageCounter = 0.0F;
      int turn = 0;
      int MAX_TURNS = 20;

      while (types.size() > 0) {
         float livingHeroesMult = TierStats.getLivingHeroesMultiplier(totalDamageToPlayer / this.tierStats.getTotalHealth());
         if (livingHeroesMult <= 0.0F) {
            return new BattleResult(false, 1.0F - remainingMonsterHp / startingMonsterHp);
         }

         float playerDmgOutput = this.tierStats.getAvgDamage() * livingHeroesMult;
         remainingMonsterHp -= playerDmgOutput;
         unspentPlayerDamageCounter += playerDmgOutput;
         playerDmgOutput = this.tierStats.getAvgMitigation() * livingHeroesMult;
         List<MonsterType> attackingMonsters = this.getAttackingMonsters();

         while (unspentPlayerDamageCounter > 0.0F && types.size() > 0 && attackingMonsters.size() > 0) {
            MonsterType bestTarget = getBestTarget(attackingMonsters, turn);
            if (bestTarget == null) {
               break;
            }

            float bestTargetHp = bestTarget.getEffectiveHp();
            if (!(unspentPlayerDamageCounter >= bestTargetHp)) {
               break;
            }

            unspentPlayerDamageCounter -= bestTargetHp;
            types.remove(bestTarget);
            attackingMonsters.remove(bestTarget);
         }

         if (types.size() > 0) {
            float enemyDamage = this.calculateEnemyDamage(attackingMonsters, unspentPlayerDamageCounter, getBestTarget(attackingMonsters, turn));
            float actualDamage = Math.max(0.0F, enemyDamage - playerDmgOutput);
            totalDamageToPlayer += Math.max(0.0F, actualDamage);
         }

         if (++turn >= 20) {
            return new BattleResult(false, -1.0F);
         }
      }

      return new BattleResult(true, totalDamageToPlayer / this.tierStats.getTotalHealth());
   }

   private List<MonsterType> getAttackingMonsters() {
      attackers.clear();
      int totalFullness = 0;

      for (int i = 0; i < types.size(); i++) {
         MonsterType type = types.get(i);
         totalFullness += type.size.getReinforceSize();
         if (totalFullness > 165) {
            break;
         }

         attackers.add(type);
      }

      return attackers;
   }

   @Override
   public String toString() {
      String result = "Battletest";

      for (MonsterType mt : types) {
         result = result + mt.getName(false) + ",";
      }

      return result;
   }

   private float calculateEnemyDamage(List<MonsterType> attackingMonsters, float unspentDmg, MonsterType bestTarget) {
      float total = 0.0F;
      int i = 0;

      for (int attackingMonstersSize = attackingMonsters.size(); i < attackingMonstersSize; i++) {
         MonsterType mt = attackingMonsters.get(i);
         total += mt.getAvgEffectTier();
      }

      if (bestTarget != null) {
         if (unspentDmg > bestTarget.getEffectiveHp() * 0.9F) {
            float ratio = 0.75F;
            total -= bestTarget.getAvgEffectTier() * ratio;
         } else if (unspentDmg > bestTarget.getEffectiveHp() * 0.8) {
            float ratio = 0.4F;
            total -= bestTarget.getAvgEffectTier() * ratio;
         }
      }

      return total;
   }

   private static MonsterType getBestTarget(List<MonsterType> attackingMonsters, int turn) {
      MonsterType mt = getBestTarget(attackingMonsters, false, turn);
      if (mt != null) {
         return mt;
      } else {
         mt = getBestTarget(attackingMonsters, true, turn);
         if (mt != null) {
            return mt;
         } else {
            return attackingMonsters.size() > 0 ? attackingMonsters.get(0) : null;
         }
      }
   }

   private static MonsterType getBestTarget(List<MonsterType> attackingMonsters, boolean ranged, int turn) {
      float bestRatio = -500.0F;
      MonsterType best = null;

      for (int i = 0; i < attackingMonsters.size(); i++) {
         MonsterType t = attackingMonsters.get(i);
         if (ranged || !t.calcBackRow(turn)) {
            float ratio = t.getBattleTestRatio();
            if (ratio > bestRatio) {
               bestRatio = ratio;
               best = t;
            }
         }
      }

      return best;
   }
}
