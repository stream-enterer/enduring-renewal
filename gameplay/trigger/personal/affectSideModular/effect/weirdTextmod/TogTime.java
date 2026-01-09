package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;

public class TogTime extends BasicTextmodToggle {
   @Override
   public String describe() {
      return "buff duration toggles inf/1";
   }

   @Override
   public Eff alterEff(Eff e) {
      e = e.copy();
      if (e.getType() == EffType.Buff && e.getBuff() != null) {
         e.getBuff().turns = e.getBuff().turns == 1 ? -1 : 1;
      }

      return e;
   }
}
