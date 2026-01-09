package com.tann.dice.gameplay.progress.stats.stat.endOfFight;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.StatLib;
import java.util.ArrayList;
import java.util.List;

public class HeroDeath extends EndOfFightStat {
   final transient int index;

   public HeroDeath(int index) {
      super(getNameFromIndex(index));
      this.index = index;
   }

   public static String getNameFromIndex(int index) {
      return index + "-deaths";
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      return ss.afterCommand.getStates(true, null).get(this.index).getDeathsForStats();
   }

   public static List<HeroDeath> getAllStats(StatLib.StatSource statSource, DungeonContext maybe) {
      if (statSource != StatLib.StatSource.Master && maybe != null) {
         List<HeroDeath> result = new ArrayList<>();

         for (int i = 0; i < maybe.getParty().getHeroes().size(); i++) {
            result.add(new HeroDeath(i));
         }

         return result;
      } else {
         return new ArrayList<>();
      }
   }
}
