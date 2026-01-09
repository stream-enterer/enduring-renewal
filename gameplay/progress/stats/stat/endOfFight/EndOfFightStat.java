package com.tann.dice.gameplay.progress.stats.stat.endOfFight;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.KillsStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.TotalKillsStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.tracker.MonsterTrackerStat;
import java.util.ArrayList;
import java.util.List;

public abstract class EndOfFightStat extends Stat {
   public EndOfFightStat(String name) {
      super(name);
   }

   public void updateEndOfFight(StatSnapshot ss, boolean victory) {
      this.addToValue(this.getValueFromSnapshot(ss));
   }

   public abstract int getValueFromSnapshot(StatSnapshot var1);

   public static List<EndOfFightStat> make(StatLib.StatSource statSource, DungeonContext maybe) {
      List<EndOfFightStat> result = new ArrayList<>();
      result.addAll(HeroDeath.getAllStats(statSource, maybe));
      result.add(new TotalDeathsStat());
      result.add(new TotalBattleWinsStat());
      result.addAll(KillsStat.getAllStats());
      result.addAll(TotalKillsStat.getAllStats());
      result.addAll(MonsterTrackerStat.getAllStats());
      result.addAll(FurthestReachedStat.makeForAllConfigs());
      return result;
   }
}
