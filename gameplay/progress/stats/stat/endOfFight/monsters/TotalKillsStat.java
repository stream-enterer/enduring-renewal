package com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.EndOfFightStat;
import java.util.Arrays;
import java.util.Collection;

public class TotalKillsStat extends EndOfFightStat {
   public static final String NAME = "total-kills";

   public TotalKillsStat() {
      super("total-kills");
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      int total = 0;

      for (EntState es : ss.future.getStates(false, true)) {
         if (!es.isFled()) {
            total++;
         }
      }

      return total;
   }

   public static Collection<? extends EndOfFightStat> getAllStats() {
      return Arrays.asList(new TotalKillsStat());
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 0;
   }

   @Override
   public int getOrder() {
      return 6;
   }
}
