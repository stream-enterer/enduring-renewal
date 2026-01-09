package com.tann.dice.gameplay.effect.eff.conditionalBonus;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.EnumConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.NotRequirement;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;

public class ConditionalBonus {
   public final ConditionalRequirement requirement;
   public final ConditionalBonusType bonusType;
   public final int bonusAmount;

   public ConditionalBonus(ConditionalRequirement requirement) {
      this(requirement, ConditionalBonusType.Multiply, 2);
   }

   public ConditionalBonus(StateConditionType type, boolean source) {
      this(new GSCConditionalRequirement(new GenericStateCondition(type), source), ConditionalBonusType.Multiply, 2);
   }

   public ConditionalBonus(ConditionalRequirement requirement, ConditionalBonusType bonusType, int value) {
      this.requirement = requirement;
      this.bonusAmount = value;
      this.bonusType = bonusType;
   }

   public ConditionalBonus(ConditionalBonusType bonusType) {
      this(EnumConditionalRequirement.Always, bonusType, 1);
   }

   public boolean isValid(EntState sourceState, EntState targetState, Eff eff) {
      Snapshot s = null;
      if (sourceState != null) {
         s = sourceState.getSnapshot();
      } else if (targetState != null) {
         s = targetState.getSnapshot();
      }

      return s == null ? false : this.requirement.isValid(s, sourceState, targetState, eff);
   }

   public int affectValue(Eff eff, EntState sourceState, EntState targetState, int value) {
      if (!this.isValid(sourceState, targetState, eff)) {
         return 0;
      } else {
         Snapshot s = null;
         if (sourceState != null) {
            s = sourceState.getSnapshot();
         } else if (targetState != null) {
            s = targetState.getSnapshot();
         }

         return s == null ? 0 : this.bonusType.getBonus(s, sourceState, targetState, this.bonusAmount, value, eff);
      }
   }

   public ConditionalBonus antiRequirement() {
      return new ConditionalBonus(new NotRequirement(this.requirement), this.bonusType, this.bonusAmount);
   }

   public ConditionalBonus halveVersion() {
      return new ConditionalBonus(this.requirement, ConditionalBonusType.Divide, 2);
   }
}
