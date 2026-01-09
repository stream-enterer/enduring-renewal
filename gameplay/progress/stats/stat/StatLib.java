package com.tann.dice.gameplay.progress.stats.stat;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.EndOfFightStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfRun.GameEndStat;
import com.tann.dice.gameplay.progress.stats.stat.endRound.EndRoundStat;
import com.tann.dice.gameplay.progress.stats.stat.leaderboardStat.LeaderboardStat;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.MetaEndOfRunStat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.MiscStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.progress.stats.stat.rollStat.RollPhaseStat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StatLib {
   public static List<Stat> makeAllStats() {
      return makeAllStats(null);
   }

   public static List<Stat> makeAllStats(StatLib.StatSource statSource) {
      return makeAllStats(statSource, null);
   }

   public static List<Stat> makeAllStats(StatLib.StatSource statSource, DungeonContext maybe) {
      List<Stat> stats = new ArrayList<>();
      stats.addAll(EndOfFightStat.make(statSource, maybe));
      stats.addAll(PickStat.make(statSource));
      stats.addAll(GameEndStat.make());
      stats.addAll(EndRoundStat.make());
      stats.addAll(RollPhaseStat.make());
      stats.addAll(MiscStat.make());
      stats.addAll(MetaEndOfRunStat.make());
      stats.addAll(LeaderboardStat.make());
      return stats;
   }

   public static Map<String, Stat> makeStatsMap(List<Stat> allStats) {
      Map<String, Stat> map = new HashMap<>();

      for (Stat s : allStats) {
         map.put(s.getName(), s);
      }

      return map;
   }

   public static List<Stat> getNonZeroStats(List<Stat> stats) {
      List<Stat> result = new ArrayList<>();

      for (Stat s : stats) {
         if (s.getValue() > 0) {
            result.add(s);
         }
      }

      return result;
   }

   public static List<Stat> copy(List<Stat> nonZeroStats) {
      List<Stat> result = new ArrayList<>();

      for (Stat s : nonZeroStats) {
         Stat n = new Stat(s.getName());
         n.setValue(s.getValue());
         result.add(n);
      }

      return result;
   }

   public static void mergeStats(List<Stat> base, List<Stat> add) {
      mergeStats(base, add, false);
   }

   public static void mergeStats(List<Stat> base, List<Stat> add, boolean addNews) {
      Map<String, Stat> baseMap = makeStatsMap(base);

      for (int addIndex = 0; addIndex < add.size(); addIndex++) {
         Stat addStat = add.get(addIndex);
         Stat baseStat = baseMap.get(addStat.getName());
         if (baseStat != null) {
            baseStat.merge(addStat);
         } else if (addNews) {
            base.add(addStat);
         }
      }
   }

   public static enum StatSource {
      Master,
      Dungeon;
   }
}
