package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntState;

public class Gaze extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Whenever you use your dice, take damage equal to the number of dice used this turn";
   }

   @Override
   public void afterUse(EntState entState, EntSide side) {
      int diceUsed = entState.getSnapshot().getNumDiceUsedThisTurn();
      entState.damage(diceUsed, null, null, null);
      super.afterUse(entState, side);
   }

   @Override
   public boolean allowOverheal() {
      return true;
   }
}
