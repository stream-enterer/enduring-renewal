package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogVis extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take visual from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).visual(left.getVisual()).bEff();
   }
}
