package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.Pixl;

public class CombinedRequirement implements ConditionalRequirement {
   final ConditionalRequirement a;
   final ConditionalRequirement b;

   public CombinedRequirement(ConditionalRequirement a, ConditionalRequirement b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      return this.a.isValid(s, sourceState, targetState, eff) && this.b.isValid(s, sourceState, targetState, eff);
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
      return this.a.describe(eff) + "&" + this.b.describe(eff);
   }

   @Override
   public String getBasicString() {
      return this.a.getBasicString() + (" & " + this.b.getBasicString()).toLowerCase();
   }

   @Override
   public Actor getRestrictionActor() {
      return new Pixl(3).actor(this.a.getRestrictionActor()).text("&").actor(this.b.getRestrictionActor()).pix();
   }

   @Override
   public boolean isPlural() {
      return this.a.isPlural() || this.b.isPlural();
   }
}
