package com.tann.dice.gameplay.progress.stats.stat.miscStat;

public class SurrenderChoiceStat extends MiscStat {
   transient boolean accept;

   public SurrenderChoiceStat(boolean accept) {
      super(NAME(accept));
      this.accept = accept;
   }

   public static String NAME(boolean accept) {
      return "surr-" + accept;
   }

   @Override
   public void onSurrenderChoice(boolean accepted) {
      if (accepted == this.accept) {
         this.addToValue(1);
      }
   }
}
