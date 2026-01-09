package com.tann.dice.test;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterGenerated;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterTraited;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemGenerated;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeModRandom;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.SimpleCommand;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.SpeechGarbler;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestScattershot {
   private static HeroType[] defaultHeroes = new HeroType[]{
      HeroTypeUtils.byName("Healer"),
      HeroTypeUtils.byName("Defender"),
      HeroTypeUtils.byName("Fighter"),
      HeroTypeUtils.byName("Thief"),
      HeroTypeUtils.byName("Mage")
   };
   private static MonsterType[] defaultMonsters = new MonsterType[]{
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin"),
      MonsterTypeLib.byName("testGoblin")
   };

   @Test
   @Slow
   public static void testItemCombinations() {
      int numTests = 1000;

      for (int i = 0; i < numTests; i++) {
         if (i % 100 == 0) {
            System.out.println("Item Scattershot: " + i + " (" + (float)i / numTests + ")");
         }

         checkRandomItemLoadout(ItemLib.getMasterCopy());
      }
   }

   @Test
   @Slow
   public static void testGeneratedModifierRender() {
      Batch batch = new SpriteBatch();
      batch.begin();
      List<Modifier> bads = new ArrayList<>();

      for (Pipe<Modifier> pipe : PipeMod.pipes) {
         for (Modifier example : pipe.examples(50)) {
            try {
               Actor a = example.makeChoosableActor(true, 0);
               a.draw(batch, 0.0F);
            } catch (Exception var7) {
               var7.printStackTrace();
               bads.add(example);
            }
         }
      }

      batch.end();
      batch.dispose();
      Tann.assertBads(bads);
   }

   @Test
   @Slow
   public static void testGeneratedItemCombinations() {
      int numTests = 1000;
      List<Item> randoms = new ArrayList<>();
      Pipe<Item> pi = new PipeItemGenerated();

      for (int i = 0; i < 100; i++) {
         randoms.add(pi.example());
      }

      for (int i = 0; i < numTests; i++) {
         if (i % 100 == 0) {
            System.out.println("Item Scattershot: " + i + " (" + (float)i / numTests + ")");
         }

         checkRandomItemLoadout(randoms);
      }
   }

   @Test
   @Slow
   public static void designedItems() {
      testRender(ItemLib.getMasterCopy());
   }

   @Test
   @Slow
   public static void generatedItems() {
      if (!FontWrapper.getFont().isHDFont()) {
         testRender(new PipeItemGenerated().examples(2000));
      }
   }

   @Test
   @Slow
   public static void designedModifiers() {
      testRender(ModifierLib.getAll());
   }

   @Test
   @Slow
   public static void generatedModifiers() {
      if (!FontWrapper.getFont().isHDFont()) {
         testRender(new PipeModRandom().examples(2000));
      }
   }

   private static void testRender(List<? extends Choosable> items) {
      Batch batch = new SpriteBatch();
      batch.begin();
      List<Choosable> bads = new ArrayList<>();

      for (Choosable i : items) {
         try {
            String desc = i.describe();
            desc.length();

            for (boolean b : Tann.BOTH) {
               Actor a = i.makeChoosableActor(b, 0);
               a.draw(batch, 1.0F);
            }
         } catch (Throwable var11) {
            bads.add(i);
         }
      }

      batch.dispose();
      Tann.assertTrue("Should be no bads: " + bads, bads.isEmpty());
   }

   private static void checkRandomItemLoadout(List<Item> source) {
      List<Item> list = Tann.pickNRandomElements(source, 10);

      try {
         testSingleItemLoadout(list);
      } catch (Throwable var7) {
         System.out.println("Error with " + list);
         var7.printStackTrace();
         System.out.println("Starting full test");
         List<Item> tmp = new ArrayList<>(list);

         for (int i = 0; i < 10; i++) {
            tmp.set(i, null);

            try {
               testSingleItemLoadout(tmp);
               tmp.set(i, list.get(i));
            } catch (Throwable var6) {
            }
         }

         throw new RuntimeException("Final minimal item loadout to trigger crash: " + tmp);
      }
   }

   private static void testSingleItemLoadout(List<Item> itemList) {
      List<Hero> heroList = new ArrayList<>();
      List<Monster> monsters = new ArrayList<>();

      for (HeroType ht : defaultHeroes) {
         heroList.add(new Hero(ht));
      }

      for (MonsterType mt : defaultMonsters) {
         monsters.add(new Monster(mt));
      }

      for (int i = 0; i < itemList.size(); i++) {
         if (itemList.get(i) != null) {
            heroList.get(i / 2).addItem(itemList.get(i));
         }
      }

      FightLog f = TestUtils.setupFight(heroList, monsters, new Modifier[0]);

      for (int heroIndex = 0; heroIndex < 5; heroIndex++) {
         Hero h = heroList.get(heroIndex);

         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            EntSide es = h.getSides()[sideIndex];
            Snapshot present = f.getSnapshot(FightLog.Temporality.Present);
            if (present.isVictory() || present.isLoss()) {
               return;
            }

            EntSideState ess = present.getState(h).getSideState(es);
            Eff first = ess.getCalculatedEffect();
            Ent target = null;
            if (first.needsTarget()) {
               target = Tann.random(f.getSnapshot(FightLog.Temporality.Present).getAliveEntities(first.isFriendly()));
            }

            TestUtils.hit(f, h, target, es, false);
         }
      }
   }

   @Test
   @Slow
   public static void testAllMonsters() {
      for (MonsterType monsterType : MonsterTypeLib.getMasterCopy()) {
         testMonsters(Arrays.asList(monsterType.makeEnt()));
      }
   }

   @Test
   @Slow
   public static void testGenMonsters() {
      for (MonsterType monsterType : new PipeMonsterGenerated().examples(50)) {
         testMonsters(Arrays.asList(monsterType.makeEnt()));
      }

      for (MonsterType monsterType : new PipeMonsterTraited().examples(150)) {
         if (monsterType != null) {
            testMonsters(Arrays.asList(monsterType.makeEnt()));
         }
      }
   }

   private static void testMonsters(List<Monster> monsters) {
      List<Hero> heroList = new ArrayList<>();

      for (HeroType ht : defaultHeroes) {
         heroList.add(new Hero(ht));
      }

      FightLog f = TestUtils.setupFight(heroList, monsters, new Modifier[0]);

      for (int heroIndex = 0; heroIndex < 5; heroIndex++) {
         Hero h = heroList.get(heroIndex);

         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            EntSide es = h.getSides()[sideIndex];
            Snapshot present = f.getSnapshot(FightLog.Temporality.Present);
            if (present.isVictory()) {
               return;
            }

            EntSideState ess = present.getState(h).getSideState(es);
            Eff first = ess.getCalculatedEffect();
            Ent target = null;
            if (first.needsTarget()) {
               target = Tann.random(f.getSnapshot(FightLog.Temporality.Present).getAliveEntities(first.isFriendly()));
            }

            TestUtils.hit(f, h, target, es, false);
         }
      }
   }

   @Test
   @Slow
   public static void testModifierCombinations() {
      int numTests = 500;
      List<String> bad = new ArrayList<>();

      for (int i = 0; i < numTests; i++) {
         if (i % 100 == 0) {
            System.out.println("Modifier Scattershot: " + i + " (" + (float)i / numTests + ")");
         }

         bad.addAll(checkModifierLoadout(Tann.pickNRandomElements(ModifierLib.getAll(), 50)));
      }

      Tann.assertBads(bad);
   }

   @Test
   @Slow
   public static void testModifierCombinationsGenerated() {
      List<String> bad = new ArrayList<>();
      int numTests = 500;

      for (int i = 0; i < numTests; i++) {
         if (i % 100 == 0) {
            System.out.println("Textmod Modifier Scattershot: " + i + " (" + (float)i / numTests + ")");
         }

         bad.addAll(checkModifierLoadout(TestPipe.randomMods(1)));
      }

      Tann.assertBads(bad);
   }

   private static List<String> checkModifierLoadout(List<Modifier> list) {
      List<String> bad = new ArrayList<>();

      try {
         testSingleModifierLoadout(list);
      } catch (Throwable var8) {
         var8.printStackTrace();
         System.out.println("Starting full test");
         List<Modifier> tmp = new ArrayList<>(list);

         for (int i = 0; i < tmp.size(); i++) {
            Modifier rm = tmp.remove(i);

            try {
               testSingleModifierLoadout(tmp);
               tmp.add(i, rm);
            } catch (Throwable var7) {
               i--;
            }
         }

         bad.add("Final minimal modifier loadout to trigger crash: " + tmp);
      }

      return bad;
   }

   @Test
   public static void testHeroCleaveABunch() {
      for (int i = 0; i < 5; i++) {
         testSingleModifierLoadout(Arrays.asList(ModifierLib.byName("hero cleave")));
      }
   }

   private static void testSingleModifierLoadout(List<Modifier> modifiers) {
      Random r = new Random(500L);
      List<Hero> heroList = new ArrayList<>();
      List<Monster> monsters = new ArrayList<>();

      for (HeroType ht : defaultHeroes) {
         heroList.add(new Hero(ht));
      }

      for (MonsterType mt : defaultMonsters) {
         monsters.add(new Monster(mt));
      }

      FightLog f = TestUtils.setupFight(heroList, monsters, modifiers.toArray(new Modifier[0]));

      for (int heroIndex = 0; heroIndex < 5; heroIndex++) {
         Hero h = heroList.get(heroIndex);

         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            Snapshot present = f.getSnapshot(FightLog.Temporality.Present);
            if (present.isEnd()) {
               return;
            }

            EntSide es = h.getSides()[sideIndex];
            EntSideState ess = f.getState(FightLog.Temporality.Present, h).getSideState(es);
            Eff first = ess.getCalculatedEffect();
            Ent target = null;
            if (f.isVictoryAssured()) {
               return;
            }

            if (first.needsTarget()) {
               target = Tann.random(f.getSnapshot(FightLog.Temporality.Present).getAliveEntities(first.isFriendly()), r);
            }

            TestUtils.hit(f, h, target, es, false);
         }
      }
   }

   @Test
   @Slow
   public static void testItemPerformance() {
      long DELTA_LIMIT = 8L;
      List<Item> fail = new ArrayList<>();
      int GROUP_SIZE = 5;
      List<Item> copy = ItemLib.getMasterCopy();

      for (int i = 0; i < copy.size(); i += GROUP_SIZE) {
         List<Item> toTest = copy.subList(i, Math.min(copy.size() - 1, i + GROUP_SIZE));
         toTest.remove(ItemLib.byName("bag of holding"));
         long t = System.currentTimeMillis();
         testSingleItemLoadout(toTest);
         long delta = System.currentTimeMillis() - t;
         if (delta > 8L) {
            System.err.println(toTest + ": took " + delta + "ms, rechecking...");
            int tests = 50;
            t = System.currentTimeMillis();

            for (int testIndex = 0; testIndex < tests; testIndex++) {
               testSingleItemLoadout(toTest);
            }

            delta = System.currentTimeMillis() - t;
            long per = delta / tests;
            System.err.println("... took " + delta + " for " + tests + " (" + per + ")");
            if (delta > 8L * tests) {
               System.err.println("(which is still too high)");
               fail.addAll(toTest);
            }
         }
      }

      if (fail.size() > 0) {
         System.err.println(fail);
         Tann.assertTrue(fail.size() == 0);
      }
   }

   @Test
   @SkipNonTann
   public static void heroGenerationSpeed() {
      int MAX_MS = 50;
      int ITER = 10;
      List<String> failed = new ArrayList<>();

      for (HeroCol col : HeroCol.basics()) {
         for (int heroIndex = 0; heroIndex < 10; heroIndex++) {
            int tier = (int)(Math.random() * 4.0);
            int seed = (int)(Math.random() * 1000.0);
            boolean ok = false;

            for (int i = 0; i < 3; i++) {
               if (getTimeTakenToGenerate(col, tier, seed) < 50L) {
                  ok = true;
                  break;
               }
            }

            if (!ok) {
               String name = col + "" + tier + "-" + seed;
               System.err.println("Took " + getTimeTakenToGenerate(col, tier, seed) + " to generate " + name);
               failed.add(name);
            }
         }
      }

      if (failed.size() > 0) {
         System.out.println(failed);
      }

      Tann.assertEquals("Failed strings should be empty", 0, failed.size());
   }

   private static long getTimeTakenToGenerate(HeroCol col, int tier, int seed) {
      long t = System.currentTimeMillis();
      HeroType ht = PipeHeroGenerated.generate(col, tier, seed);
      return System.currentTimeMillis() - t;
   }

   @Test
   @Slow
   public static void entPanelRendering() {
      List<EntSide> bad = new ArrayList<>();
      Batch b = new SpriteBatch();
      b.begin();
      List<EntSide> sides = EntSidesLib.getAllSidesWithValue();
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);

      for (EntSide es : sides) {
         try {
            TestUtils.rollHit(f, h, m, es, false);
            m.setState(FightLog.Temporality.Visual, TestUtils.getState(f, m, FightLog.Temporality.Present));
            m.setState(FightLog.Temporality.Present, TestUtils.getState(f, m, FightLog.Temporality.Present));
            m.setState(FightLog.Temporality.Future, TestUtils.getState(f, m, FightLog.Temporality.Future));
            m.getEntPanel().layout();
            m.getEntPanel().draw(b, 1.0F);
         } catch (Exception var9) {
            bad.add(es);
            var9.printStackTrace();
         }

         TestUtils.undo(f);
      }

      b.end();
      b.dispose();
      Tann.assertTrue("Should be no bads: " + bad, bad.size() == 0);
   }

   @Test
   @Slow
   public static void multiRandomTest() {
      int amt = 1000;

      for (int i = 0; i < 1000; i++) {
         if (i % 1000 == 0) {
            System.out.println("ScatterMultiRandom " + i + "/" + 1000);
         }

         miniRandomTest();
      }
   }

   private static void miniRandomTest() {
      TestUtils.setupFight(HeroTypeUtils.random().makeEnt(), MonsterTypeLib.randomWithRarity());

      for (Hero h : TestUtils.heroes) {
         for (int i = 0; i < 2; i++) {
            Item item = ItemLib.random();
            if (!item.getName().equalsIgnoreCase("dead crow")) {
               String name = item.getName(false);
               if (!name.equalsIgnoreCase("Stasis") && !name.equalsIgnoreCase("bag of holding")) {
                  h.addItem(item);
               }
            }
         }
      }

      try {
         List<Monster> monsters = MonsterTypeLib.monsterList(MonsterTypeLib.listName("basilisk"));
         FightLog f = TestUtils.setupFight(TestUtils.heroes, monsters, new Modifier[0]);
         TestUtils.spell(f, SpellLib.BURST, TestUtils.heroes.get(0));
         TestUtils.rollHit(
            f,
            monsters.get(0),
            TestUtils.heroes.get(0),
            Tann.pick(EntSidesBlobBig.decay.val(2), EntSidesBlobBig.chillingGaze.val(2), EntSidesBlobBig.poisonApple.val(2)),
            true
         );
         rollHitLateBuff(f, TestUtils.heroes.get(0), TestUtils.heroes.get(0), Tann.pick(ESB.shield.val(2), ESB.undying, ESB.shieldRepel.val(1)));
         rollHitLateBuff(f, TestUtils.heroes.get(0), null, Tann.pick(ESB.dmgAll.val(1), ESB.manaLust.val(1), ESB.manaDuplicate.val(1)));
      } catch (Exception var5) {
         var5.printStackTrace();
         Tann.assertTrue("failed with setup: " + TestUtils.heroes.get(0) + ":" + TestUtils.heroes.get(0).getItems() + ":" + TestUtils.monsters, false);
      }
   }

   private static void rollHitLateBuff(FightLog f, Ent source, Ent target, EntSide side) {
      f.addCommand(new SimpleCommand(source, new SimpleTargetable(null, new EffBill().buff(new Buff(new AffectSides(new ReplaceWith(side)))).bEff())), false);
      source.getDie().setSide(0);
      f.addCommand(source.getDie().getTargetable(), target, false);
   }

   @Test
   @Slow
   public static void testAllDifficultiesGeneration() {
      for (Difficulty d : Difficulty.values()) {
         if (d != Difficulty.Normal) {
            testDifficultyAllOptions(d);
         }
      }
   }

   private static void testDifficultyAllOptions(Difficulty d) {
      for (boolean b : Tann.BOTH) {
         OptionLib.MYRIAD_OFFERS.setValue(b, false);
         testModifierOffer(d, 20);
      }
   }

   private static void testModifierOffer(Difficulty d, int amt) {
      DungeonContext dc = new DungeonContext(new ClassicConfig(d), Party.generate(0));

      for (int i = 0; i < amt; i++) {
         List<Phase> phases = new PhaseGeneratorDifficulty(d).generate(dc);
         ChoicePhase cp = (ChoicePhase)phases.get(0);

         for (Choosable option : cp.getOptions()) {
            Modifier m = (Modifier)option;
            Tann.assertTrue("should be no missingno offered for " + d, !m.isMissingno());
         }
      }
   }

   @Test
   @Slow
   public static void generatedTactics() {
      TestUtils.setupFight(HeroTypeUtils.random().makeEnt(), MonsterTypeLib.randomWithRarity());
      HeroType ht = HeroTypeLib.byName("oij");
      List<String> bads = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         try {
            List<Monster> monsters = MonsterTypeLib.monsterList(MonsterTypeLib.listName("basilisk"));
            FightLog f = TestUtils.setupFight(TestUtils.heroes, monsters, new Modifier[0]);
            Snapshot s = f.getSnapshot();
            ht = HeroTypeLib.byName("n" + Tann.randomInt(10) + "." + Tann.randomInt(500));
            Tactic t = ht.getTactic();
            if (t != null) {
               List<Ent> targs = TargetingManager.getValidTargets(s, t, true);
               if (targs.size() > 0) {
                  f.addCommand(new AbilityCommand(t, targs.get(0)), false);
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
            bads.add(ht.getName());
         }
      }

      Tann.assertBads(bads);
   }

   @Test
   @Slow
   public static void randomTextRendering() {
      int attempts = 500;
      SpriteBatch batch = new SpriteBatch();
      batch.begin();
      List<String> bad = new ArrayList<>();

      for (int i = 0; i < 500; i++) {
         String s = makeRandomScaryString();

         try {
            TextWriter tw = new TextWriter(makeRandomScaryString());
            tw.draw(batch, 1.0F);
            if (TannStageUtils.hasActor(tw, Rectactor.class)) {
               bad.add(s);
            }
         } catch (Exception var6) {
            var6.printStackTrace();
            bad.add(s);
         }
      }

      batch.end();
      Tann.assertBads(bad);
   }

   private static String makeRandomScaryString() {
      String s = "";

      for (int i = 0; i < 5; i++) {
         switch (Tann.randomInt(10)) {
            case 1:
               s = s + "[";
               break;
            case 2:
               s = s + "]";
               break;
            case 3:
               s = s + "$";
               break;
            case 4:
               s = "[" + s + "]";
               break;
            case 5:
               s = s + Tann.randomInt(99999);
               break;
            case 6:
               s = s + Math.random();
               break;
            case 7:
               return SpeechGarbler.garble(s);
            case 8:
               return s + makeRandomScaryString();
            default:
               s = s + Tann.randomString(5);
         }
      }

      return s;
   }
}
