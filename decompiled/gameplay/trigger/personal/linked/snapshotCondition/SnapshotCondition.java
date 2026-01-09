package com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.ui.TextWriter;

public class SnapshotCondition implements ConditionalRequirement {
   final int val;
   final SnapshotConditionType type;

   public SnapshotCondition(SnapshotConditionType type) {
      this(type, -1);
   }

   public SnapshotCondition(SnapshotConditionType type, int val) {
      this.val = val;
      this.type = type;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      return this.type.holdsFor(s, this.val);
   }

   @Override
   public boolean preCalculate() {
      return true;
   }

   @Override
   public String getInvalidString(Eff eff) {
      return "inv";
   }

   @Override
   public String describe(Eff eff) {
      return this.type.name();
   }

   @Override
   public String getBasicString() {
      return this.type.getBasicString(this.val);
   }

   @Override
   public Actor getRestrictionActor() {
      return new TextWriter(this.type.getShortString(this.val), 40);
   }

   @Override
   public boolean isPlural() {
      return false;
   }
}
