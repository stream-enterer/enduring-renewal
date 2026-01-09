package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class TurnRequirementEveryN extends TurnRequirement {
   final int n;

   public TurnRequirementEveryN(int everyN) {
      if (everyN < 2) {
         throw new RuntimeException("invalid N: " + everyN);
      } else {
         this.n = everyN;
      }
   }

   @Override
   public Actor makePanelActor() {
      return new Pixl().image(Images.turnIcon).gap(2).text("[text]/" + this.n).pix();
   }

   @Override
   public boolean isValid(int turn) {
      return turn != 0 && turn % this.n == 0;
   }

   @Override
   public String describe() {
      return "[notranslate]" + com.tann.dice.Main.t("Every " + Words.ordinal(this.n) + " turn");
   }

   @Override
   public String hyphenTag() {
      return this.n + "";
   }
}
