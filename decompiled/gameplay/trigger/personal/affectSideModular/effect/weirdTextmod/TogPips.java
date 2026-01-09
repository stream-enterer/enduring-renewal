package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogPips extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take pips from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).value(left.getValue()).bEff();
   }
}
