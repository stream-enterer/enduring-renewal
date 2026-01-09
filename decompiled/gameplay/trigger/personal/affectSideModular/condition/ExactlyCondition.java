package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.lang.Words;

public class ExactlyCondition extends AffectSideCondition {
   final int number;

   public ExactlyCondition(int number) {
      this.number = number;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedEffect().getValue() == this.number;
   }

   @Override
   public boolean isAfterSides() {
      return true;
   }

   @Override
   public String describe() {
      return "with exactly " + this.number + " " + Words.plural("pip", this.number);
   }

   @Override
   public Actor getPrecon() {
      Actor inner = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            Draw.fillActor(batch, this, Colours.dark, Colours.AS_BORDER);
            EntSide.drawPipsSinglePixelSquish(batch, EntSize.reg, ExactlyCondition.this.number, 0, Colours.purple, (int)this.getX(), (int)this.getY());
         }
      };
      int size = EntSize.reg.getPixels();
      inner.setSize(size, size);
      return inner;
   }
}
