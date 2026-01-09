package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.EquipRestrictCol;

public class ColRestriction implements ConditionalRequirement {
   public final HeroCol col;

   public ColRestriction(HeroCol col) {
      this.col = col;
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      return sourceState.getEnt().getColour().equals(this.col.col);
   }

   @Override
   public boolean preCalculate() {
      return true;
   }

   @Override
   public String getInvalidString(Eff eff) {
      return "inv?";
   }

   @Override
   public String describe(Eff eff) {
      return this.getBasicString();
   }

   @Override
   public String getBasicString() {
      return "equipped to a " + this.col.colourTaggedName(false) + " hero";
   }

   @Override
   public Actor getRestrictionActor() {
      return EquipRestrictCol.restrActor(this.col);
   }

   @Override
   public boolean isPlural() {
      return false;
   }
}
