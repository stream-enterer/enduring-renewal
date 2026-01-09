package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class HasValue extends AffectSideCondition {
   public final boolean pips;

   public HasValue(boolean pips) {
      this.pips = pips;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedEffect().hasValue() == this.pips;
   }

   @Override
   public String describe() {
      return this.pips ? "pipped" : "pipless";
   }

   @Override
   public EffectDraw getAddDraw() {
      final RandomSidesView result = new RandomSidesView(1);
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            if (HasValue.this.pips) {
               batch.setColor(Colours.green);
            } else {
               batch.setColor(Colours.red);
            }

            int w = 1;
            int b = 1;
            int xw = (int)(x + result.getWidth());
            int xo = -1;
            Draw.drawLine(batch, xw - 1 - 1 + -1, y + 1, xw - 1 + -1, y + result.getHeight() - 2.0F, 1.0F);
         }
      };
   }

   @Override
   public boolean hasSideImage() {
      return true;
   }
}
