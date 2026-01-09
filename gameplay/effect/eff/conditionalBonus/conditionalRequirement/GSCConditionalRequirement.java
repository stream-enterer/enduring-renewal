package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;

public class GSCConditionalRequirement implements ConditionalRequirement {
   final GenericStateCondition gsc;
   final boolean source;

   public GSCConditionalRequirement(GenericStateCondition gsc) {
      this(gsc, false);
   }

   public GSCConditionalRequirement(StateConditionType sct) {
      this(new GenericStateCondition(sct));
   }

   public GSCConditionalRequirement(GenericStateCondition gsc, boolean source) {
      this.gsc = gsc;
      this.source = source;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      EntState es = this.source ? sourceState : targetState;
      return es == null ? false : this.gsc.isValid(es);
   }

   public boolean isSource() {
      return this.source;
   }

   @Override
   public boolean preCalculate() {
      return this.source;
   }

   @Override
   public String getInvalidString(Eff eff) {
      return this.gsc.getInvalidString(eff);
   }

   @Override
   public String describe(Eff eff) {
      String gscs = this.gsc.describeShort();
      switch (eff.getTargetingType()) {
         case Group:
         case ALL:
            return "with " + gscs;
         default:
            return "(target must " + (gscs.contains("HP") ? "have" : "be") + " " + gscs + ")";
      }
   }

   @Override
   public String getBasicString() {
      return this.gsc.describeShort();
   }

   @Override
   public Actor getRestrictionActor() {
      return this.gsc.getPrecon();
   }

   @Override
   public boolean isPlural() {
      return false;
   }

   public GenericStateCondition getGsc() {
      return this.gsc;
   }

   public ConditionalRequirement getSwapped() {
      return new GSCConditionalRequirement(this.gsc, !this.source);
   }
}
