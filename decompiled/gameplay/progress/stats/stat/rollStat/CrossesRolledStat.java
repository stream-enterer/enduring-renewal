package com.tann.dice.gameplay.progress.stats.stat.rollStat;

import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import java.util.List;

public class CrossesRolledStat extends RollPhaseStat {
   public static final String NAME = "crosses-rolled";

   public CrossesRolledStat() {
      super("crosses-rolled");
   }

   @Override
   public void allDiceLanded(List<EntSideState> states) {
      for (EntSideState ess : states) {
         if (ess.getCalculatedEffect().getType() == EffType.Blank) {
            this.addToValue(1);
         }
      }
   }

   @Override
   public void heroDiceRolled(int count) {
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 1;
   }

   @Override
   public int getOrder() {
      return 1;
   }
}
