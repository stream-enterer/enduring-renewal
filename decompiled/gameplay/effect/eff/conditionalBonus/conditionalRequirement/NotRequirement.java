package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.Pixl;

public class NotRequirement implements ConditionalRequirement {
   final ConditionalRequirement conditionalRequirement;

   public NotRequirement(ConditionalRequirement conditionalRequirement) {
      this.conditionalRequirement = conditionalRequirement;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      return !this.conditionalRequirement.isValid(s, sourceState, targetState, eff);
   }

   @Override
   public boolean preCalculate() {
      return this.conditionalRequirement.preCalculate();
   }

   @Override
   public String getInvalidString(Eff eff) {
      return transform(this.conditionalRequirement.getInvalidString(eff));
   }

   public static String transform(String description) {
      String s = description.replace(" vs ", " except vs ")
         .replace(" if ", " except if ")
         .replace("there are ", "there aren't ")
         .replace("there is ", "there isn't ")
         .replace("bonus", "minus")
         .replace("+1", "-1");
      if (s.equals(description)) {
         s = "not " + s;
      }

      return s;
   }

   @Override
   public String describe(Eff eff) {
      return transform(this.conditionalRequirement.describe(eff));
   }

   @Override
   public String getBasicString() {
      return transform(this.conditionalRequirement.getBasicString());
   }

   @Override
   public Actor getRestrictionActor() {
      Actor a = this.conditionalRequirement.getRestrictionActor();
      return (Actor)(a != null ? new Pixl().text("[grey][b]not").gap(2).actor(a).pix() : a);
   }

   @Override
   public boolean isPlural() {
      return this.conditionalRequirement.isPlural();
   }
}
