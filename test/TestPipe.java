package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestPipe {
   @Test
   public static void modifierPipeValidation() {
      int amtPer = 100;
      List<String> bads = new ArrayList<>();

      for (Pipe<Modifier> pipe : PipeMod.pipes) {
         if (!pipe.isTransformative()) {
            for (Modifier mod : pipe.examples(100)) {
               Modifier n = pipe.get(mod.getName());
               if (n == null || !n.getName().equalsIgnoreCase(mod.getName())) {
                  bads.add(pipe.getClass().getSimpleName() + ":" + mod.getName());
               }
            }
         }
      }

      Tann.assertTrue("Should be no bads; " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void monsterPipeValidation() {
      int amtPer = 10;
      List<String> bads = new ArrayList<>();

      for (Pipe<MonsterType> pipe : PipeMonster.pipes) {
         if (!pipe.isTransformative()) {
            for (MonsterType mod : pipe.examples(10)) {
               MonsterType n = PipeMonster.fetch(mod.getName());
               if (n.isMissingno() || !n.getName().equalsIgnoreCase(mod.getName())) {
                  bads.add(pipe.getClass().getSimpleName() + ":" + mod.getName());
               }
            }
         }
      }

      Tann.assertTrue("Should be no bads; " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void heroPipeValidation() {
      int amtPer = 100;
      List<String> bads = new ArrayList<>();

      for (Pipe<HeroType> pipe : PipeHero.pipes) {
         if (!pipe.isTransformative()) {
            for (HeroType mod : pipe.examples(100)) {
               HeroType n = PipeHero.fetch(mod.getName());
               if (n.isMissingno() || !n.getName().equalsIgnoreCase(mod.getName())) {
                  bads.add(pipe.getClass().getSimpleName() + ":" + mod.getName());
               }
            }
         }
      }

      Tann.assertTrue("Should be no bads; " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void itemPipeValidation() {
      int amtPer = 100;
      List<String> bads = new ArrayList<>();

      for (Pipe<Item> pipe : PipeItem.pipes) {
         if (!pipe.isTransformative()) {
            for (Item mod : pipe.examples(100)) {
               Item n = PipeItem.fetch(mod.getName());
               if (n == null || !n.getName().equalsIgnoreCase(mod.getName())) {
                  bads.add(pipe.getClass().getSimpleName() + ":" + mod.getName());
               }
            }
         }
      }

      Tann.assertTrue("Should be no bads; " + bads, bads.isEmpty());
   }

   @Test
   @Slow
   public static void pipePerformance() {
      int amtPer = 20;
      List<TP<Pipe, Long>> results = new ArrayList<>();

      for (Pipe pipe : getForTest()) {
         long t = System.currentTimeMillis();
         int genned = 0;

         while (genned < 20) {
            List<Modifier> g = pipe.examples(20);
            if (g.size() == 0) {
               break;
            }

            genned += g.size();
         }

         long d = System.currentTimeMillis() - t;
         results.add(new TP<>(pipe, d));
      }

      Collections.sort(results, new Comparator<TP<Pipe, Long>>() {
         public int compare(TP<Pipe, Long> o1, TP<Pipe, Long> o2) {
            return Long.compare(o2.b, o1.b);
         }
      });
      System.out.println(results);
      TP<Pipe, Long> worst = results.get(0);
      int cutoffMS = 400;
      Tann.assertTrue("Should be none longer than cutoff: " + results, worst.b < 400L);
   }

   private static List<Pipe> getForTest() {
      List<Pipe> all = Pipe.makeAllPipes();

      for (int i = all.size() - 1; i >= 0; i--) {
         if (all.get(i).isSlow()) {
            all.remove(i);
         }
      }

      return all;
   }

   @Test
   public static void pipePerformanceUsedForGenerate() {
      int amtPer = 20;
      List<TP<Pipe, Long>> results = new ArrayList<>();
      List<Pipe> bads = new ArrayList<>();

      for (Pipe pipe : getForTest()) {
         for (boolean wild : Tann.BOTH) {
            if (pipe.canGenerate(wild)) {
               long t = System.currentTimeMillis();
               int made = 0;

               for (int i = 0; i < 20; i++) {
                  Object g = pipe.generate(wild);
                  if (g != null) {
                     made++;
                  }
               }

               if (made < 2) {
                  bads.add(pipe);
               }

               long d = System.currentTimeMillis() - t;
               results.add(new TP<>(pipe, d));
            }
         }
      }

      Collections.sort(results, new Comparator<TP<Pipe, Long>>() {
         public int compare(TP<Pipe, Long> o1, TP<Pipe, Long> o2) {
            return Long.compare(o2.b, o1.b);
         }
      });
      System.out.println(results);
      TP<Pipe, Long> worst = results.get(0);
      int cutoffMS = 150;
      Tann.assertTrue("Should be no bads: " + bads, bads.isEmpty());
      Tann.assertTrue("Should be none longer than cutoff ms: " + results, worst.b < 150L);
   }

   @Test
   public static void testGeneratedMonsters() {
      int tests = 1000;
      List<MonsterType> fails = new ArrayList<>();

      for (int i = 0; i < tests; i++) {
         MonsterType mt = PipeMonster.makeGen();
         if (mt.isMissingno() || Float.isNaN(mt.getEffectiveHp()) || Float.isNaN(mt.getOldSummonValue()) || Float.isNaN(mt.getSummonValue())) {
            fails.add(mt);
         }
      }

      Tann.assertTrue("" + fails, fails.isEmpty());
   }

   @Test
   public static void sidesAPI() {
      List<Object> bad = new ArrayList<>();

      for (Object s : EntSidesLib.getSizedSides(EntSize.reg)) {
         if (!(s instanceof EntSide) && !(s instanceof EnSiBi)) {
            bad.add(s);
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   @Slow
   public static void textmodDataIntegrity() {
      List<Pipe> bad = new ArrayList<>();
      String base = DebugUtilsUseful.getAllStrings();

      for (Pipe pipe : getForTest()) {
         pipe.examples(50);
         String test = DebugUtilsUseful.getAllStrings();
         if (!test.equals(base)) {
            bad.add(pipe);
            base = test;
         }
      }

      Tann.assertBads(bad);
   }

   public static List<Modifier> randomMods(int amtPer) {
      List<Modifier> mods = new ArrayList<>();

      for (Pipe<Modifier> pipe : PipeMod.pipes) {
         if (!pipe.isTransformative()) {
            mods.addAll(pipe.examples(amtPer));
         }
      }

      return mods;
   }
}
