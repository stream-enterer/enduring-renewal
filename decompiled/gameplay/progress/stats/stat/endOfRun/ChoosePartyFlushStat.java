package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ChoosePartyConfig;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd.RunEndAchievement;
import com.tann.dice.gameplay.progress.stats.stat.StatMergeType;

public class ChoosePartyFlushStat extends GameEndStat {
   public static final String NAME = "custom-party-flush";

   public ChoosePartyFlushStat() {
      super("custom-party-flush");
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (victory && context.getContextConfig() instanceof ChoosePartyConfig) {
         Party p = context.getParty();
         if (RunEndAchievement.getMaxOfOneColour(p) == 5) {
            HeroCol first = context.getParty().getHeroes().get(0).getHeroCol();
            int heroColIndex = first.ordinal();
            int mask = 1 << heroColIndex;
            this.setValue(this.getValue() | mask);
         }
      }
   }

   @Override
   protected StatMergeType getMergeType() {
      return StatMergeType.BitMerge;
   }
}
