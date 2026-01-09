package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.util.Tann;

public class NextPrime extends AffectSideEffect {
   @Override
   public String describe() {
      return "Increase to the next prime";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      if (e.hasValue()) {
         e.setValue(Tann.nextPrimeAfter(e.getValue()));
      }
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }
}
