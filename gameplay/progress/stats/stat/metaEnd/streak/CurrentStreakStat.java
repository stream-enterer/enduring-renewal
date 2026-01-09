package com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;

public class CurrentStreakStat extends StreakStat {
   public CurrentStreakStat(ContextConfig cc) {
      super(cc, getName(cc));
   }

   public static String getName(ContextConfig cc) {
      return cc.getSpecificKey("current--streak");
   }

   @Override
   public void metaEndOfRun(DungeonContext context, boolean victory) {
      if (!context.isBugged()) {
         if (this.getName().equalsIgnoreCase(getName(context.getContextConfig()))) {
            if (victory) {
               this.setValue(this.getValue() + 1);
            } else {
               this.setValue(0);
            }
         }
      }
   }
}
