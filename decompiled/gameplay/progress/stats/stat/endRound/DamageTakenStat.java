package com.tann.dice.gameplay.progress.stats.stat.endRound;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;

public class DamageTakenStat extends EndRoundStat {
   public static String NAME = "dmg-taken";

   public DamageTakenStat() {
      super(NAME);
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 1;
   }

   @Override
   public void endOfRound(StatSnapshot ss) {
      for (EntState es : ss.afterCommand.getStates(true, null)) {
         this.addToValue(es.getDamageTakenThisTurn());
      }
   }
}
