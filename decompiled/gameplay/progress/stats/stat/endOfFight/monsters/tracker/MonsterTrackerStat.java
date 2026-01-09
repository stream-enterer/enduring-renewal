package com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.tracker;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.EndOfFightStat;
import java.util.ArrayList;
import java.util.List;

public class MonsterTrackerStat extends EndOfFightStat {
   transient boolean victory;
   transient MonsterType monsterType;

   public MonsterTrackerStat(MonsterType monsterType, boolean victory) {
      super(getNameFrom(monsterType, victory));
      this.monsterType = monsterType;
      this.victory = victory;
   }

   public static String getNameFrom(MonsterType monsterType, boolean victory) {
      return monsterType.getName(false) + (victory ? "-v" : "-d");
   }

   @Override
   public boolean isBoring() {
      return true;
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      boolean victory = ss.afterCommand.isVictory() || !ss.afterCommand.isLoss();
      if (victory != this.victory) {
         return 0;
      } else {
         List<MonsterType> monsts = ss.context.getCurrentLevel().getMonsterList();

         for (int i = 0; i < monsts.size(); i++) {
            if (monsts.get(i).sameForStats(this.monsterType)) {
               return 1;
            }
         }

         return 0;
      }
   }

   public static List<MonsterTrackerStat> getAllStats() {
      List<MonsterTrackerStat> result = new ArrayList<>();

      for (MonsterType mt : MonsterTypeLib.getMasterCopy()) {
         result.add(new MonsterTrackerStat(mt, true));
         result.add(new MonsterTrackerStat(mt, false));
      }

      return result;
   }
}
