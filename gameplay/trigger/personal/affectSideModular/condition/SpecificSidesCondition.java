package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSideView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.screens.dungeon.panels.Explanel.affectSides.AffectSideTemplate;

public class SpecificSidesCondition extends AffectSideCondition {
   public final SpecificSidesType specificSidesType;

   public SpecificSidesCondition(SpecificSidesType specificSidesType) {
      this.specificSidesType = specificSidesType;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return this.specificSidesType.validFor(sideState, owner);
   }

   @Override
   public int indexValid(EntSideState sideState, EntState owner) {
      return this.specificSidesType.validIndex(sideState, owner);
   }

   @Override
   public String describe() {
      return this.specificSidesType.description;
   }

   @Override
   public boolean isPlural() {
      return this.specificSidesType.sideIndices.length != 1;
   }

   @Override
   public GenericView getActor() {
      return new AffectSideView(new AffectSideTemplate(this.specificSidesType));
   }

   @Override
   public boolean overrideDoesntNeedPrecon() {
      return true;
   }

   @Override
   public boolean needsGraphic() {
      return this.specificSidesType != SpecificSidesType.Any;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.specificSidesType.getCollisionBits(player);
   }
}
