package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;

public class AfterUseDiceEffect extends PersonalEffContainer {
   final Eff eff;

   public AfterUseDiceEffect(Eff eff) {
      super(eff);
      this.eff = eff;
   }

   public AfterUseDiceEffect(EffBill e) {
      this(e.bEff());
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "After I use my dice, " + this.eff.describe().toLowerCase();
   }

   @Override
   public void afterUse(EntState entState, EntSide side) {
      entState.getSnapshot().untargetedUse(this.eff, entState.getEnt());
   }
}
