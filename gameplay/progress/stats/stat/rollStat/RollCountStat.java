package com.tann.dice.gameplay.progress.stats.stat.rollStat;

import com.tann.dice.gameplay.fightLog.EntSideState;
import java.util.List;

public class RollCountStat extends RollPhaseStat {
   public static final String NAME = "dice-rolled";

   public RollCountStat() {
      super("dice-rolled");
   }

   @Override
   public void allDiceLanded(List<EntSideState> states) {
   }

   @Override
   public void heroDiceRolled(int count) {
      this.addToValue(count);
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 0;
   }

   @Override
   public int getOrder() {
      return 0;
   }
}
