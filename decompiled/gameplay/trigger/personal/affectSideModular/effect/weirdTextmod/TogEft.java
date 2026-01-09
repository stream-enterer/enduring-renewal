package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogEft extends LeftTextmodToggle {
   @Override
   public String describe() {
      return "take eft from left side";
   }

   @Override
   public Eff alterEff(Eff e, Eff left) {
      switch (left.getType()) {
         case Summon:
            return new EffBill(e).summon(left.getSummonType(), left.getValue()).bEff();
         case Buff:
            return new EffBill(e).buff(left.getBuffAndCopy()).bEff();
         default:
            return new EffBill(e).type(left.getType()).bEff();
      }
   }
}
