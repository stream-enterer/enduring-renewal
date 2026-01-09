package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;

public class XORRequirement implements ConditionalRequirement {
   final ConditionalRequirement a;
   final ConditionalRequirement b;

   public XORRequirement(ConditionalRequirement a, ConditionalRequirement b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      return this.a.isValid(s, sourceState, targetState, eff) ^ this.b.isValid(s, sourceState, targetState, eff);
   }

   @Override
   public boolean preCalculate() {
      return this.a.preCalculate() && this.b.preCalculate();
   }

   @Override
   public String getInvalidString(Eff eff) {
      return this.a.getInvalidString(eff);
   }

   @Override
   public String describe(Eff eff) {
      return this.a.describe(eff) + " xor " + this.b.describe(eff);
   }

   @Override
   public String getBasicString() {
      return this.a.getBasicString() + " xor " + this.b.getBasicString();
   }

   @Override
   public Actor getRestrictionActor() {
      return null;
   }

   @Override
   public boolean isPlural() {
      return this.a.isPlural() || this.b.isPlural();
   }
}
