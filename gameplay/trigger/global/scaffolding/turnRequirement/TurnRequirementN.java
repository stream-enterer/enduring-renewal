package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class TurnRequirementN extends TurnRequirement {
   final int n;

   public TurnRequirementN(int n) {
      this.n = n;
   }

   @Override
   public Actor makePanelActor() {
      return new Pixl().image(Images.turnIcon).gap(2).text("[text]=" + this.n).pix();
   }

   @Override
   public boolean isValid(int turn) {
      return turn == this.n;
   }

   @Override
   public String describe() {
      return "the " + Words.ordinal(this.n) + " turn";
   }

   @Override
   public String hyphenTag() {
      return this.n + "";
   }
}
