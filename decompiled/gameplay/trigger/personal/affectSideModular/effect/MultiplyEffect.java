package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class MultiplyEffect extends AffectSideEffect {
   final int multiple;

   public MultiplyEffect(int multiple) {
      this.multiple = multiple;
   }

   @Override
   public String getToFrom() {
      return "of";
   }

   @Override
   public String describe() {
      return Words.capitaliseFirst(Words.multiple(this.multiple)) + " the pips";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      if (e.hasValue()) {
         int newValue = e.getValue() * this.multiple;
         e.setValue(newValue);
      }
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public EffectDraw getAddDraw(final boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            int size = EntSize.reg.getPixels();
            batch.setColor(Colours.orange);
            if (hasSideImage) {
               batch.setColor(Colours.orange);

               for (int i = 0; i < MultiplyEffect.this.multiple; i++) {
                  batch.draw(Images.multiplier, x + 16 - 5, y + 2 + i * 4);
               }
            } else {
               TannFont.font.drawString(batch, "x" + MultiplyEffect.this.multiple, (int)(x + size / 2.0F), (int)(y + size / 2.0F), 1);
            }
         }
      };
   }

   @Override
   public AffectSideEffect genMult(int mult) {
      return new MultiplyEffect(GlobalNumberLimit.box(this.multiple * mult));
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.LARGE_VALUES;
   }
}
