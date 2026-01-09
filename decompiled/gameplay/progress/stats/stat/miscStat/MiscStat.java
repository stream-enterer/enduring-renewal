package com.tann.dice.gameplay.progress.stats.stat.miscStat;

import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.Arrays;
import java.util.Collection;

public abstract class MiscStat extends Stat {
   public MiscStat(String name) {
      super(name);
   }

   public static Collection<? extends Stat> make() {
      return Arrays.asList(
         new UndoCountStat(), new SurrenderChoiceStat(false), new SurrenderChoiceStat(true), new ChallengeStat(false), new ChallengeStat(true)
      );
   }

   public void onUndo(int undosInARow) {
   }

   public void onSurrenderChoice(boolean accepted) {
   }

   public void onChallenge(boolean accepted) {
   }
}
