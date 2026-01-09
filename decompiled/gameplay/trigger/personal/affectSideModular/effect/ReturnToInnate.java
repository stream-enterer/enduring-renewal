package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import java.util.List;

public class ReturnToInnate extends AffectSideEffect {
   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw(Images.ASEInnate);
   }

   @Override
   public String describe() {
      return "Revert all changes";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int i2 = sideState.getIndex();
      EntSide original = owner.getEnt().getSides()[i2];
      ReplaceWith.replaceSide(sideState, original);
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }
}
