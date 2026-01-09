package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogOrf extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take left side as friendly targeting effect";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).or(new EffBill(left), new EffBill(e)).bEff();
   }
}
