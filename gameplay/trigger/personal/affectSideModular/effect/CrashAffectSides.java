package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;

public class CrashAffectSides extends AffectSideEffect {
   @Override
   public String describe() {
      return "attempt to crash the game";
   }

   @Override
   public String getToFrom() {
      return "on";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      throw new RuntimeException("test crash as");
   }
}
