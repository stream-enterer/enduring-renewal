package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.PipGrid;

public class EvennessCondition extends AffectSideCondition {
   final boolean even;

   public EvennessCondition(boolean even) {
      this.even = even;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      Eff e = sideState.getCalculatedEffect();
      return e.hasValue() && this.even == (e.getValue() % 2 == 0);
   }

   @Override
   public String describe() {
      return this.even ? "even" : "odd";
   }

   final Actor getPreconAlt() {
      String text = "[text]";
      int numExamples = 3;

      for (int i = 0; i < 3; i++) {
         text = text + (i * 2 + (this.even ? 2 : 1));
         if (i < 2) {
            text = text + ", ";
         }
      }

      text = text + "...";
      return new Pixl(0, 1).border(Colours.grey).text(text).pix();
   }

   @Override
   public Actor getPrecon() {
      int num = this.even ? 2 : 1;
      int numExamples = 3;
      Pixl p = new Pixl(2);

      for (int i = 0; i < 3; i++) {
         p.actor(PipGrid.make(num));
         if (i < 2) {
            p.text("[grey]|");
         }

         num += 2;
      }

      p.text("[grey]...");
      return p.pix(4);
   }
}
