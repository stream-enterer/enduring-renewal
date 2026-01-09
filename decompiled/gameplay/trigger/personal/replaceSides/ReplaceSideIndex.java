package com.tann.dice.gameplay.trigger.personal.replaceSides;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;

public class ReplaceSideIndex extends Personal {
   int replaceIndex;
   EntSide replacement;

   public ReplaceSideIndex(int replaceIndex, EntSide replacement) {
      this.replaceIndex = replaceIndex;
      this.replacement = replacement;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      if (sideState.getOriginal() == owner.getEnt().getSides()[this.replaceIndex]) {
         ReplaceWith.replaceSide(sideState, this.replacement);
      }
   }

   @Override
   public float getPriority() {
      return -7.0F;
   }
}
