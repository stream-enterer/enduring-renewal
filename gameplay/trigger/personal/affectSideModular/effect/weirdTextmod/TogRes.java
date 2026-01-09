package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;

public class TogRes extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take restrictions from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).restrict(EffUtils.getRestrictionsForTog(left).toArray(new ConditionalRequirement[0])).bEff();
   }
}
