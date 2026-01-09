package com.tann.dice.gameplay.progress.stats.stat.endOfFight;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.stats.stat.StatMergeType;
import java.util.ArrayList;
import java.util.List;

public class FurthestReachedStat extends EndOfFightStat {
   public FurthestReachedStat(String name) {
      super(name);
   }

   @Override
   public void updateEndOfFight(StatSnapshot ss, boolean victory) {
      if (!ss.context.isBugged()) {
         if (victory && !ss.context.isAtLastLevel()) {
            this.setValue(Math.max(this.getValue(), ss.context.getCurrentLevelNumber() + 1));
         }
      }
   }

   @Override
   public int getValueFromSnapshot(StatSnapshot ss) {
      return 0;
   }

   public static String getName(ContextConfig cc) {
      return cc.getSpecificKey("furthest31");
   }

   public static List<EndOfFightStat> makeForAllConfigs() {
      List<EndOfFightStat> result = new ArrayList<>();

      for (ContextConfig contextConfig : Mode.getAllSaveBearingConfigs()) {
         if (!contextConfig.skipStats()) {
            result.add(new FurthestReachedStat(getName(contextConfig)));
         }
      }

      return result;
   }

   @Override
   protected StatMergeType getMergeType() {
      return StatMergeType.Highest;
   }

   @Override
   public boolean isBoring() {
      return true;
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return getName(contextConfig).equals(this.getName());
   }
}
