package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatMergeType;
import java.util.Arrays;
import java.util.List;

public class DifficultyCompletionStat extends GameEndStat {
   transient Difficulty difficulty;

   public DifficultyCompletionStat(Difficulty difficulty) {
      super(getNameFromDifficulty(difficulty));
      this.difficulty = difficulty;
   }

   public static String getNameFromDifficulty(Difficulty difficulty) {
      return "curse-completion-" + difficulty.name();
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (victory
         && context.getContextConfig() instanceof DifficultyConfig
         && ((DifficultyConfig)context.getContextConfig()).getDifficulty() == this.difficulty) {
         int curseMask = 0;
         this.setValue(this.getValue() | curseMask);
      }
   }

   public static List<Stat> makeAll() {
      return Arrays.asList(new DifficultyCompletionStat(Difficulty.Hard), new DifficultyCompletionStat(Difficulty.Unfair));
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return contextConfig instanceof DifficultyConfig && ((DifficultyConfig)contextConfig).getDifficulty() == this.difficulty;
   }

   @Override
   protected StatMergeType getMergeType() {
      return StatMergeType.BitMerge;
   }
}
