package com.tann.dice.gameplay.trigger.global.eff;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;

public class GlobalStartOfCombat extends GlobalTurnRequirement {
   public GlobalStartOfCombat(Eff eff) {
      super(new TurnRequirementN(1), new GlobalStartTurnEff(eff));
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
