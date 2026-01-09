package com.tann.dice.gameplay.progress.stats.stat.endOfFight;

import com.tann.dice.gameplay.progress.StatSnapshot;

public class TotalBattleWinsStat extends EndOfFightStat {
   public static String NAME = "fights-won";

   public TotalBattleWinsStat() {
      super(NAME);
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      return ss.afterCommand.isVictory() ? 1 : 0;
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 0;
   }

   @Override
   public int getOrder() {
      return -6;
   }
}
