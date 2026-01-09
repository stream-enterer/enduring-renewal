package com.tann.dice.gameplay.battleTest;

import com.tann.dice.gameplay.battleTest.template.LevelTemplate;
import com.tann.dice.gameplay.battleTest.testProvider.TierStats;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.util.NDimension;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleTestUtils {
   public static final int MAX_DIMENSION_LOOKBACK = 2;
   public static final int MAX_ENEMIES = 8;
   public static final int MAX_ENEMIES_SINGLE = 12;
   public static final int ATTEMPTS = 40;
   public static final int BOSS_ATTEMPTS = 6;
   public static final float TARGET_HEALTH_LOST_RATIO = 0.45F;
   public static final float MIN_HEALTH_LOST_RATIO = 0.14999999F;
   public static final float ACCEPTED_HP_DIFF = 0.08F;
   private static MonsterType barrel;
   private static MonsterType chief;
   private static MonsterType bandit;
   private static MonsterType slimer;
   private static MonsterType slate;
   private static MonsterType log;
   private static MonsterType goblin;
   static Map<MonsterType, Integer> map = new HashMap<>();
   static List<MonsterType> allMonsters = MonsterTypeLib.getMasterCopy();

   public static Level generateStdLevel(Zone zone, TierStats ts, List<NDimension> closeLevels, boolean printInfo, DungeonContext dc) throws NoLevelGeneratedException {
      float rarityRandom = Tann.random();
      List<TP<Float, List<MonsterType>>> result = new ArrayList<>();

      for (int i = 0; i < 40; i++) {
         LevelTemplate zoneTemplate = new LevelTemplate(zone, ts.difficulty, ts.playerTier, rarityRandom, dc.getModifierGlobalsIncludingLinked());
         TP<Float, List<MonsterType>> t = generateMonsterList(zoneTemplate, ts, 1, printInfo);
         if (t != null) {
            result.add(t);
         }
      }

      return generateFromResults(result, closeLevels);
   }

   public static Level generateBossLevel(LevelTemplate template, TierStats ts, List<NDimension> closeLevels, boolean printInfo) throws NoLevelGeneratedException {
      List<TP<Float, List<MonsterType>>> result = new ArrayList<>();

      for (int i = 0; i < 6; i++) {
         template.resetExtras();
         TP<Float, List<MonsterType>> t = generateMonsterList(template, ts, 1, printInfo);
         if (t != null) {
            result.add(t);
         }
      }

      return generateFromResults(result, closeLevels);
   }

   private static Level generateFromResults(List<TP<Float, List<MonsterType>>> result, final List<NDimension> closeLevels) throws NoLevelGeneratedException {
      if (result.size() == 0) {
         TannLog.log("Failed to generate level");
         throw new NoLevelGeneratedException();
      } else {
         List<Integer> seen = new ArrayList<>();

         for (int ri = result.size() - 1; ri >= 0; ri--) {
            int hash = 0;
            List<MonsterType> list = (List<MonsterType>)result.get(ri).b;

            for (int mi = 0; mi < list.size(); mi++) {
               hash += list.get(mi).hashCode();
            }

            if (seen.contains(hash)) {
               result.remove(ri);
            } else {
               seen.add(hash);
            }
         }

         Collections.sort(
            result,
            new Comparator<TP<Float, List<MonsterType>>>() {
               public int compare(TP<Float, List<MonsterType>> o1, TP<Float, List<MonsterType>> o2) {
                  boolean o1ok = Math.abs(o1.a) < 0.08F;
                  boolean o2ok = Math.abs(o2.a) < 0.08F;
                  if (closeLevels.size() > 0 && o1ok && o2ok) {
                     return (int)(
                        -Math.signum(BattleTestUtils.fromTypeList(o1.b).getMinDist(closeLevels) - BattleTestUtils.fromTypeList(o2.b).getMinDist(closeLevels))
                     );
                  } else {
                     return o1ok && o2ok ? 0 : (int)Math.signum(Math.abs(o1.a) - Math.abs(o2.a));
                  }
               }
            }
         );
         TP<Float, List<MonsterType>> chosen = result.get(0);
         return new Level(chosen.a, chosen.b);
      }
   }

   private static TP<Float, List<MonsterType>> generateMonsterList(LevelTemplate template, TierStats ts, int attempts, boolean printInfo) {
      if (printInfo) {
         System.out.println("generating fights for tier " + ts + "(" + attempts + " attempts)");
      }

      boolean single = template.getInitialSetup().size() + template.getExtrasList().size() == 1;
      int maxMonsters = single ? 12 : 8;
      long t = System.currentTimeMillis();
      List<MonsterType> best = new ArrayList<>();
      float bestDifficulty = 500000.0F;
      float bestDifficultyDiff = 50000.0F;
      BattleTest bt = new BattleTest(ts, template.getInitialSetup());

      for (int i = 0; i < attempts; i++) {
         List<MonsterType> types = template.getInitialSetup();
         clearLimits(types);

         while (types.size() <= maxMonsters) {
            bt.setup(ts, types);
            BattleResult br = bt.runBattle();
            if (!br.playerVictory) {
               break;
            }

            float healthLost = br.damageTaken;
            float healthLossDiff = healthLost - 0.45F;
            if (healthLost >= 0.14999999F && Math.abs(healthLossDiff) < Math.abs(bestDifficultyDiff) && monstersValid(types)) {
               bestDifficultyDiff = healthLossDiff;
               bestDifficulty = healthLost;
               best.clear();
               best.addAll(types);
            }

            if (healthLost > 0.45F) {
               break;
            }

            MonsterType newMon = template.getExtra();
            types.add(newMon);
            if (addMonsterExceedLimits(newMon)) {
               break;
            }
         }
      }

      if (best.isEmpty()) {
         if (printInfo) {
            System.err.println("Failed to generate level for tier " + ts + ", template: " + template);
         }

         return null;
      } else {
         long taken = System.currentTimeMillis() - t;
         float per = (float)taken / attempts;
         if (printInfo) {
            System.out.println("target: 0.45");
            System.out.println(bestDifficulty + ":" + best);
            System.out.println("avg ms:" + per + ", total:" + taken);
         }

         return new TP<>(bestDifficultyDiff, best);
      }
   }

   public static void init() {
      barrel = MonsterTypeLib.byName("barrel");
      chief = MonsterTypeLib.byName("warchief");
      bandit = MonsterTypeLib.byName("bandit");
      slimer = MonsterTypeLib.byName("slimer");
      slate = MonsterTypeLib.byName("slate");
      log = MonsterTypeLib.byName("log");
      goblin = MonsterTypeLib.byName("goblin");
   }

   private static void clearLimits(List<MonsterType> types) {
      map.clear();

      for (int i = 0; i < types.size(); i++) {
         addMonsterExceedLimits(types.get(i));
      }
   }

   private static boolean addMonsterExceedLimits(MonsterType type) {
      Integer v = map.get(type);
      if (v == null) {
         v = 0;
      }

      v = v + 1;
      map.put(type, v);
      return v > type.getMaxInFight();
   }

   public static boolean levelValidDebug(Level l) {
      return monstersValid(l.getMonsterList());
   }

   private static boolean monstersValid(List<MonsterType> monsters) {
      if (monsters.size() == 0) {
         return false;
      } else if (monsters.get(0) == barrel || monsters.get(monsters.size() - 1) == barrel) {
         return false;
      } else if (monsters.contains(chief) && monsters.size() <= 2) {
         return false;
      } else {
         if (monsters.contains(bandit)) {
            if (monsters.contains(slate)) {
               return false;
            }

            if (monsters.contains(slimer)) {
               return false;
            }
         }

         if (monsters.contains(log) && monsters.contains(goblin)) {
            return false;
         } else {
            boolean allRanged = true;

            for (int i = 0; i < monsters.size(); i++) {
               MonsterType mt = monsters.get(i);
               if (!mt.calcBackRow(0)) {
                  allRanged = false;
                  break;
               }
            }

            return !allRanged;
         }
      }
   }

   public static NDimension fromTypeList(List<MonsterType> types) {
      int extraDimensions = 2;
      float[] data = new float[extraDimensions + allMonsters.size()];
      data[0] = types.size() * 0.1F;
      data[1] = Tann.countDistinct(types) * 0.02F;

      for (int i = 0; i < types.size(); i++) {
         MonsterType type = types.get(i);
         if (!type.isGenerated() && !type.getName(false).contains(".")) {
            int index = allMonsters.indexOf(type);
            if (index == -1) {
               TannLog.log("Weird dimension error: " + type);
            } else {
               data[index + extraDimensions] = 1.0F;
            }
         }
      }

      return new NDimension(data);
   }
}
