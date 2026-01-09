package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.BitStat;
import java.util.ArrayList;
import java.util.List;

public class ModeWinStat extends GameEndStat {
   public ModeWinStat(String key) {
      super(key);
   }

   public static List<Stat> makeAll() {
      List<Stat> result = new ArrayList<>();

      for (ContextConfig cc : Mode.getAllSaveBearingConfigs()) {
         if (!cc.skipStats()) {
            result.add(new ModeWinStat(getName(cc)));
         }
      }

      return result;
   }

   public static String getName(ContextConfig cc) {
      return cc.getSpecificKey("w");
   }

   public static int val(Stat s, boolean loss) {
      return BitStat.val(s.getValue(), loss);
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (this.getName().equalsIgnoreCase(getName(context.getContextConfig()))) {
         this.addToValue(victory ? 1 : 65536);
      }
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return this.getName().equalsIgnoreCase(getName(contextConfig));
   }
}
