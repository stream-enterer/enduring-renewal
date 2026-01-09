package com.tann.dice.gameplay.progress.stats;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.EndOfFightStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.HeroDeath;
import com.tann.dice.gameplay.progress.stats.stat.endOfRun.GameEndStat;
import com.tann.dice.gameplay.progress.stats.stat.endRound.EndRoundStat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.MiscStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.progress.stats.stat.rollStat.RollPhaseStat;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonStatManager implements StatUpdate {
   private List<Stat> allStats;
   private List<EndOfFightStat> endOfFightStats = new ArrayList<>();
   private List<RollPhaseStat> rollPhaseStats = new ArrayList<>();
   private List<EndRoundStat> endRoundStats = new ArrayList<>();
   private List<GameEndStat> gameEndStats = new ArrayList<>();
   private List<MiscStat> miscStats = new ArrayList<>();
   private final DungeonContext dungeonContext;
   Map<String, Stat> cachedStatsMap;

   public DungeonStatManager(DungeonContext dungeonContext) {
      this.dungeonContext = dungeonContext;
      this.reset();
   }

   public void reset() {
      if (this.dungeonContext.skipStats()) {
         this.allStats = new ArrayList<>();
      } else {
         this.allStats = StatLib.makeAllStats(StatLib.StatSource.Dungeon, this.dungeonContext);

         for (int i = this.allStats.size() - 1; i >= 0; i--) {
            Stat s = this.allStats.get(i);
            if (!s.validFor(this.dungeonContext.getContextConfig())) {
               this.allStats.remove(s);
            }
         }

         filter(this.allStats, this.endOfFightStats, EndOfFightStat.class);
         filter(this.allStats, this.rollPhaseStats, RollPhaseStat.class);
         filter(this.allStats, this.endRoundStats, EndRoundStat.class);
         filter(this.allStats, this.gameEndStats, GameEndStat.class);
         filter(this.allStats, this.miscStats, MiscStat.class);
      }
   }

   private static <T> void filter(List<Stat> allStats, List<T> outList, Class<? extends Stat> clazz) {
      for (Stat s : allStats) {
         if (clazz.isInstance(s)) {
            outList.add((T)s);
         }
      }
   }

   @Override
   public void updateAfterCommand(StatSnapshot ss, Map<String, Stat> statMap) {
   }

   @Override
   public void updateEndOfRound(StatSnapshot ss) {
      for (EndRoundStat s : this.endRoundStats) {
         s.endOfRound(ss);
      }
   }

   @Override
   public void updateAllDiceLanded(List<EntSideState> states) {
      for (RollPhaseStat rps : this.rollPhaseStats) {
         rps.allDiceLanded(states);
      }
   }

   @Override
   public void updateDiceRolled(int count) {
      for (RollPhaseStat rps : this.rollPhaseStats) {
         rps.heroDiceRolled(count);
      }
   }

   @Override
   public void endOfFight(StatSnapshot ss, boolean victory) {
      if (!this.dungeonContext.skipStats()) {
         this.updateEndOfRound(ss);

         for (EndOfFightStat s : this.endOfFightStats) {
            try {
               s.updateEndOfFight(ss, victory);
            } catch (Exception var6) {
               TannLog.error(var6, "updating stats eof");
            }
         }
      }
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory, boolean background) {
      for (GameEndStat s : this.gameEndStats) {
         s.endOfRun(context, victory);
      }
   }

   public List<Stat> getAllStats() {
      return this.allStats;
   }

   public Map<String, Stat> getStatsMap() {
      if (this.cachedStatsMap == null) {
         this.cachedStatsMap = new HashMap<>();

         for (Stat s : this.getAllStats()) {
            this.cachedStatsMap.put(s.getName(), s);
         }
      }

      return this.cachedStatsMap;
   }

   public void removeStat(Stat s) {
      this.getAllStats().remove(s);
      this.endOfFightStats.remove(s);
      this.rollPhaseStats.remove(s);
      this.endRoundStats.remove(s);
      this.miscStats.remove(s);
      this.cachedStatsMap = null;
   }

   public void addStat(HeroDeath heroDeath) {
      this.getAllStats().add(heroDeath);
      this.endOfFightStats.add(heroDeath);
      this.cachedStatsMap = null;
   }

   public void onUndo(int undosInARow) {
      for (MiscStat ms : this.miscStats) {
         ms.onUndo(undosInARow);
      }
   }

   public void onChallenge(boolean accepted) {
      for (MiscStat ms : this.miscStats) {
         ms.onChallenge(accepted);
      }

      this.dungeonContext.specialCachedAchievementCheck();
   }

   public void surrenderLog(boolean accepted) {
      for (MiscStat ms : this.miscStats) {
         ms.onSurrenderChoice(accepted);
      }
   }

   public void clearStats(List<Stat> statsToRefresh) {
      for (Stat s : statsToRefresh) {
         s.reset();
      }
   }

   public void pickDelta(Choosable c, boolean pick) {
      String name = PickStat.nameFor(c);
      Map<String, Stat> map = this.getStatsMap();
      Stat s = map.get(name);
      if (s == null) {
         s = new PickStat(c);
         this.allStats.add(s);
         map.put(name, s);
      }

      s.addToValue(pick ? 1 : 65536);
   }
}
