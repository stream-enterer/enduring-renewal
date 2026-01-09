package com.tann.dice.gameplay.trigger.personal.replaceSides;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class BuffSideIndex extends Personal {
   public final int index;
   public final int delta;

   public BuffSideIndex(int index, int delta) {
      this.index = index;
      this.delta = delta;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      if (sideState.getIndex() == this.index) {
         Eff e = sideState.getCalculatedEffect();
         if (e.hasValue()) {
            int newValue = e.getValue() + this.delta;
            e.setValue(newValue);
         }
      }

      super.affectSide(sideState, owner, triggerIndex);
   }
}
