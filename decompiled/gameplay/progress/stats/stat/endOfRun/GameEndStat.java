package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameEndStat extends Stat {
   public GameEndStat(String name) {
      super(name);
   }

   public static Collection<? extends Stat> make() {
      List<Stat> stats = new ArrayList<>();
      stats.addAll(ModeWinStat.makeAll());
      stats.add(new TotalRunWinsStat());
      stats.add(new TotalLossesStat());
      stats.add(new HardestDifficultyVictoryStat());
      stats.addAll(DifficultyCompletionStat.makeAll());
      stats.addAll(SpeedrunStat.makeAll());
      stats.add(new ChoosePartyFlushStat());
      return stats;
   }

   public void endOfRun(DungeonContext context, boolean victory) {
   }
}
