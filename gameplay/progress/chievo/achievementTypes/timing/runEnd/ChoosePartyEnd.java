package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class ChoosePartyEnd extends RunEndAchievement {
   final int amt;

   public ChoosePartyEnd(Difficulty difficulty, int amt, Unlockable... unlockables) {
      super(
         new RunEndCondition(Mode.CHOOSE_PARTY, difficulty),
         Mode.CHOOSE_PARTY.getTextButtonName() + " " + amt + "x",
         "with " + amt + "+ of one hero colour",
         unlockables
      );
      this.amt = amt;
      this.diff((amt - 2) * 2);
   }

   @Override
   protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
      return RunEndAchievement.getMaxOfOneColour(context.getParty()) >= this.amt;
   }

   public static List<ChoosePartyEnd> makeAllCPE() {
      return Arrays.asList(
         new ChoosePartyEnd(Difficulty.Normal, 3),
         new ChoosePartyEnd(Difficulty.Hard, 4, ItemLib.byName("red flag")),
         new ChoosePartyEnd(Difficulty.Unfair, 5, ItemLib.byName("lightning rod")),
         new ChoosePartyEnd(Difficulty.Brutal, 6),
         new ChoosePartyEnd(Difficulty.Hell, 7)
      );
   }
}
