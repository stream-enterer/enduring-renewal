package com.tann.dice.gameplay.trigger.personal.eff;

import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;

public class Truce extends EndOfTurnEff {
   public Truce() {
      super(new EffBill().self().flee());
   }

   @Override
   public String getImageName() {
      return "truce";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "At the end of the turn, if no damage was dealt to any monster, I flee.";
   }

   @Override
   public void endOfTurn(EntState entState) {
      int totalD = 0;

      for (EntState state : entState.getSnapshot().getStates(false, null)) {
         totalD += state.getBlockableDamageTaken();
      }

      if (totalD == 0) {
         super.endOfTurn(entState);
      }
   }
}
