package com.tann.dice.gameplay.progress.stats.stat.endRound;

import com.tann.dice.gameplay.progress.StatSnapshot;

public class SpellsCastStat extends EndRoundStat {
   public static final String NAME = "spells-cast";

   public SpellsCastStat() {
      super("spells-cast");
   }

   @Override
   public void endOfRound(StatSnapshot ss) {
      this.addToValue(ss.afterCommand.getNumAbilitiesUsed());
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 1;
   }

   @Override
   public int getOrder() {
      return 6;
   }
}
