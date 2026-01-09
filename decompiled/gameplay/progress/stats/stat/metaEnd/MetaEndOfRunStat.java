package com.tann.dice.gameplay.progress.stats.stat.metaEnd;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak.StreakStat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MetaEndOfRunStat extends Stat {
   public MetaEndOfRunStat(String name) {
      super(name);
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return false;
   }

   public static Collection<? extends Stat> make() {
      List<Stat> stats = new ArrayList<>();
      stats.addAll(StreakStat.makeAll());
      return stats;
   }
}
