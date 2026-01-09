package com.tann.dice.screens.dungeon.panels.tutorial;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DemoConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.listener.SnapshotChangeListener;
import com.tann.dice.gameplay.phase.NothingPhase;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseListen;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.StatUpdate;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.screens.dungeon.DungeonScreen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager implements SnapshotChangeListener, PhaseListen, StatUpdate {
   private static final String REPLACE_CC = "REPLACE_CC";
   private static final String REPLACE_DIFFICULTY = "REPLACE_DIFF";
   private static final String REPLACE_EXTRA_PHASE = "REPLACE_EXTRA_PHASE";
   private static final String base = "`{v:ignore,d:{n:1,cc:REPLACE_CC,c:REPLACE_DIFF,p:{h:[Fighter,Lazy,Thief,Defender,Defender]},l:{m:[Wolf,Wolf]}},s:1,c:[2033,2162,4],s:5222211,p:[REPLACE_EXTRA_PHASE0e;0]}`";
   Map<Class<? extends Phase>, List<TutorialItem>> items = new HashMap<>();
   List<TutorialItem> ALL = new ArrayList<>();
   public final TutorialHolder tutorialHolder;
   private boolean modeEnabled;
   Phase phase = new NothingPhase();
   List<TutorialItem> actives = new ArrayList<>();
   private List<TutorialItem> cached = null;

   private static String getExtraPhaseOverride(Difficulty d) {
      switch (d) {
         case Easy:
            return "\\\"cNumber#1;m2 hero hp@3mExtra Reroll;[green](Easy)[cu]\\\",";
         case Hard:
            return "\\\"cNumber#1;mtop.blank@3mFewer Reroll@3mmonster hp^1;[orange](Hard)[cu]\\\",";
         default:
            return "";
      }
   }

   public static String getTutOverride(DifficultyConfig diffCon) {
      if (!(diffCon instanceof ClassicConfig) && !(diffCon instanceof DemoConfig)) {
         return null;
      } else {
         Difficulty d = diffCon.getDifficulty();
         if (Math.abs(d.getBaseAmt()) > 5) {
            return null;
         } else {
            String cc = diffCon.getClass().getSimpleName();
            return "`{v:ignore,d:{n:1,cc:REPLACE_CC,c:REPLACE_DIFF,p:{h:[Fighter,Lazy,Thief,Defender,Defender]},l:{m:[Wolf,Wolf]}},s:1,c:[2033,2162,4],s:5222211,p:[REPLACE_EXTRA_PHASE0e;0]}`"
               .replaceAll("REPLACE_CC", cc)
               .replaceAll("REPLACE_DIFF", d.name())
               .replaceAll("REPLACE_EXTRA_PHASE", getExtraPhaseOverride(d));
         }
      }
   }

   public TutorialManager(DungeonScreen dungeonScreen) {
      this.modeEnabled = !dungeonScreen.getDungeonContext().getContextConfig().mode.skipStats();
      this.setupItems();
      this.tutorialHolder = new TutorialHolder();
      this.tutorialHolder.setPosition(-999.0F, -999.0F);
   }

   public void reset() {
      this.setupItems();
      this.newPhase(PhaseManager.get().getPhase());
   }

   private void setupItems() {
      this.cached = null;
      this.items = TutorialItem.makeAll();
      this.ALL = new ArrayList<>();

      for (List<TutorialItem> i : this.items.values()) {
         this.ALL.addAll(i);
      }

      List<Integer> completeds = com.tann.dice.Main.getSettings().getTutorialCompletion();
      List<TutorialItem> allItems = this.getAllItems();

      for (int i = 0; i < allItems.size(); i++) {
         TutorialItem ti = allItems.get(i);
         if (completeds.contains(i)) {
            ti.markCompleted();
         }

         if (!ti.complete) {
            ti.loadIn();
            if (ti.isComplete()) {
               this.saveTutorialState();
            }
         }
      }
   }

   @Override
   public void snapshotChanged(FightLog.Temporality temporality, Snapshot newSnapshot) {
      for (TutorialItem ti : this.ALL) {
         if (!ti.isComplete()) {
            ti.newSnapshot(newSnapshot);
         }
      }

      this.afterAction();
   }

   @Override
   public void updateAfterCommand(StatSnapshot ss, Map<String, Stat> statMap) {
      for (TutorialItem ti : this.ALL) {
         if (!ti.isComplete()) {
            ti.newStatsSnapshot(ss);
         }
      }

      this.afterAction();
   }

   @Override
   public void updateEndOfRound(StatSnapshot ss) {
   }

   @Override
   public void updateAllDiceLanded(List<EntSideState> states) {
   }

   @Override
   public void updateDiceRolled(int count) {
   }

   @Override
   public void endOfFight(StatSnapshot ss, boolean victory) {
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory, boolean background) {
   }

   public void onRoll(List<Ent> heroes) {
      for (TutorialItem ti : this.ALL) {
         if (!ti.isComplete()) {
            ti.onRoll(heroes);
         }
      }

      this.afterAction();
   }

   public void onAction(TutorialManager.TutorialAction type) {
      this.onAction(type, null);
   }

   public void onAction(TutorialManager.TutorialAction type, Object arg) {
      for (TutorialItem ti : this.ALL) {
         if (!ti.isComplete()) {
            ti.onAction(type, arg);
         }
      }

      this.afterAction();
   }

   public void onLock(List<Ent> heroes) {
      for (TutorialItem ti : this.ALL) {
         if (!ti.isComplete()) {
            ti.onLock(heroes);
         }
      }

      this.afterAction();
   }

   @Override
   public void newPhase(Phase phase) {
      this.showManagerForPhase(phase);
   }

   private void showManagerForPhase(Phase phase) {
      this.actives.clear();
      this.saveTutorialState();
      DungeonScreen ds = DungeonScreen.get();
      FightLog f = ds.getFightLog();
      this.phase = phase;
      List<TutorialItem> phaseItems = this.items.get(phase.getClass());
      if (!com.tann.dice.Main.justResized() && phaseItems != null) {
         for (TutorialItem ti : phaseItems) {
            if (this.isValid(ti, f) && (!ti.isComplex() || this.actives.size() <= 0)) {
               this.actives.add(ti);
               if (ti.isComplex() || this.actives.size() == 2) {
                  break;
               }
            }
         }

         if (this.actives.size() == 0 || !this.isEnabled()) {
            this.slideAwayHolder();
         }

         this.tutorialHolder.setItems(this.actives);
         if (this.actives.size() > 0) {
            for (TutorialItem tix : this.actives) {
               tix.layout();
            }

            if (this.isEnabled()) {
               this.tutorialHolder.clearActions();
               phase.positionTutorial(this.tutorialHolder);
            }
         }

         this.tutorialHolder.toFront();
      } else {
         this.slideAwayHolder();
      }
   }

   private List<TutorialItem> getAllItems() {
      if (this.cached == null) {
         this.cached = new ArrayList<>();

         for (List<TutorialItem> list : this.items.values()) {
            this.cached.addAll(list);
         }

         Collections.sort(this.cached, new Comparator<TutorialItem>() {
            public int compare(TutorialItem o1, TutorialItem o2) {
               return o1.getSortText().compareTo(o2.getSortText());
            }
         });
      }

      return this.cached;
   }

   void slideAwayHolder() {
      this.tutorialHolder.slideAway();
   }

   public boolean isEnabled() {
      return this.modeEnabled && com.tann.dice.Main.self().settings.isTutorialEnabled();
   }

   private boolean isValid(TutorialItem i, FightLog fightLog) {
      return !i.isComplete() && i.isValid(fightLog);
   }

   public void saveTutorialState() {
      List<TutorialItem> allItems = this.getAllItems();
      List<Integer> completeds = new ArrayList<>();

      for (int i = 0; i < allItems.size(); i++) {
         if (allItems.get(i).isComplete()) {
            completeds.add(i);
         }
      }

      com.tann.dice.Main.getSettings().setTutorialCompletion(completeds);
   }

   private void afterAction() {
      if (this.tutorialHolder.allComplete()) {
         this.tutorialHolder.slideAway(1.2F);
      }
   }

   public static int getNumTutorialElements() {
      DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
      return ds == null ? 30 : ds.getTutorialManager().ALL.size();
   }

   public static enum TutorialAction {
      DieInfo,
      HeroPanelInfo,
      MonsterPanelInfo,
      Undo,
      Equip,
      SelectMonster,
      SwapItems;
   }
}
