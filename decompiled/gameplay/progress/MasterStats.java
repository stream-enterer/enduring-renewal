package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.stats.StatUpdate;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.KillsStat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.SurrenderChoiceStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.save.LevelData;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.ui.ClipboardUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MasterStats implements StatUpdate {
   private List<Stat> allStats;
   private Map<String, Stat> statMap;
   private UnlockManager unlockManager;
   private RunHistoryStore runHistoryStore;
   static Map<String, Stat> cached;

   public void init() {
      this.allStats = StatLib.makeAllStats(StatLib.StatSource.Master);
      this.statMap = StatLib.makeStatsMap(this.allStats);
      AchLib.init();
      this.unlockManager = new UnlockManager(AchLib.getAll());
      this.runHistoryStore = new RunHistoryStore();
      this.runHistoryStore.loadAll();
      this.reset();
   }

   public void reset() {
      cached = null;
      this.runHistoryStore.loadAll();

      for (Stat s : this.allStats) {
         s.reset();
      }

      this.getUnlockManager().resetAchievements();

      try {
         this.loadAll();
      } catch (Exception var3) {
         var3.printStackTrace();
         TannLog.log("Failed to load stats " + var3.getMessage());
      }
   }

   public void mergeStats(DungeonContext dungeonContext) {
      this.mergeStats(dungeonContext.getNonZeroStats());
   }

   public void mergeStats(List<Stat> stats) {
      StatLib.mergeStats(this.allStats, stats);
      this.saveAll();
   }

   public void saveAll() {
      MasterStatsData msd = new MasterStatsData(StatLib.getNonZeroStats(this.allStats), this.getUnlockManager().getCompletedAchievements());
      Prefs.setString("stats", com.tann.dice.Main.getJson().toJson(msd));
      com.tann.dice.Main.getSettings().saveForHs();
   }

   private MasterStatsData loadData() {
      String save = Prefs.getString("stats", "");
      if (save.isEmpty()) {
         return null;
      } else {
         try {
            return (MasterStatsData)com.tann.dice.Main.getJson().fromJson(MasterStatsData.class, save);
         } catch (Exception var3) {
            var3.printStackTrace();
            TannLog.log("Failed to load stats - " + var3.getMessage(), TannLog.Severity.error);
            return null;
         }
      }
   }

   private void loadAll() {
      MasterStatsData msd = this.loadData();
      this.loadAll(msd);
   }

   private void loadAll(MasterStatsData msd) {
      if (msd != null) {
         StatLib.mergeStats(this.allStats, msd.stats);
         this.getUnlockManager().load(msd.completedAchievementStrings);
      }
   }

   public Stat getStat(String key) {
      Stat s = this.statMap.get(key);
      if (s == null) {
         throw new RuntimeException("Error finding stat from key: " + key);
      } else {
         return s;
      }
   }

   public Map<String, Stat> createMergedStats() {
      if (cached != null) {
         return cached;
      } else {
         cached = this.createMergedStats(null);
         return cached;
      }
   }

   public static void clearMergedStats() {
      cached = null;
   }

   public Map<String, Stat> createMergedStats(Mode exclude) {
      List<Stat> stats = StatLib.makeAllStats(StatLib.StatSource.Master);
      MasterStatsData msd = this.loadData();
      if (msd != null) {
         StatLib.mergeStats(stats, msd.stats);
      }

      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s instanceof DungeonScreen) {
         DungeonScreen ds = (DungeonScreen)s;
         Mode m = ds.getDungeonContext().getContextConfig().mode;
         if (m != exclude && !m.skipStats()) {
            try {
               DungeonContext cont = SaveState.loadContext(m.getSaveKey());
               if (cont != null) {
                  StatLib.mergeStats(stats, cont.getNonZeroStats());
               }
            } catch (Exception var8) {
               TannLog.log("Failed to merge logs from " + m.getClass().getSimpleName());
               TannLog.error(var8.getClass().getSimpleName() + ": " + var8.getMessage());
               var8.printStackTrace();
            }
         }
      }

      if (OptionLib.SHOW_STAT_POPUPS.c()) {
         s = com.tann.dice.Main.getCurrentScreen();
         if (s != null) {
            s.addPopup(new Pixl(3, 3).border(Colours.grey).text("created merged stats").pix());
         }
      }

      return StatLib.makeStatsMap(stats);
   }

   @Override
   public void updateAfterCommand(StatSnapshot ss, Map<String, Stat> statMap) {
      this.getUnlockManager().updateAfterCommand(ss, statMap);
   }

   @Override
   public void updateEndOfRound(StatSnapshot ss) {
      this.getUnlockManager().updateEndOfRound(ss);
   }

   @Override
   public void updateAllDiceLanded(List<EntSideState> states) {
      this.getUnlockManager().allDiceLanded(states);
   }

   public void updateAfterSaveForStats(Map<String, Stat> mergedStats) {
      this.getUnlockManager().updateAfterSaveForStats(mergedStats);
   }

   @Override
   public void endOfFight(StatSnapshot ss, boolean victory) {
      this.getUnlockManager().endOfFight(ss, victory);
   }

   @Override
   public void updateDiceRolled(int count) {
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory, boolean background) {
      for (Stat s : this.allStats) {
         s.metaEndOfRun(context, victory);
      }

      this.mergeStats(context);
      this.getUnlockManager().endOfRun(context, victory, background);
      this.updateAfterSaveForStats(this.statMap);
      RunHistory runHistory = new RunHistory(
         System.currentTimeMillis(),
         victory,
         context.getContextConfig().mode.getSaveKey(),
         context.getContextConfig().getUntranslatedEndTitle(),
         context.getCurrentModifiersStrings(),
         context.getParty().toSave(),
         new LevelData(context.getCurrentLevel())
      );
      this.addRunHistoryData(runHistory);
      this.saveAll();
   }

   public void setAllPickedStatsRNG(int rngMax) {
      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         this.getStat(KillsStat.getStatName(mt)).setValue(srng(rngMax));
      }

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if (!ht.skipStats()) {
            this.getStat(PickStat.nameFor(ht)).setValue(PickStat.getRandomValue(rngMax));
         }
      }

      for (Item eq : ItemLib.getMasterCopy()) {
         this.getStat(PickStat.nameFor(eq)).setValue(PickStat.getRandomValue(rngMax));
      }
   }

   private static int srng(int rngmax) {
      return (int)(Math.random() * rngmax);
   }

   private MasterStatsData loadStatsDataFromClipboard() {
      try {
         String clip = ClipboardUtils.pasteSafer();
         if (clip == null || clip.isEmpty()) {
            throw new Exception("Empty clipboard?");
         } else if (!clip.contains("xxxxxxxxxxxxxxx")) {
            throw new Exception("Clipboard does not contain copied progress");
         } else {
            clip = clip.trim();
            String[] split = clip.split("xxxxxxxxxxxxxxx");
            String json = split[0];
            int hashscode = Integer.parseInt(split[1]);
            if (hashscode != json.hashCode()) {
               throw new Exception("[purple]Clipboard has been edited[n][n][pink][sin]Suspected Cheater[cu][sin][n][n](or try exporting again?)");
            } else {
               return (MasterStatsData)com.tann.dice.Main.getJson().fromJson(MasterStatsData.class, json);
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         TannLog.log(var6.getMessage(), TannLog.Severity.error);
         com.tann.dice.Main.getCurrentScreen().showDialog("[red]Progress not loaded - [n][text]" + var6.getClass().getSimpleName() + ": " + var6.getMessage());
         return null;
      }
   }

   public void loadAchievementFromClipboard() {
      try {
         MasterStatsData masterStatsData = this.loadStatsDataFromClipboard();
         if (masterStatsData != null) {
            this.getUnlockManager().loadAchievementsFromStrings(masterStatsData.completedAchievementStrings);
            this.saveAll();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
         TannLog.log(var2.getMessage(), TannLog.Severity.error);
         com.tann.dice.Main.getCurrentScreen().showDialog("[red]Progress not loaded - [n][text]" + var2.getClass().getSimpleName() + ": " + var2.getMessage());
      }
   }

   private void addRunHistoryData(RunHistory runHistory) {
      this.runHistoryStore.addRunHistory(runHistory);
   }

   public void resetAllStatsButNotAchievements() {
      this.runHistoryStore.reset();

      for (Stat s : this.allStats) {
         s.reset();
      }

      this.saveAll();
   }

   public UnlockManager getUnlockManager() {
      return this.unlockManager;
   }

   public RunHistoryStore getRunHistoryStore() {
      return this.runHistoryStore;
   }

   public boolean hasWon() {
      return this.getStat("total-wins").getValue() > 0;
   }

   public void savePickRatesToClipboard() {
      List<PickStat> stats = new ArrayList<>();

      for (Stat s : this.allStats) {
         if (s instanceof PickStat && s.getValue() != 0) {
            stats.add((PickStat)s);
         }
      }

      PickRateData prd = new PickRateData(stats);
      String json = com.tann.dice.Main.getJson().toJson(prd);
      ClipboardUtils.copyWithSoundAndToast(json);
   }

   int getFlees(boolean allow) {
      Stat s = this.getStat(SurrenderChoiceStat.NAME(allow));
      return s != null ? s.getValue() : 0;
   }

   public boolean allowFlee() {
      return this.getFlees(true) * 0.5 > this.getFlees(false) || this.getFlees(false) < 3;
   }
}
