package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class Sidesc extends BasicTextmodToggle {
   public static String NOKEYWORD = "[nokeyword]";
   final String desc;

   public Sidesc(String desc) {
      this.desc = desc;
   }

   @Override
   public String describe() {
      return "override side description to " + this.desc;
   }

   @Override
   public Eff alterEff(Eff e) {
      return new EffBill(e).overrideDescription(this.calcDesc(e)).bEff();
   }

   private String calcDesc(Eff e) {
      return this.desc;
   }
}
