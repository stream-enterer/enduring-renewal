package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.AchievementCompletionListener;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement.StatAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll.AfterRollAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip.EquipAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd.FightEndAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd.RunEndAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot.SnapshotAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.weird.MetaAchievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.ClipboardUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnlockManager {
   final List<Achievement> allAchievements;
   private List<Achievement> completedAchievements = new ArrayList<>();
   Set<Unlockable> lockedUnlockables = new HashSet<>();
   private final List<SnapshotAchievement> snapshotAchievements = new ArrayList<>();
   private final List<StatAchievement> statAchievements = new ArrayList<>();
   private final List<MetaAchievement> metaAchievements = new ArrayList<>();
   private final List<RunEndAchievement> runEndAchievements = new ArrayList<>();
   private final List<FightEndAchievement> fightEndAchievements = new ArrayList<>();
   private final List<EquipAchievement> equipAchievements = new ArrayList<>();
   private final List<AfterRollAchievement> afterRollAchievements = new ArrayList<>();
   List<AchievementCompletionListener> listeners = new ArrayList<>();

   public UnlockManager(List<Achievement> allAchievements) {
      this.allAchievements = allAchievements;

      for (Achievement a : allAchievements) {
         if (!a.isAchieved()) {
            if (a instanceof SnapshotAchievement) {
               this.snapshotAchievements.add((SnapshotAchievement)a);
            } else if (a instanceof StatAchievement) {
               this.statAchievements.add((StatAchievement)a);
            } else if (a instanceof MetaAchievement) {
               this.metaAchievements.add((MetaAchievement)a);
            } else if (a instanceof RunEndAchievement) {
               this.runEndAchievements.add((RunEndAchievement)a);
            } else if (a instanceof FightEndAchievement) {
               this.fightEndAchievements.add((FightEndAchievement)a);
            } else if (a instanceof EquipAchievement) {
               this.equipAchievements.add((EquipAchievement)a);
            } else if (a instanceof AfterRollAchievement) {
               this.afterRollAchievements.add((AfterRollAchievement)a);
            }
         }
      }
   }

   public void resetUnlocks() {
      this.lockedUnlockables.clear();
      if (!com.tann.dice.Main.getSettings().isBypass()) {
         for (Achievement a : this.allAchievements) {
            if (!a.isAchieved()) {
               this.lockedUnlockables.addAll(Arrays.asList(a.getUnlockables()));
            }
         }
      }
   }

   public List<Achievement> getCompletedAchievements(Boolean secret) {
      if (secret == null) {
         return this.completedAchievements;
      } else {
         List<Achievement> result = new ArrayList<>();

         for (Achievement a : this.completedAchievements) {
            if (a.isChallenge() != secret) {
               result.add(a);
            }
         }

         return result;
      }
   }

   public List<Achievement> getCompletedAchievements() {
      return this.getCompletedAchievements(null);
   }

   public List<Achievement> getIncompleteAchievements() {
      ArrayList<Achievement> incomplete = new ArrayList<>(this.allAchievements);
      incomplete.removeAll(this.getCompletedAchievements());
      return incomplete;
   }

   public void unlockAllModeAchievements() {
      for (Achievement a : this.allAchievements) {
         if (!a.isAchieved() && a.getUnlockables().length > 0) {
            for (Unlockable unlockable : a.getUnlockables()) {
               if (unlockable instanceof Mode) {
                  this.achieveAchievement(a, false);
                  break;
               }
            }
         }
      }

      this.saveAll();
   }

   public void unlockAllChallenges() {
      for (Achievement a : this.allAchievements) {
         if (!a.isAchieved() && a.getUnlockables().length > 0) {
            this.achieveAchievement(a, false);
         }
      }

      this.saveAll();
   }

   public void saveAll() {
      com.tann.dice.Main.self().masterStats.saveAll();
   }

   void achieveAchievement(Achievement a) {
      this.achieveAchievement(a, true);
   }

   void achieveAchievement(Achievement a, boolean save) {
      if (!a.isAchieved()) {
         this.completedAchievements.add(a);
         a.setAchievedStateInternal(true);

         for (AchievementCompletionListener acl : this.listeners) {
            acl.onUnlock(a);
         }

         this.lockedUnlockables.removeAll(Arrays.asList(a.getUnlockables()));
         if (save) {
            this.updateAfterAchieve();
            this.saveAll();
            com.tann.dice.Main.getSettings().setLastAlmanacPage("ledger-unlock");
         }
      }
   }

   void resetAchievements() {
      for (Achievement a : this.allAchievements) {
         a.setAchievedStateInternal(false);
      }

      this.completedAchievements.clear();
      this.resetUnlocks();
   }

   public void unlockAll() {
      this.unlockAll(1.0F);
   }

   public void unlockAll(float chance) {
      this.resetAchievements();

      for (Achievement a : this.allAchievements) {
         if (Math.random() < chance) {
            this.achieveAchievement(a, false);
         }
      }

      this.saveAll();
   }

   public List<Achievement> getAllAchievements() {
      return this.allAchievements;
   }

   public List<Achievement> getAllTypedAchievements() {
      List<Achievement> result = new ArrayList<>();
      result.addAll(this.snapshotAchievements);
      result.addAll(this.statAchievements);
      result.addAll(this.metaAchievements);
      result.addAll(this.runEndAchievements);
      result.addAll(this.fightEndAchievements);
      result.addAll(this.equipAchievements);
      result.addAll(this.afterRollAchievements);
      return result;
   }

   public List<Achievement> getShownChallenges() {
      List<Achievement> incompleteChallenges = new ArrayList<>();

      for (Achievement incomplete : this.getIncompleteAchievements()) {
         if (incomplete.isChallenge() && incomplete.isCompletable()) {
            incompleteChallenges.add(incomplete);
         }
      }

      Collections.sort(incompleteChallenges, new Comparator<Achievement>() {
         public int compare(Achievement o1, Achievement o2) {
            return (int)Math.signum(o1.getDifficulty() - o2.getDifficulty());
         }
      });
      return incompleteChallenges.size() == 0 ? incompleteChallenges : incompleteChallenges.subList(0, Math.min(5, incompleteChallenges.size()));
   }

   public Map<String, Achievement> getAchievementMap() {
      Map<String, Achievement> result = new HashMap<>();

      for (Achievement a : this.getAllAchievements()) {
         result.put(a.getName(), a);
      }

      return result;
   }

   public void registerAchievementListener(AchievementCompletionListener listener) {
      for (int i = this.listeners.size() - 1; i >= 0; i--) {
         if (this.listeners.get(i).getClass() == listener.getClass()) {
            this.listeners.remove(i);
         }
      }

      this.listeners.add(listener);
   }

   public void load(List<String> completedAchievementStrings) {
      if (completedAchievementStrings.size() > 10) {
         String s = "First Boss";
         if (!completedAchievementStrings.contains(s)) {
            completedAchievementStrings.add(s);
         }
      }

      Map<String, Achievement> map = this.getAchievementMap();

      for (String s : completedAchievementStrings) {
         Achievement a = map.get(s);
         if (a == null) {
            TannLog.log("Failed to load unlocked chievo: " + s);
         } else {
            this.achieveAchievement(a, false);
         }
      }
   }

   public void updateAfterAchieve() {
      for (MetaAchievement a : this.metaAchievements) {
         if (!a.isAchieved() && a.onAchieveOther(this.completedAchievements)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void updateAfterCommand(StatSnapshot ss, Map<String, Stat> statMap) {
      for (SnapshotAchievement a : this.snapshotAchievements) {
         if (!a.isAchieved() && a.snapshotCheck(ss)) {
            this.achieveAchievement(a);
         }
      }

      for (StatAchievement ax : this.statAchievements) {
         if (!ax.isAchieved() && ax.statCheck(statMap)) {
            this.achieveAchievement(ax);
         }
      }
   }

   public void updateEndOfRound(StatSnapshot ss) {
      for (SnapshotAchievement a : this.snapshotAchievements) {
         if (!a.isAchieved() && a.snapshotCheck(ss)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void updateAfterSaveForStats(Map<String, Stat> mergedStats) {
      for (StatAchievement a : this.statAchievements) {
         if (!a.isAchieved() && a.statCheck(mergedStats)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void endOfFight(StatSnapshot ss, boolean victory) {
      for (FightEndAchievement a : this.fightEndAchievements) {
         if (!a.isAchieved() && a.endOfFightCheck(ss, victory)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void endOfRun(DungeonContext context, boolean victory, boolean background) {
      ContextConfig contextConfig = context.getContextConfig();
      Difficulty difficulty = null;
      if (contextConfig instanceof DifficultyConfig) {
         difficulty = ((DifficultyConfig)contextConfig).getDifficulty();
      }

      if (!background) {
         for (RunEndAchievement a : this.runEndAchievements) {
            if (!a.isAchieved() && a.runEndCheck(context, contextConfig, difficulty, victory)) {
               this.achieveAchievement(a);
            }
         }
      }
   }

   public void afterEquip(Party party) {
      for (EquipAchievement a : this.equipAchievements) {
         if (!a.isAchieved() && a.onEquip(party)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void allDiceLanded(List<EntSideState> states) {
      for (AfterRollAchievement a : this.afterRollAchievements) {
         if (!a.isAchieved() && a.allDiceLandedCheck(states)) {
            this.achieveAchievement(a);
         }
      }
   }

   public void loadAchievementsFromStrings(List<String> completedAchievementStrings) throws Exception {
      if (completedAchievementStrings.size() == 0) {
         throw new Exception("[purple]Found 0 achievements, cancelling load");
      } else {
         int previousAchievements = this.getCompletedAchievements().size();
         int achievementsInPaste = completedAchievementStrings.size();
         this.load(completedAchievementStrings);
         int newAchievements = this.getCompletedAchievements().size();
         int numNew = newAchievements - previousAchievements;
         String explanation = "[text]Found [yellow]"
            + achievementsInPaste
            + " achievements[cu].[n]"
            + (
               numNew == 0
                  ? "[purple]but none of them were new..."
                  : "Successfully-merged, added [green]" + numNew + "[cu] new " + Words.plural("achievement", numNew)
            );
         com.tann.dice.Main.self().setScreen(new TitleScreen());
         com.tann.dice.Main.getCurrentScreen().showDialog(explanation);
         this.updateAfterAchieve();
      }
   }

   public void saveAchievementsToClipboard() {
      MasterStatsData msd = new MasterStatsData(new ArrayList<>(), this.getCompletedAchievements());
      String json = com.tann.dice.Main.getJson().toJson(msd);
      int hashcode = json.hashCode();
      ClipboardUtils.copyWithSoundAndToast(json + "xxxxxxxxxxxxxxx" + hashcode);
   }

   public boolean isLocked(Unlockable u) {
      return this.lockedUnlockables.contains(u);
   }

   public void achieveRandom() {
      Achievement a = RandomCheck.checkedRandom(this.allAchievements, new Checker<Achievement>() {
         public boolean check(Achievement achievement) {
            return !UnlockManager.this.completedAchievements.contains(achievement);
         }
      }, null);
      if (a != null) {
         this.achieveAchievement(a, true);
      }
   }

   public void achieveFromMigration(String achName) {
      Achievement a = this.getAchievementMap().get(achName);
      if (a != null && !a.isAchieved()) {
         this.achieveAchievement(a, true);
      }
   }
}
