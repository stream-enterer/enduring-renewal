package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogMult extends BasicTextmodToggle {
   @Override
   public String describe() {
      return "transform 'targeting restrictions' into 'x2 conditionals'";
   }

   @Override
   public Eff alterEff(Eff e) {
      return new EffBill(e).conMult(e.getRestrictions(true)).clearRestr().bEff();
   }
}
