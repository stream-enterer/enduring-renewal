package com.tann.dice.gameplay.progress.stats.stat.endOfFight;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;

public class TotalDeathsStat extends EndOfFightStat {
   public static final String NAME = "total-deaths";

   public TotalDeathsStat() {
      super("total-deaths");
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      int result = 0;

      for (EntState es : ss.afterCommand.getStates(true, null)) {
         result += es.getDeathsForStats();
      }

      return result;
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 0;
   }

   @Override
   public int getOrder() {
      return 5;
   }
}
