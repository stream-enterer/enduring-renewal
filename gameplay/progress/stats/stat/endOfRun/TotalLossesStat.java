package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.context.DungeonContext;

public class TotalLossesStat extends GameEndStat {
   public static final String NAME = "total-losses";

   public TotalLossesStat() {
      super("total-losses");
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (!victory) {
         this.addToValue(1);
      }
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 0;
   }

   @Override
   public int getOrder() {
      return -9;
   }
}
