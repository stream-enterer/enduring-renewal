package com.tann.dice.gameplay.progress.stats.stat.endRound;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;

public class HealingStat extends EndRoundStat {
   public static final String NAME = "total-healing";

   public HealingStat() {
      super("total-healing");
   }

   @Override
   public void endOfRound(StatSnapshot ss) {
      int total = 0;

      for (EntState es : ss.afterCommand.getStates(true, null)) {
         total += es.getHealingThisTurn();
      }

      this.addToValue(total);
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 1;
   }

   @Override
   public int getOrder() {
      return 4;
   }
}
