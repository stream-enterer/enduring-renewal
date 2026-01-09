package com.tann.dice.gameplay.progress.stats.stat.rollStat;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RollPhaseStat extends Stat {
   public RollPhaseStat(String name) {
      super(name);
   }

   public static Collection<? extends Stat> make() {
      return Arrays.asList(new CrossesRolledStat(), new RollCountStat());
   }

   public abstract void allDiceLanded(List<EntSideState> var1);

   public abstract void heroDiceRolled(int var1);
}
