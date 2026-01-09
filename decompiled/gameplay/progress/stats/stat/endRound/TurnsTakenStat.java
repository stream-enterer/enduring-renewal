package com.tann.dice.gameplay.progress.stats.stat.endRound;

import com.tann.dice.gameplay.progress.StatSnapshot;

public class TurnsTakenStat extends EndRoundStat {
   public static String NAME = "turns-taken";

   public TurnsTakenStat() {
      super(NAME);
   }

   @Override
   public void endOfRound(StatSnapshot ss) {
      this.addToValue(1);
   }

   @Override
   public int getOrder() {
      return 10;
   }
}
