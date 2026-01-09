package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;

public abstract class BasicTextmodToggle extends AffectSideEffect {
   @Override
   public boolean needsGraphic() {
      return false;
   }

   @Override
   public final void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      sideState.setCalculatedEffect(this.alterEff(sideState.getCalculatedEffect()));
   }

   public abstract Eff alterEff(Eff var1);
}
