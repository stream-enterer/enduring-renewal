package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogTarg extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take targeting from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).targetType(left.getTargetingType()).bEff();
   }
}
