package com.tann.dice.test;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.battleTest.BattleTestUtils;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.NoLevelGeneratedException;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.battleTest.template.BossTemplateLibrary;
import com.tann.dice.gameplay.battleTest.template.LevelTemplate;
import com.tann.dice.gameplay.battleTest.testProvider.TierStats;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.statics.Images;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestRandomBits {
   @Test
   @Slow
   public static void testScenarioGen() {
      for (int runs = 0; runs < 3; runs++) {
         for (Difficulty diff : Difficulty.values()) {
            int levelIndex = 1;

            for (TP<Zone, Integer> conf : Mode.getStandardLevelTypes()) {
               for (int insideLevel = 0; insideLevel < conf.b; insideLevel++) {
                  try {
                     DungeonContext dc = DebugUtilsUseful.dummyContext();
                     Level level;
                     if (Tann.contains(Mode.getStandardBossLevels(), levelIndex)) {
                        LevelTemplate template = BossTemplateLibrary.getBossTemplate(conf.a);
                        level = BattleTestUtils.generateBossLevel(template, new TierStats(levelIndex, diff), new ArrayList<>(), false);
                     } else {
                        level = BattleTestUtils.generateStdLevel(conf.a, new TierStats(levelIndex, Difficulty.Normal), new ArrayList<>(), false, dc);
                     }

                     TestRunner.assertTrue("generated level " + levelIndex + " should have monsters", level.getMonsterList().size() > 0);
                     TestRunner.assertTrue(
                        "generated level " + levelIndex + " should have no errors", !level.getMonsterList().contains(PipeMonster.getMissingno())
                     );
                     TestRunner.assertTrue("generated level " + levelIndex + " should have no nulls", !level.getMonsterList().contains(null));
                     levelIndex++;
                  } catch (NoLevelGeneratedException var12) {
                     var12.printStackTrace();
                     TestRunner.assertTrue("failed tgenerate level " + levelIndex + " and type : " + conf.a, false);
                  }
               }
            }
         }
      }
   }

   @Test
   public static void testLootGen() {
      DungeonContext dc = DebugUtilsUseful.dummyContext();

      for (int i = 1; i <= 20; i += 2) {
         int quality = ItemLib.getStandardItemQualityFor(i, 0);
         List<Item> items = ItemLib.randomWithExactQuality(2, quality, dc);
         TestRunner.assertTrue("generated level " + i + " should have 2 items", items.size() == 2);
         TestRunner.assertTrue("generated level " + i + " should have no nulls", !items.contains(null));
         TestRunner.assertTrue(
            "generated level " + i + " should have no curses", !items.get(0).getName(false).equals("curse") && !items.get(1).getName(false).equals("curse")
         );
      }
   }

   @Test
   public static void testAllEnemiesKillable() {
      List<MonsterType> types = new ArrayList<>(MonsterTypeLib.getMasterCopy());

      for (int i = types.size() - 1; i >= 0; i--) {
         if (types.get(i).getName(false).contains("host")) {
            types.remove(i);
         }
      }

      types.remove(MonsterTypeLib.byName("inevitable"));
      types.remove(MonsterTypeLib.byName("demon"));
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("whirl.i.silk cape.i.leather gloves.i.stasis.i.determination")}, types.toArray(new MonsterType[0])
      );
      Hero h = f.getSnapshot(FightLog.Temporality.Present).getHeroesAliveAtStartOfTurn().get(0);
      TestUtils.hit(f, h, ESB.undying.getBaseEffect());
      TestRunner.assertTrue("Should be a bunch of monsters", f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).size() > 1);
      TestRunner.assertTrue("Should not be victory", !f.getSnapshot(FightLog.Temporality.Present).isVictory());
      Spell s = new SpellBill().eff(new EffBill().damage(3).group()).bSpell();

      for (int ix = 0; ix < 100 && !f.getSnapshot(FightLog.Temporality.Present).isVictory(); ix++) {
         TestUtils.hit(f, null, new EffBill().damage(100).group().bEff(), false);
         Eff e = new EffBill().damage(100).group().bEff();
         TestUtils.hit(f, null, e, false);
         TestUtils.spell(f, s, null);
         TestUtils.roll(f, h, null, 0, false);
         if (f.getSnapshot(FightLog.Temporality.Present).isVictory()) {
            break;
         }
      }

      Snapshot end = f.getSnapshot(FightLog.Temporality.Present);
      TestRunner.assertTrue("Should be victory " + end.getEntities(false, false), end.isVictory());
   }

   @Test
   public static void testItemFiles() {
      List<String> fileNames = new ArrayList<>();
      ArrayIterator allItem = com.tann.dice.Main.atlas.getRegions().iterator();

      while (allItem.hasNext()) {
         AtlasRegion ar = (AtlasRegion)allItem.next();
         String n = ar.name;
         if (n.startsWith("item/")
            && !n.contains("unused/")
            && !n.contains("placeholder")
            && !n.contains("unknown")
            && !n.contains("generated")
            && !n.contains("special")
            && !n.contains("/bug")
            && !n.contains("idol")) {
            fileNames.add(ar.name);
         }
      }

      for (Item e : ItemLib.getMasterCopy()) {
         fileNames.remove(e.getImagePath());
      }

      TestRunner.assertTrue("no unused files : " + fileNames, fileNames.size() == 0);
   }

   @Test
   public static void testSideFiles() {
      List<String> fileNames = new ArrayList<>();
      ArrayIterator sides = com.tann.dice.Main.atlas_3d.getRegions().iterator();

      while (sides.hasNext()) {
         AtlasRegion ar = (AtlasRegion)sides.next();
         String n = ar.name;
         if (n.startsWith("reg/face")
            && !n.contains("unused/")
            && !n.contains("/bug")
            && !n.contains("placeholder")
            && !n.contains("template")
            && !n.contains("/old/")
            && !n.contains("/special/")
            && !n.contains("/extra/")
            && !n.contains("/debug/")) {
            fileNames.add(ar.name);
         }
      }

      for (EntSide es : EntSidesLib.getAllSidesWithValue()) {
         fileNames.remove(((AtlasRegion)es.getTexture()).name);
      }

      TestRunner.assertTrue("no unused files : " + fileNames, fileNames.size() == 0);
   }

   @Test
   public static void testSpellFiles() {
      List<String> fileNames = new ArrayList<>();
      ArrayIterator spells = com.tann.dice.Main.atlas.getRegions().iterator();

      while (spells.hasNext()) {
         AtlasRegion ar = (AtlasRegion)spells.next();
         String n = ar.name;
         if (!n.contains(PipeHeroGenerated.BS)
            && !n.contains(PipeHeroGenerated.RS)
            && !n.contains("unused/")
            && !n.contains("placeholder/")
            && !n.contains("template")
            && !n.contains("special/")
            && !n.contains("glyph/")
            && n.startsWith("ability/spell/")) {
            fileNames.add(ar.name);
         }
      }

      for (Spell s : SpellLib.makeAllSpellsList()) {
         fileNames.remove(((AtlasRegion)s.getImage()).name);
      }

      TestRunner.assertTrue("no unused files : " + fileNames, fileNames.size() == 0);
   }

   @Test
   public static void testPortraitFiles() {
      List<String> fileNames = new ArrayList<>();
      ArrayIterator types = com.tann.dice.Main.atlas.getRegions().iterator();

      while (types.hasNext()) {
         AtlasRegion ar = (AtlasRegion)types.next();
         String n = ar.name;
         if (n.startsWith("portrait/hero")
            && !n.contains("unused/")
            && !n.contains("placeholder")
            && !n.contains("template")
            && !n.contains("rnd")
            && !n.contains("old")
            && !n.contains("special/")
            && !n.contains("Tw1n".toLowerCase())) {
            String[] splitName = ar.name.split("/");
            fileNames.add(splitName[splitName.length - 1]);
         }
      }

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         for (int i = fileNames.size() - 1; i >= 0; i--) {
            if (fileNames.get(i).startsWith(ht.getName(false).toLowerCase())) {
               fileNames.remove(i);
            }
         }
      }

      TestRunner.assertTrue("no unused files : " + fileNames, fileNames.size() == 0);
   }

   @Test
   @Slow
   public static void testMonstersGeneratedPerLevel() {
      Difficulty diff = Difficulty.Unfair;
      Map<Integer, Set<MonsterType>> levelNumberMap = new HashMap<>();

      for (int i = 1; i <= 20; i++) {
         levelNumberMap.put(i, new HashSet<>());
      }

      int TESTS = 500;

      for (int i = 0; i < 500; i++) {
         DungeonContext dc = new DungeonContext(new ClassicConfig(diff), Party.generate(0), 1);

         for (int levelNum = 1; levelNum <= 20; levelNum++) {
            Level current = dc.getCurrentLevel();

            for (MonsterType mt : current.getMonsterList()) {
               levelNumberMap.get(levelNum).add(mt);
            }

            if (levelNum < 19) {
               dc.nextLevel();
            }
         }
      }

      DungeonContext dc = new DungeonContext(new ClassicConfig(diff), Party.generate(0), 1);
      Map<Zone, Set<MonsterType>> typeMap = new HashMap<>();

      for (Zone lt : Zone.values()) {
         typeMap.put(lt, new HashSet<>());
      }

      int minMonsters = 5000;
      int minMonstersLevel = -1;
      float worstRatio = 1.0F;
      int worstRatioLevel = -1;

      for (int i = 1; i <= 20; i++) {
         if (i % 4 != 3) {
            Zone type = dc.getContextConfig().getTypeForLevel(i, dc);
            typeMap.get(type).addAll(levelNumberMap.get(i));
            int total = levelNumberMap.get(i).size();
            float ratio = (float)total / type.validMonsters.size();
            if (ratio < worstRatio) {
               worstRatio = ratio;
               worstRatioLevel = i;
            }

            if (total < minMonsters) {
               minMonsters = total;
               minMonstersLevel = i;
            }
         }
      }

      System.out.println("Worst Ratio: " + worstRatio + "#" + worstRatioLevel);
      System.out.println("Lowest diversity: " + minMonsters + "#" + minMonstersLevel);
      if (minMonsters < 3) {
         throw new RuntimeException("Diversity too low: " + minMonsters + "--" + minMonstersLevel);
      } else {
         for (Zone lt : Zone.values()) {
            if (lt.isClassic()) {
               List<MonsterType> foundMonsters = new ArrayList<>(typeMap.get(lt));
               List<MonsterType> allMonsters = new ArrayList<>(lt.validMonsters);
               allMonsters.removeAll(foundMonsters);

               for (int ix = allMonsters.size() - 1; ix >= 0; ix--) {
                  if (UnUtil.isLocked(allMonsters.get(ix))) {
                     allMonsters.remove(ix);
                  }
               }

               if (allMonsters.size() > 0) {
                  System.err.println(allMonsters + " never generated for " + lt);
               }

               if (allMonsters.size() > 3) {
                  throw new RuntimeException("Too many missing monsters: " + allMonsters + "--" + lt);
               }
            }
         }
      }
   }

   @Test
   @Slow
   public static void testLevelGenerationUniqueness() {
      Difficulty diff = Difficulty.Unfair;
      int TESTS = 500;
      int fail = 0;
      int succeed = 0;
      List<List<MonsterType>> dupeLists = new ArrayList<>();

      for (int i = 0; i < 500; i++) {
         DungeonContext dc = new DungeonContext(new ClassicConfig(diff), Party.generate(0), 1);
         List<List<MonsterType>> metaList = new ArrayList<>();

         for (int levelNum = 1; levelNum <= 20; levelNum++) {
            Level current = dc.getCurrentLevel();
            List<MonsterType> currentList = current.getMonsterList();
            if (metaList.contains(currentList)) {
               fail++;
               System.err.println(levelNum + ":" + currentList + " (also found at position " + metaList.indexOf(currentList) + ")");
               if (!dupeLists.contains(currentList)) {
                  dupeLists.add(currentList);
               }
            } else {
               succeed++;
               metaList.add(currentList);
            }

            if (levelNum < 20) {
               dc.nextLevel();
            }
         }
      }

      if (fail > 0) {
         System.err.println(dupeLists);
         throw new RuntimeException("Same level served :" + (float)fail / (fail + succeed));
      }
   }

   @Test
   public static void testImagesLoaded() {
      List<String> bads = new ArrayList<>();

      for (Field f : Images.class.getDeclaredFields()) {
         try {
            Object a = f.get(null);
            if (a == null) {
               System.out.println(a);
               bads.add(f.getName());
            }
         } catch (IllegalAccessException var6) {
            bads.add(f.getName());
            var6.printStackTrace();
         }
      }

      Tann.assertTrue("Should be no bad images; " + bads, bads.size() == 0);
   }

   @Test
   @Skip
   public static void checkItemPartials() {
      List<String> all = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         all.add(e.getName(false));
      }

      List<String> badPartials = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         for (String s : all) {
            if (!s.equalsIgnoreCase(e.getName(false)) && s.contains(e.getName(false))) {
               badPartials.add(s);
               badPartials.add(e.getName(false));
            }
         }
      }

      if (badPartials.size() > 0) {
         System.out.println(badPartials);
      }

      Tann.assertEquals("should be no conflicts", badPartials.size(), 0);
   }

   @Test
   public static void keywordFiles() {
      List<String> badFileNames = new ArrayList<>();
      ArrayIterator var1 = com.tann.dice.Main.atlas_3d.getRegions().iterator();

      while (var1.hasNext()) {
         AtlasRegion ar = (AtlasRegion)var1.next();
         String name = ar.name;
         if (name.startsWith("keyword/") && !name.contains("unused") && !name.contains("placeholder") && !name.contains("small/") && !name.contains("special")) {
            String[] n = name.split("\\/");
            String texName = n[n.length - 1];
            if (texName.contains("-")) {
               texName = texName.split("-")[0];
            }

            boolean found = false;

            for (Keyword k : Keyword.values()) {
               if (k.name().toLowerCase().equalsIgnoreCase(texName)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               badFileNames.add(texName);
            }
         }
      }

      TestRunner.assertTrue("no unused files : " + badFileNames, badFileNames.size() == 0);
   }

   @Test
   public static void listMessing() {
      ModifierType[] mts = new ModifierType[]{null, ModifierType.Blessing, ModifierType.Curse, ModifierType.Tweak};

      for (ModifierType mt : mts) {
         try {
            ModifierLib.getAll(mt).add(null);
            Tann.assertTrue("Shouldn't be able to mess with " + mt, false);
         } catch (UnsupportedOperationException var6) {
         }
      }
   }

   public static Map<Mode, List<Modifier>> getBannedModifiersPerMode() {
      Map<Mode, List<Modifier>> done = new HashMap<>();

      for (Mode mode : Mode.getAllModes()) {
         List<Modifier> banned = new ArrayList<>();

         for (Modifier mod : ModifierLib.getAll()) {
            if ((mode.getBannedCollisionBits() & mod.getCollisionBits()) != 0L) {
               banned.add(mod);
            }
         }

         done.put(mode, banned);
      }

      return done;
   }

   public static Map<PartyLayoutType, List<Modifier>> getBannedModifiersPerPLT() {
      Map<PartyLayoutType, List<Modifier>> done = new HashMap<>();

      for (PartyLayoutType plt : PartyLayoutType.values()) {
         List<Modifier> banned = new ArrayList<>();

         for (Modifier mod : ModifierLib.getAll()) {
            if ((plt.getBannedCollisionBits(true) & mod.getCollisionBits()) != 0L) {
               banned.add(mod);
            }
         }

         done.put(plt, banned);
      }

      return done;
   }
}
