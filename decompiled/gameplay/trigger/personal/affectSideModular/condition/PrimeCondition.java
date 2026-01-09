package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.util.Tann;

public class PrimeCondition extends AffectSideCondition {
   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return Tann.isPrime(sideState.getCalculatedEffect().getValue());
   }

   @Override
   public String describe() {
      return "prime";
   }
}
