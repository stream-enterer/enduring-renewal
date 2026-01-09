package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class TogFriend extends BasicTextmodToggle {
   @Override
   public String describe() {
      return "toggle friendliness type friend/foe";
   }

   @Override
   public Eff alterEff(Eff e) {
      return e.isFriendly() ? new EffBill(e).enemy().bEff() : new EffBill(e).friendly().bEff();
   }
}
