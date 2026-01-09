package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;

public class ReplaceWithBlank extends ReplaceWith {
   final ChoosableType src;

   public ReplaceWithBlank(ChoosableType blankType) {
      super(EntSidesLib.getBlank(blankType));
      this.src = blankType;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      EntSize sz = owner.getEnt().getSize();
      EntSide blank = sz.getBlank();
      if (sz == EntSize.reg) {
         blank = EntSidesLib.getBlank(this.src);
      }

      ReplaceWith.replaceSide(sideState, blank);
   }
}
