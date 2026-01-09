package com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.MetaEndOfRunStat;
import java.util.ArrayList;
import java.util.List;

public abstract class StreakStat extends MetaEndOfRunStat {
   protected transient ContextConfig cc;

   public StreakStat(ContextConfig cc, String name) {
      super(name);
      this.cc = cc;
   }

   public static List<StreakStat> makeAll() {
      List<StreakStat> result = new ArrayList<>();

      for (ContextConfig cc : Mode.getAllSaveBearingConfigs()) {
         if (!cc.skipStats()) {
            result.add(new CurrentStreakStat(cc));
            result.add(new BestStreakStat(cc));
         }
      }

      return result;
   }

   @Override
   public boolean isBoring() {
      return true;
   }
}
