package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;

public class StateConditionSideCondition extends AffectSideCondition {
   final GenericStateCondition genericStateCondition;

   public StateConditionSideCondition(GenericStateCondition genericStateCondition) {
      this.genericStateCondition = genericStateCondition;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerIndex) {
      return this.genericStateCondition.isValid(owner);
   }

   @Override
   public boolean isAfterSides() {
      return true;
   }

   @Override
   public String describe() {
      return this.genericStateCondition.describe().toLowerCase();
   }

   @Override
   public Actor getPrecon() {
      return this.genericStateCondition.getPrecon();
   }

   @Override
   public boolean showInPanel() {
      return this.genericStateCondition.getStateConditionType() == StateConditionType.HalfOrLessHP;
   }

   @Override
   public String getImageName() {
      switch (this.genericStateCondition.getStateConditionType()) {
         case HalfOrLessHP:
            return "berserk";
         case FullHP:
         case Dying:
         case Damaged:
         case HasShields:
         case GainedNoShields:
         default:
            return super.getImageName();
      }
   }
}
