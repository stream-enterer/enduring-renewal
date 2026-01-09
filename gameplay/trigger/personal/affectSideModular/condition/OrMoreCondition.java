package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.TannFont;

public class OrMoreCondition extends AffectSideCondition {
   final int value;

   public OrMoreCondition(int value) {
      this.value = value;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedEffect().getValue() >= this.value;
   }

   @Override
   public boolean isAfterSides() {
      return true;
   }

   @Override
   public Actor getPrecon() {
      Actor inner = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            Draw.fillActor(batch, this, Colours.dark, Colours.AS_BORDER);
            batch.setColor(Colours.light);
            TannFont.font
               .drawString(
                  batch, ">=" + OrMoreCondition.this.value, (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + this.getHeight() / 2.0F), 1
               );
         }
      };
      int size = EntSize.reg.getPixels();
      inner.setSize(size, size);
      return inner;
   }

   @Override
   public String describe() {
      return "with " + this.value + " or more pips";
   }

   @Override
   public String hyphenTag() {
      return "" + this.value;
   }
}
