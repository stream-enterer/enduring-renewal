package com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.EndOfFightStat;
import java.util.ArrayList;
import java.util.List;

public class KillsStat extends EndOfFightStat {
   final transient MonsterType type;

   public static String getStatName(MonsterType type) {
      return type.getName(false) + "-k";
   }

   public KillsStat(MonsterType type) {
      super(getStatName(type));
      this.type = type;
   }

   public static List<EndOfFightStat> getAllStats() {
      List<EndOfFightStat> stats = new ArrayList<>();

      for (MonsterType t : MonsterTypeLib.getMasterCopy()) {
         stats.add(new KillsStat(t));
      }

      return stats;
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      int count = 0;

      for (EntState es : ss.afterCommand.getStates(false, true)) {
         EntType et = es.getEnt().getEntType();
         if (et.sameForStats(this.type) && (!es.isFled() || this.type.hp >= 30)) {
            count++;
         }
      }

      return count;
   }

   @Override
   public boolean isBoring() {
      return true;
   }
}
