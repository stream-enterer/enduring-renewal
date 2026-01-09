package com.tann.dice.test;

import com.tann.dice.gameplay.battleTest.BattleTestUtils;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.level.LevelUtils;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBattleSim {
   static final Difficulty d = Difficulty.Unfair;

   @Test
   @Slow
   public static void battleSimPerformance() {
      long start = System.currentTimeMillis();
      int attempts = 20;

      for (int i = 0; i < 20; i++) {
         List var4 = LevelUtils.generateFor(d);
      }

      long taken = System.currentTimeMillis() - start;
      long takenPer = taken / 20L;
      TannLog.log("Battle sim per full generation: " + takenPer);
      Tann.assertTrue("Should not be too slow " + takenPer, takenPer < 20L);
   }

   @Test
   @Slow
   public static void battleSimSameEnemies() {
      int sames = 0;
      int total = 0;
      int attempts = 20;
      List<MonsterType> culprits = new ArrayList<>();

      for (int attIndex = 0; attIndex < 20; attIndex++) {
         List<Level> levs = LevelUtils.generateFor(d);

         for (int levIndex = 1; levIndex < levs.size(); levIndex++) {
            if (levIndex % 4 != 3) {
               Level l = levs.get(levIndex);
               Level l2 = levs.get(levIndex - 1);
               if (Tann.anySharedItems(l2.getMonsterList(), l.getMonsterList())) {
                  sames++;
                  culprits.addAll(Tann.getSharedItems(l.getMonsterList(), l2.getMonsterList()));
               }

               total++;
            }
         }
      }

      TannLog.log("Levels with a same monster as before: " + sames + "/" + total);
      Map<MonsterType, Integer> cnt = new HashMap<>();

      for (MonsterType culprit : culprits) {
         if (cnt.get(culprit) == null) {
            cnt.put(culprit, 0);
         }

         cnt.put(culprit, 1 + cnt.get(culprit));
      }

      TannLog.log(cnt.toString());
      float r = (float)sames / total;
      Tann.assertTrue("Should not be not too many sames: " + r, r < 0.2F);
   }

   @Test
   @Slow
   public static void battleSimMissingno() {
      int attempts = 100;
      MonsterType err = MonsterTypeLib.byName("oeiurgjtoier");
      List<Level> fails = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         List<Level> levs = LevelUtils.generateFor(d);

         for (int i1 = 0; i1 < levs.size(); i1++) {
            Level l = levs.get(i1);
            if (l.getMonsterList().contains(err)) {
               fails.add(l);
            }
         }
      }

      Tann.assertBads(fails);
   }

   @Test
   @Slow
   public static void battleSimBarrel() {
      int attempts = 100;
      List<Level> fails = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         List<Level> levs = LevelUtils.generateFor(d);

         for (int i1 = 0; i1 < levs.size(); i1++) {
            Level l = levs.get(i1);
            if (!BattleTestUtils.levelValidDebug(l)) {
               fails.add(l);
            }
         }
      }

      Tann.assertBads(fails);
   }
}
