package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.battleTest.BattleResult;
import com.tann.dice.gameplay.battleTest.BattleTest;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.context.config.misc.BalanceConfig;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.level.LevelUtils;
import com.tann.dice.gameplay.level.Symmetricality;
import com.tann.dice.gameplay.mode.general.nightmare.NightmareConfig;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DebugUtilsUseful {
   public static void debug() {
      System.out.println("debug");
   }

   public static DungeonContext dummyContext() {
      return dummyContext(1);
   }

   public static DungeonContext dummyContext(int lv) {
      return new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(lv - 1), lv);
   }

   public static DungeonContext dummyContext(ContextConfig config) {
      return new DungeonContext(config, Party.generate(0), 1);
   }

   public static Group makeFreqGroup(Difficulty difficulty, int iterations) {
      return makeFreqGroup(difficulty, iterations, null);
   }

   public static Group makeFreqGroup(Difficulty difficulty, int iterations, Integer levelRestriction) {
      Pixl p = new Pixl(3, 5).border(difficulty.getColor());
      long t = System.currentTimeMillis();
      p.text("Most frequent levels for " + difficulty.getColourTaggedName() + " - " + iterations + " iterations").row();
      List<Level> levels = new ArrayList<>();
      if (levelRestriction == null) {
         for (int i = 0; i < iterations; i++) {
            levels.addAll(LevelUtils.generateFor(difficulty));
         }
      } else {
         for (int i = 0; i < iterations; i++) {
            levels.add(LevelUtils.generateFor(difficulty, levelRestriction));
         }
      }

      List<HashMap<List<MonsterType>, Integer>> freqMap = new ArrayList<>();
      List<HashMap<List<MonsterType>, Float>> diffDeltaMap = new ArrayList<>();
      int MAX_LEVEL = 20;

      for (int i = 1; i <= MAX_LEVEL; i++) {
         freqMap.add(new HashMap<>());
         diffDeltaMap.add(new HashMap<>());
      }

      Comparator<MonsterType> cmp = new Comparator<MonsterType>() {
         public int compare(MonsterType o1, MonsterType o2) {
            return o2.getName(false).compareTo(o1.getName(false));
         }
      };

      for (int i = levels.size() - 1; i >= 0; i--) {
         Collections.sort(levels.get(i).getMonsterList(), cmp);
      }

      for (int i = 0; i < levels.size(); i++) {
         int levelNum = levelRestriction == null ? i % MAX_LEVEL : levelRestriction;
         Level l = levels.get(i);
         HashMap<List<MonsterType>, Integer> tmp = freqMap.get(levelNum - 1);
         diffDeltaMap.get(levelNum - 1).put(l.getMonsterList(), l.getDiffD());
         if (tmp.get(l.getMonsterList()) == null) {
            tmp.put(l.getMonsterList(), 0);
         }

         tmp.put(l.getMonsterList(), tmp.get(l.getMonsterList()) + 1);
      }

      for (int levelNumber = 1; levelNumber <= MAX_LEVEL; levelNumber++) {
         if (levelRestriction == null || levelRestriction == levelNumber) {
            Pixl level = new Pixl(3, 3).border(Colours.grey);
            level.text("[grey]Level " + levelNumber).row();
            List<Entry<List<MonsterType>, Integer>> list = new ArrayList<>(freqMap.get(levelNumber - 1).entrySet());
            Collections.sort(list, new Comparator<Entry<List<MonsterType>, Integer>>() {
               public int compare(Entry<List<MonsterType>, Integer> o1, Entry<List<MonsterType>, Integer> o2) {
                  return o2.getValue() - o1.getValue();
               }
            });
            int total = 0;

            for (Entry<List<MonsterType>, Integer> i : list) {
               total += i.getValue();
            }

            for (int i = 0; i < Math.min(4, list.size()); i++) {
               final Entry<List<MonsterType>, Integer> current = list.get(i);
               Pixl single = new Pixl(3, 3).border(Colours.purple);
               String txt = current.getValue() + "/" + total;
               if (total != 100) {
                  txt = txt + " (" + Tann.floatFormat((float)current.getValue().intValue() / total) + ")";
               }

               single.text(txt);
               if (OptionLib.SHOW_LEVEL_DIFF.c()) {
                  single.text(Level.diffDeltaString(diffDeltaMap.get(levelNumber - 1).get(current.getKey())));
               }

               single.row();

               for (MonsterType mt : current.getKey()) {
                  ImageActor ia = new ImageActor(mt.portrait);
                  single.actor(ia);
               }

               Actor singleLevel = single.pix();
               final int finalLevelNum = levelNumber;
               singleLevel.addListener(new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     Symmetricality.sort(current.getKey());
                     DungeonContext dc = new DungeonContext(new BalanceConfig(finalLevelNum), Party.generate(finalLevelNum - 1), finalLevelNum);
                     List<MonsterType> hack = dc.getCurrentLevel().getMonsterList();
                     hack.clear();
                     hack.addAll(current.getKey());
                     GameStart.start(dc);
                     return true;
                  }
               });
               level.actor(singleLevel, com.tann.dice.Main.width);
            }

            p.actor(level.pix());
            p.row();
         }
      }

      t = System.currentTimeMillis() - t;
      p.row().text("took " + t + "ms");
      return p.pix(4);
   }

   public static List<Integer> getValidLevels(List<MonsterType> asList) {
      List<Integer> valids = new ArrayList<>();

      for (int i = 1; i < 30; i++) {
         int ret = runBattle(i, asList);
         if ((ret & 2) > 0) {
            valids.add(i);
         }
      }

      return valids;
   }

   public static int runBattle(int tier, List<MonsterType> types) {
      BattleResult result = new BattleTest(tier, Difficulty.Unfair, types.toArray(new MonsterType[0])).runBattle();
      int ret = 0;
      if (result.isPlayerVictory()) {
         ret |= 1;
      }

      if (result.isValidLevel()) {
         ret |= 2;
      }

      return ret;
   }

   public static Actor showImages(int type, int width) {
      Pixl p = new Pixl(2);

      for (final AtlasRegion t : getAllTextureRegions(type)) {
         if (!t.name.contains("noise_packed")) {
            ImageActor ia = new ImageActor(t);
            ia.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  Actor a = new Pixl(3, 3).border(Colours.grey).text(t.name).row().image(t).pix();
                  com.tann.dice.Main.getCurrentScreen().push(a);
                  Tann.center(a);
                  return true;
               }
            });
            p.actor(ia, width);
         }
      }

      return p.pix();
   }

   public static List<AtlasRegion> getAllTextureRegions(int type) {
      TextureAtlas atlas;
      switch (type) {
         case 0:
            atlas = com.tann.dice.Main.atlas;
            break;
         case 1:
            atlas = com.tann.dice.Main.atlas_3d;
            break;
         case 2:
            atlas = com.tann.dice.Main.atlas_big;
            break;
         default:
            throw new RuntimeException("blah");
      }

      List<AtlasRegion> tr = new ArrayList<>();
      ArrayIterator var3 = atlas.getRegions().iterator();

      while (var3.hasNext()) {
         AtlasRegion ar = (AtlasRegion)var3.next();
         tr.add(ar);
      }

      return tr;
   }

   public static int getUses(EntSide target, Boolean player) {
      int total = 0;
      List<EntType> types = new ArrayList<>();
      if (player == null) {
         types.addAll(HeroTypeLib.getMasterCopy());
         types.addAll(MonsterTypeLib.getMasterCopy());
      } else if (player) {
         types.addAll(HeroTypeLib.getMasterCopy());
      } else {
         types.addAll(MonsterTypeLib.getMasterCopy());
      }

      for (EntType et : types) {
         for (EntSide es : et.sides) {
            if (target.sameTexture(es)) {
               total++;
            }
         }
      }

      return total;
   }

   public static List<List<Choosable>> makeRewards(int amt) {
      List<List<Choosable>> result = new ArrayList<>();

      for (int levelIndex = 0; levelIndex < amt; levelIndex++) {
         DungeonContext dc = new DungeonContext(new NightmareConfig(), Party.generate(0), levelIndex);
         result.add(dc.getLootForPreviousLevel());
      }

      return result;
   }

   public static Map<Keyword, Integer> getUses() {
      List<Eff> effs = EntTypeUtils.getAllEffs();
      List<Keyword> ks = ItemLib.getAllKeywordRefrences();
      ks.addAll(getKeywords(effs));
      Map<Keyword, Integer> uses = new HashMap<>();

      for (Keyword value : Keyword.values()) {
         uses.put(value, 0);
      }

      for (Keyword keyword : ks) {
         uses.put(keyword, uses.get(keyword) + 1);
      }

      return uses;
   }

   private static List<Keyword> getKeywords(List<Eff> effs) {
      List<Keyword> result = new ArrayList<>();

      for (Eff eff : effs) {
         result.addAll(eff.getKeywords());
      }

      return result;
   }

   public static String getAllStrings() {
      return getHeroStrings(false)
         + getMonsterStrings(false)
         + getModifierStrings(false)
         + itemStrings()
         + spellStrings()
         + traitStrings()
         + sideStrings()
         + getKeywordStrings();
   }

   public static String getBalString() {
      return getHeroStrings(true) + getMonsterStrings(true) + getModifierStrings(true) + itemStrings();
   }

   private static String getKeywordStrings() {
      String result = "";

      for (Keyword value : Keyword.values()) {
         result = result + value.getColourTaggedString() + "-" + value.getRules() + "-" + value.getExtraRules() + "\n";
      }

      return result;
   }

   private static String sideStrings() {
      String result = "";
      EntType et = HeroTypeLib.byName("thief");

      for (EntSide entSide : EntSidesLib.getAllSidesWithValue()) {
         result = result + entSide.toString() + "-" + entSide.getBaseEffect().describe() + "-" + entSide.getApproxTotalEffectTier(et) + "\n";
      }

      return result;
   }

   private static String traitStrings() {
      String result = "";

      for (EntType entType : EntTypeUtils.getAll()) {
         for (Trait trait : entType.traits) {
            if (trait.visible) {
               result = result + entType.getName() + "-" + trait.personal.describeForSelfBuff() + "\n";
            }
         }
      }

      return result;
   }

   private static String spellStrings() {
      String result = "";

      for (Spell spell : SpellLib.makeAllSpellsList()) {
         result = result + spell.getTitle() + "-" + spell.getBaseCost() + "-" + spell.describe() + "\n";
      }

      return result;
   }

   private static String itemStrings() {
      String result = "";

      for (Item item : ItemLib.getMasterCopy()) {
         result = result + item.getName() + "-" + item.getTier() + "-" + item.getDescription() + "\n";
      }

      return result;
   }

   private static String getMonsterStrings(boolean mini) {
      String result = "";

      for (MonsterType monsterType : MonsterTypeLib.getMasterCopy()) {
         String sideDetail = "";

         for (EntSide side : monsterType.sides) {
            sideDetail = sideDetail + side.getBaseEffect().describe(true);
         }

         sideDetail = getAllSides(monsterType, mini);
         String monString = monsterType.getName();
         if (mini) {
            monString = monString + monsterType.hp + "-" + sideDetail;
         } else {
            monString = monString
               + "-"
               + monsterType.getEffectiveHp()
               + "-"
               + monsterType.getAvgEffectTier(true)
               + "-"
               + monsterType.getSummonValue()
               + "-"
               + sideDetail;
         }

         monString = monString + "\n";
         result = result + monString;
      }

      return result;
   }

   private static String getHeroStrings(boolean mini) {
      String result = "";

      for (HeroType heroType : HeroTypeLib.getMasterCopy()) {
         String hS = heroType.getName()
            + (
               mini
                  ? heroType.hp + "-" + getAllSides(heroType, mini)
                  : "-" + heroType.getEffectiveHp() + "-" + heroType.getAvgEffectTier(true) + "-" + getAllSides(heroType, mini)
            )
            + "\n";
         result = result + hS;
      }

      return result;
   }

   private static int getWidth() {
      return 5;
   }

   private static int getHeight() {
      return 5;
   }

   private static String getAllSides(EntType heroType, boolean mini) {
      String sep = ":";
      List<String> strings = new ArrayList<>();

      for (EntSide side : heroType.sides) {
         strings.add(side.getBaseEffect().getValue() + "");
         strings.add(((AtlasRegion)side.getTexture()).name.replaceAll(".*/face/", ""));
         if (!mini) {
            strings.add(side.getApproxTotalEffectTier(heroType) + "");
         }
      }

      return Tann.commaList(strings, sep, sep);
   }

   private static String getModifierStrings(boolean mini) {
      String result = "";

      for (Modifier modifier : ModifierLib.getAll((Boolean)null)) {
         String mt = "" + (mini ? modifier.getTier() : modifier.getFloatTier());
         String modString = modifier.getName() + "-" + mt + (mini ? "" : "-" + modifier.getFullDescription()) + "\n";
         result = result + modString;
      }

      return result;
   }

   public static int getLowest(List<MonsterType> asList) {
      int lowest = 5000;

      for (int i = 1; i < 30; i++) {
         int ret = runBattle(i, asList);
         if ((ret & 1) > 0) {
            lowest = Math.min(lowest, i);
         }
      }

      return lowest;
   }
}
