package com.tann.dice.gameplay.context.config;

import com.tann.dice.gameplay.battleTest.BattleTestUtils;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.NoLevelGeneratedException;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.battleTest.template.BossTemplateLibrary;
import com.tann.dice.gameplay.battleTest.template.LevelTemplate;
import com.tann.dice.gameplay.battleTest.testProvider.TierStats;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.level.Symmetricality;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.cursey.BlursedMode;
import com.tann.dice.gameplay.mode.cursey.BlurtraMode;
import com.tann.dice.gameplay.mode.cursey.BlyptraMode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.endPhase.runEnd.RunEndPhase;
import com.tann.dice.gameplay.progress.MasterStats;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.FurthestReachedStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfRun.ModeWinStat;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak.BestStreakStat;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak.CurrentStreakStat;
import com.tann.dice.gameplay.save.SaveStateData;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.container.GlobalContainerGameRules;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.NDimension;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ContextConfig {
   public static final int STANDARD_BUTTON_WIDTH = 50;
   public static final int STANDARD_BUTTON_HEIGHT = 20;
   public static final int SMALL_BUTTON_WIDTH = 38;
   public static final int SMALL_BUTTON_HEIGHT = 16;
   public final Mode mode;
   private static List<Global> MG_CACHE = new ArrayList<>();

   protected ContextConfig(Mode mode) {
      this.mode = mode;
   }

   public static void CLEAR_ALL_SAVES() {
      for (Mode m : Mode.getAllModes()) {
         if (m.getSaveKey() != null) {
            Prefs.clearPref(m.getSaveKey());
         }
      }
   }

   public int getWins() {
      Stat s = com.tann.dice.Main.self().masterStats.getStat(ModeWinStat.getName(this));
      return ModeWinStat.val(s, false);
   }

   public int getLosses() {
      Stat s = com.tann.dice.Main.self().masterStats.getStat(ModeWinStat.getName(this));
      return ModeWinStat.val(s, true);
   }

   public int getFurthestReached() {
      return com.tann.dice.Main.self().masterStats.getStat(FurthestReachedStat.getName(this)).getValue();
   }

   public final String getGeneralSaveKey() {
      return this.mode.getSaveKey();
   }

   public void clearSave() {
      Prefs.clearPref(this.getGeneralSaveKey());
   }

   public boolean hasSave() {
      return this.mode.hasSave();
   }

   public boolean saveAtRunEnd() {
      try {
         String save = Prefs.getString(this.getGeneralSaveKey(), null);
         if (save == null) {
            return false;
         } else {
            SaveStateData ssd = (SaveStateData)com.tann.dice.Main.getJson().fromJson(SaveStateData.class, save);

            for (String s : ssd.getP()) {
               Phase p = Phase.deserialise(s);
               if (p instanceof RunEndPhase) {
                  return true;
               }
            }

            return false;
         }
      } catch (Exception var6) {
         TannLog.error(var6, "saving");
         return false;
      }
   }

   public String getSpecificKey(String suffix) {
      return this.getSpecificKey() + "-" + suffix;
   }

   public String getSaveFileButtonName() {
      return "";
   }

   public boolean isLocked() {
      return false;
   }

   public final DungeonContext makeContext() {
      return this.makeContext(AntiCheeseRerollInfo.makeBlank());
   }

   public DungeonContext makeContext(AntiCheeseRerollInfo info) {
      resetCache();
      if (info == null) {
         info = AntiCheeseRerollInfo.makeBlank();
      }

      List<PartyLayoutType> types = GameStart.getTypesFromContext(this, info);
      PartyLayoutType chosen;
      if (UnUtil.isLocked(HeroCol.blue)) {
         chosen = PartyLayoutType.Force;
      } else if (UnUtil.isLocked(Feature.PARTY_LAYOUT_CHOICE)) {
         chosen = PartyLayoutType.Basic;
      } else {
         chosen = this.getMostCommon(types);
      }

      Party p = this.getStartingParty(chosen, info);
      DungeonContext dc = new DungeonContext(this, p, 1 + this.getLevelOffset());
      dc.setAntiCheeseRerollModifiers(info.getOldOptions());
      return dc;
   }

   private PartyLayoutType getMostCommon(List<PartyLayoutType> types) {
      float bestRarity = 0.0F;
      PartyLayoutType best = null;

      for (PartyLayoutType type : types) {
         float c = type.getChance();
         if (c > bestRarity) {
            best = type;
            bestRarity = c;
         }
      }

      return best;
   }

   public DungeonContext makeContext(AntiCheeseRerollInfo info, Party p) {
      DungeonContext dc = new DungeonContext(this, p, 1 + this.getLevelOffset());
      if (info != null) {
         dc.setAntiCheeseRerollModifiers(info.getOldOptions());
      }

      return dc;
   }

   public final String getUntranslatedEndTitle() {
      String configDesc = this.describeConfig();
      if (com.tann.dice.Main.demo) {
         return configDesc;
      } else {
         String result = this.mode.getTextButtonName();
         if (configDesc != null) {
            result = result + " " + configDesc;
         }

         return result;
      }
   }

   public final String getEndTitle() {
      String configDesc = com.tann.dice.Main.t(this.describeConfig());
      if (com.tann.dice.Main.demo) {
         return "[notranslate]" + configDesc;
      } else {
         String result = com.tann.dice.Main.t(this.mode.getTextButtonName());
         if (configDesc != null) {
            result = result + " " + configDesc;
         }

         return "[notranslate]" + result;
      }
   }

   public static void resetCache() {
      MG_CACHE = new ArrayList<>();
   }

   public final List<Global> getModeGlobals() {
      if (MG_CACHE == null || MG_CACHE.isEmpty() || TestRunner.isTesting()) {
         ArrayList<Global> result = new ArrayList<>();
         result.add(new GlobalContainerGameRules());
         result.add(EventUtils.makeAddPhaseContainer(this));
         result.addAll(this.getSpecificModeGlobals());
         MG_CACHE = result;
      }

      return MG_CACHE;
   }

   public Collection<Global> getSpecificModeAddPhases() {
      return new ArrayList<>();
   }

   protected boolean offerChanceEvents() {
      return true;
   }

   public List<Global> getSpecificModeGlobals() {
      return new ArrayList<>();
   }

   protected boolean offerStandardRewards() {
      return true;
   }

   public Level makeNextLevel(int levelNumber, List<Level> previous, DungeonContext context) {
      if (!context.isMonsterPooling()) {
         Level bossLevel = this.makeBossLevel(levelNumber, context);
         if (bossLevel != null) {
            return bossLevel;
         }
      }

      return this.makeLevel(levelNumber, context, new ArrayList<>(previous));
   }

   public Level makeLevel(int levelNumber, DungeonContext context, List<Level> adjacents) {
      Zone type = this.getTypeForLevel(levelNumber, context);
      List<NDimension> nDimensions = new ArrayList<>();

      for (Level l : adjacents) {
         nDimensions.add(BattleTestUtils.fromTypeList(l.getMonsterList()));
      }

      try {
         Level l = BattleTestUtils.generateStdLevel(type, this.makeTierStats(levelNumber, false, context), nDimensions, false, context);
         Symmetricality.sort(l.getMonsterList());
         return l;
      } catch (NoLevelGeneratedException var8) {
         return Level.errorLevel(levelNumber);
      }
   }

   public Level makeBossLevel(int levelNumber, DungeonContext context) {
      int gameplayLevel = this.getGameplayLevel(levelNumber, context);
      if (!Tann.contains(this.getBossLevels(), gameplayLevel)) {
         return null;
      } else {
         LevelTemplate template = BossTemplateLibrary.getBossTemplate(this.getTypeForLevel(gameplayLevel, context));
         if (template == null) {
            return null;
         } else {
            try {
               Level l = BattleTestUtils.generateBossLevel(template, this.makeTierStats(levelNumber, true, context), new ArrayList<>(), false);
               Symmetricality.sort(l.getMonsterList());
               return l;
            } catch (NoLevelGeneratedException var6) {
               return Level.errorLevel(levelNumber);
            }
         }
      }
   }

   private TierStats makeTierStats(int gameplayLevel, boolean boss, DungeonContext context) {
      return new TierStats(this.getGameplayLevel(gameplayLevel, context), boss ? Difficulty.Unfair : this.getDifficulty());
   }

   private int getGameplayLevel(int levelNumber, DungeonContext context) {
      for (Global gt : context.getModifierGlobalsIncludingLinked()) {
         levelNumber = gt.getLevelNumberForGameplay(levelNumber);
      }

      return levelNumber;
   }

   protected Difficulty getDifficulty() {
      return Difficulty.Unfair;
   }

   public int getTotalLength() {
      int total = 0;

      for (TP<Zone, Integer> a : this.getDefaultLevelTypes()) {
         total += a.b;
      }

      for (Global gt : this.getModeGlobals()) {
         total = gt.getMaxLevel(total);
      }

      return total;
   }

   public final List<TP<Zone, Integer>> getDefaultLevelTypes(DungeonContext context) {
      return this.getDefaultLevelTypes();
   }

   public List<TP<Zone, Integer>> getDefaultLevelTypes() {
      return Mode.getStandardLevelTypes();
   }

   public int[] getBossLevels() {
      return Mode.getStandardBossLevels();
   }

   public boolean isBoss(int levelNumber) {
      return levelNumber % 4 == 0;
   }

   public int getTotalDifferentLevels() {
      int total = 0;

      for (TP<Zone, Integer> t : this.getDefaultLevelTypes()) {
         total += t.b;
      }

      return total;
   }

   public int getLevelOffset() {
      return 0;
   }

   public final void afterDefeatAction() {
      AnticheeseData acd = this.getAnticheese();
      if (acd != null) {
         acd.defeated();
      }
   }

   public void quitAction() {
      this.clearStatics();
      com.tann.dice.Main.self().setScreen(new TitleScreen(this.mode));
   }

   private void clearStatics() {
      DungeonScreen.clearStaticReference();
   }

   public Zone getTypeForLevel(int levelNumber, DungeonContext context) {
      int start = this.getGameplayLevel(levelNumber, context) - 1;
      List<TP<Zone, Integer>> lt = context.getLevelTypes();

      for (TP<Zone, Integer> tannp : lt) {
         if (start < tannp.b) {
            return tannp.a;
         }

         start -= tannp.b;
      }

      TannLog.error("Unable to get type from difficulty: " + this + ":" + levelNumber);
      return lt.size() > 0 ? (Zone)lt.get(0).a : Zone.All;
   }

   public int getStreak(boolean best) {
      if (this.mode.skipStats()) {
         return 0;
      } else {
         MasterStats ms = com.tann.dice.Main.self().masterStats;
         Stat s;
         if (best) {
            s = ms.getStat(BestStreakStat.getName(this));
         } else {
            s = ms.getStat(CurrentStreakStat.getName(this));
         }

         return s == null ? -1 : s.getValue();
      }
   }

   public List<TP<Zone, Integer>> getOverrideLevelTypes(DungeonContext context) {
      return null;
   }

   public String getSpecificKey() {
      return this.getGeneralSaveKey();
   }

   public String classNameSerialise() {
      return this.getClass().getSimpleName();
   }

   public String serialise() {
      return "";
   }

   public StandardButton makeStartButton(boolean big) {
      return StartConfigButton.make(TextWriter.getTag(this.mode.getColour()) + "Start");
   }

   public void reachedLevelThree() {
      AnticheeseData antiCheese = this.getAnticheese();
      if (antiCheese != null) {
         antiCheese.reachedLevelThree();
         this.saveAnticheese(antiCheese);
      }
   }

   public void anticheeseReroll() {
      AnticheeseData antiCheese = this.getAnticheese();
      if (antiCheese != null) {
         antiCheese.reroll();
         this.saveAnticheese(antiCheese);
      }
   }

   public AnticheeseData getAnticheese() {
      return com.tann.dice.Main.getSettings().getSavedAnticheese(this.getAnticheeseKey());
   }

   public void clearAnticheese() {
      if (this.getAnticheeseKey() != null) {
         com.tann.dice.Main.getSettings().clearAnticheese(this.getAnticheeseKey());
      }
   }

   public String describeConfig() {
      return null;
   }

   public String getAnticheeseKey() {
      return null;
   }

   public Party getStartingParty(PartyLayoutType chosen, AntiCheeseRerollInfo info) {
      return Party.generate(this.getLevelOffset(), PipeHero.getGenType(this.getModeGlobals()), chosen, info.getOldHeroes());
   }

   public List<Modifier> getStartingModifiers() {
      return new ArrayList<>();
   }

   public void saveAnticheese(AnticheeseData acd) {
      com.tann.dice.Main.getSettings().saveAntiCheese(this.getAnticheeseKey(), acd);
   }

   public boolean usesAnticheese() {
      return this.getAnticheeseKey() != null;
   }

   public boolean antiCheeseHeroes() {
      return true;
   }

   public boolean skipStats() {
      return this.mode.skipStats();
   }

   public boolean canRestart() {
      return true;
   }

   public final Collection<? extends Modifier> getAvoidModifiers() {
      List<Modifier> avoids = new ArrayList<>(this.getStartingModifiers());
      return avoids;
   }

   public boolean skipFirstPartyInit() {
      return false;
   }

   public String getTwoCharactersMax() {
      if (this instanceof DifficultyConfig) {
         Difficulty d = this.getDifficulty();
         String modeName = com.tann.dice.Main.t(this.mode.getName());
         String difficultyName = com.tann.dice.Main.t(d.name());
         return "[notranslate]"
            + TextWriter.getTag(this.mode.getColour())
            + modeName.toLowerCase().charAt(0)
            + ""
            + TextWriter.getTag(d.getColor())
            + difficultyName.toLowerCase().charAt(0);
      } else if (this.mode instanceof BlursedMode) {
         return "[notranslate][green]b";
      } else if (this.mode instanceof BlurtraMode) {
         return "[notranslate][red]b";
      } else if (this.mode instanceof BlyptraMode) {
         return "[notranslate][pink]b";
      } else {
         String modeName = com.tann.dice.Main.t(this.mode.getName());
         return "[notranslate]" + TextWriter.getTag(this.mode.getColour()) + modeName.toLowerCase().charAt(0);
      }
   }
}
