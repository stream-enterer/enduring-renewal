package com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;

public class BestStreakStat extends StreakStat {
   public BestStreakStat(ContextConfig cc) {
      super(cc, getName(cc));
   }

   public static String getName(ContextConfig cc) {
      return cc.getSpecificKey("best-streak31");
   }

   @Override
   public void metaEndOfRun(DungeonContext context, boolean victory) {
      if (this.getName().equalsIgnoreCase(getName(context.getContextConfig()))) {
         this.setValue(Math.max(this.getValue(), com.tann.dice.Main.self().masterStats.getStat(CurrentStreakStat.getName(this.cc)).getValue()));
      }
   }
}
