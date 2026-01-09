package com.tann.dice.gameplay.trigger.global.choosable;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalGainChoosable extends Global {
   final Choosable choosable;

   public GlobalGainChoosable(Choosable choosable) {
      this.choosable = choosable;
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("Gain") + ": " + com.tann.dice.Main.t(this.choosable.describe());
   }

   @Override
   public void onPick(DungeonContext context) {
      ChoosableUtils.checkedOnChoose(this.choosable, context, "choosable global2");
   }

   @Override
   public boolean skipNotifyRandomReveal() {
      return false;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
