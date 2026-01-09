package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogUnt extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take untargeted effect from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      Eff toUnt = left.needsTarget() ? left.getBonusUntargetedEffect() : left;
      return new EffBill(e).bonusUntargeted(toUnt).bEff();
   }
}
