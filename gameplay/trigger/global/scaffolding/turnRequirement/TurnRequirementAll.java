package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;

public class TurnRequirementAll extends TurnRequirement {
   @Override
   public boolean isValid(int turn) {
      return true;
   }

   @Override
   public String describe() {
      return "Each turn";
   }

   public static TurnRequirement get() {
      return new TurnRequirementAll();
   }

   @Override
   public Actor makePanelActor() {
      return new Pixl().image(Images.turnIcon).pix();
   }
}
