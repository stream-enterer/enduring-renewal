package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogKey extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take keywords from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      return new EffBill(e).keywords(left.getKeywords()).bEff();
   }
}
