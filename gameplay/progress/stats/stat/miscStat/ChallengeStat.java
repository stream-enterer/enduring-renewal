package com.tann.dice.gameplay.progress.stats.stat.miscStat;

public class ChallengeStat extends MiscStat {
   final transient boolean accept;

   public ChallengeStat(boolean accept) {
      super(GET_NAME(accept));
      this.accept = accept;
   }

   public static final String GET_NAME(boolean accept) {
      return "challenge-" + accept;
   }

   @Override
   public void onChallenge(boolean accepted) {
      if (this.accept == accepted) {
         this.addToValue(1);
      }
   }
}
