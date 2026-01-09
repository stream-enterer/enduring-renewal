package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endRound.DamageTakenStat;
import com.tann.dice.gameplay.progress.stats.stat.endRound.TurnsTakenStat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.UndoCountStat;
import java.util.Arrays;
import java.util.List;

public class RunEndStatAchievement extends RunEndAchievement {
   final transient String statName;
   final transient int target;
   final transient Boolean below;

   public RunEndStatAchievement(String statName, int target, Boolean below, Unlockable... unlockables) {
      this(statName, target, below, new RunEndCondition(Mode.CLASSIC, null), unlockables);
   }

   public RunEndStatAchievement(String statName, int target, Boolean below, RunEndCondition runEndCondition, Unlockable... unlockables) {
      super(runEndCondition, TITLE_FOR(statName, target, below), DESC_FOR(statName, target, below, runEndCondition), unlockables);
      this.target = target;
      this.below = below;
      this.statName = statName;
      Stat s = com.tann.dice.Main.self().masterStats.getStat(statName);
      if (s == null) {
         throw new RuntimeException("Invalid stat achievement: " + statName);
      } else {
         this.diff(9.0F);
      }
   }

   private static String tag(Boolean below, boolean concise) {
      if (below == null) {
         return "";
      } else if (concise) {
         return below ? "<= " : ">= ";
      } else {
         return below ? "or fewer " : "or more ";
      }
   }

   private static String TITLE_FOR(String statName, int target, Boolean below) {
      return statName + " " + tag(below, true) + target;
   }

   private static String DESC_FOR(String statName, int target, Boolean below, RunEndCondition runEndCondition) {
      return "with " + target + " " + tag(below, false) + statName;
   }

   @Override
   protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
      Stat s = context.getStatsManager().getStatsMap().get(this.statName);
      if (s == null) {
         return false;
      } else if (this.below == null) {
         return s.getValue() == this.target;
      } else {
         return this.below ? s.getValue() <= this.target : s.getValue() >= this.target;
      }
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new RunEndStatAchievement(TurnsTakenStat.NAME, 40, true).diff(9.0F),
         new RunEndStatAchievement(TurnsTakenStat.NAME, 30, true),
         new RunEndStatAchievement(TurnsTakenStat.NAME, 20, true),
         new RunEndStatAchievement("total-deaths", 0, null),
         new RunEndStatAchievement(UndoCountStat.NAME, 0, null),
         new RunEndStatAchievement("crosses-rolled", 0, null),
         new RunEndStatAchievement("total-kills", 0, null),
         new RunEndStatAchievement(DamageTakenStat.NAME, 0, null),
         new RunEndStatAchievement("total-blocked", 0, null)
      );
   }
}
