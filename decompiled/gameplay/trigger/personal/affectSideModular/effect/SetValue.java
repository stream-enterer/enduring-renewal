package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannFont;
import java.util.List;

public class SetValue extends AffectSideEffect {
   private static final Color col = Colours.orange;
   final int value;

   public SetValue(int value) {
      this.value = value;
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String s = "Set all ";

      for (AffectSideCondition c : conditions) {
         s = s + c.describe() + " ";
      }

      return s + "sides to " + this.value;
   }

   @Override
   public String describe() {
      return "set to " + this.value;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      if (sideState.getCalculatedEffect().hasValue()) {
         sideState.getCalculatedEffect().setValue(this.value);
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
            if (hasSideImage) {
               TextureRegion img = Images.minus;

               for (int i = 0; i < SetValue.this.value; i++) {
                  batch.setColor(SetValue.col);
                  batch.draw(img, x + EntSize.reg.getPixels() - img.getRegionWidth() - 2, y + 2 + i * (img.getRegionHeight() + 1));
               }
            } else {
               int size = EntSize.reg.getPixels();
               batch.setColor(SetValue.col);
               TannFont.font.drawString(batch, "" + SetValue.this.value, (int)(x + size / 2.0F), (int)(y + size / 2.0F), 1);
            }
         }
      };
   }

   @Override
   public AffectSideEffect genMult(int mult) {
      return new SetValue(GlobalNumberLimit.box(this.value * mult));
   }
}
