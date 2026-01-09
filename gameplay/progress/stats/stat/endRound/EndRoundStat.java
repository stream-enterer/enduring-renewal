package com.tann.dice.gameplay.progress.stats.stat.endRound;

import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.ArrayList;
import java.util.List;

public abstract class EndRoundStat extends Stat {
   public EndRoundStat(String name) {
      super(name);
   }

   public abstract void endOfRound(StatSnapshot var1);

   public static List<Stat> make() {
      List<Stat> result = new ArrayList<>();
      result.add(new SpellsCastStat());
      result.add(new TurnsTakenStat());
      result.add(new BlockedStat());
      result.add(new HealingStat());
      result.add(new DamageTakenStat());
      return result;
   }
}
