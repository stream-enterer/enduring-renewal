package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;

public abstract class LeftTextmodToggle extends AffectSideEffect {
   @Override
   public boolean needsGraphic() {
      return false;
   }

   @Override
   public final void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      if (sideState.getIndex() != 2) {
         EntSide orig = owner.getEnt().getSides()[2];
         sideState.setCalculatedEffect(this.alterEff(sideState.getCalculatedEffect(), this.getLeftEff(owner, sourceIndex, orig)));
      }
   }

   private Eff getLeftEff(EntState owner, int sourceIndex, EntSide original) {
      EntSideState ess = new EntSideState(owner, original, sourceIndex);
      return ess.getCalculatedEffect();
   }

   public abstract Eff alterEff(Eff var1, Eff var2);
}
