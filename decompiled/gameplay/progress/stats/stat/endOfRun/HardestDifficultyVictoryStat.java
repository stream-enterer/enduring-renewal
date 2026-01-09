package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;

public class HardestDifficultyVictoryStat extends GameEndStat {
   public static final String NAME = "hardest-difficulty-victory";

   public HardestDifficultyVictoryStat() {
      super("hardest-difficulty-victory");
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (victory && context.getContextConfig() instanceof DifficultyConfig) {
         Difficulty difficulty = ((DifficultyConfig)context.getContextConfig()).getDifficulty();
         if (difficulty == null) {
            TannLog.log("End of difficulty run with null difficulty?", TannLog.Severity.error);
         } else {
            this.setValue(Math.max(this.getValue(), Tann.indexOf(Difficulty.values(), difficulty)));
         }
      }
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return contextConfig instanceof DifficultyConfig;
   }
}
