package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class FlatBonus extends AffectSideEffect {
   final int[] bonus;
   final boolean showInPanel;
   final int descriptiveDelta;

   public FlatBonus(int bonus) {
      this(false, bonus);
   }

   public FlatBonus(int... bonus) {
      this(false, bonus);
   }

   public FlatBonus(boolean showInPanel, int... bonus) {
      this.bonus = bonus;
      this.showInPanel = showInPanel;
      this.descriptiveDelta = this.setupDescriptiveDelta();
   }

   private int setupDescriptiveDelta() {
      int tmp = 0;

      for (int i : this.bonus) {
         if (i < 0 && tmp > 0 || i > 0 && tmp < 0) {
            return 0;
         }

         tmp = (int)Math.signum((float)i);
      }

      return tmp;
   }

   @Override
   public String getImageName() {
      switch (this.descriptiveDelta) {
         case -1:
            return "allSidesMalus";
         case 1:
            return "allSidesBonus";
         default:
            return super.getImageName();
      }
   }

   @Override
   public String describe() {
      if (this.bonus.length == 1) {
         return Words.describePipDelta(this.bonus[0]);
      } else {
         switch (this.descriptiveDelta) {
            case -1:
               return "Weaken";
            case 0:
               return "Change";
            case 1:
               return "Improve";
            default:
               return "uhoh error: " + this.descriptiveDelta;
         }
      }
   }

   @Override
   public String getToFrom() {
      return this.bonus.length == 1 ? "to" : "SKIP";
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
            int bon = FlatBonus.getFromThing(FlatBonus.this.bonus, index);
            int size = EntSize.reg.getPixels();
            boolean positive = bon >= 0;
            if (hasSideImage) {
               batch.setColor(positive ? Colours.light : Colours.red);
               TextureRegion img = positive ? Images.plus : Images.minus;
               TextureRegion between = Images.singlePip;
               int cy = y + 2;

               for (int i = 0; i < Math.abs(bon) - 1; i++) {
                  batch.draw(between, x + size - between.getRegionWidth() - 2, cy);
                  cy += between.getRegionHeight() + 1;
               }

               batch.draw(img, x + size - img.getRegionWidth() - 2, cy);
            } else {
               batch.setColor(bon == 0 ? Colours.grey : (positive ? Colours.light : Colours.red));
               TannFont.font.drawString(batch, Tann.delta(bon), (int)(x + size / 2.0F), (int)(y + size / 2.0F), 1);
            }
         }
      };
   }

   public static <T> T getFromThing(T[] array, int index) {
      T bon;
      if (array.length == 1) {
         bon = array[0];
      } else if (array.length <= index) {
         bon = null;
      } else {
         bon = array[index];
      }

      return bon;
   }

   public static int getFromThing(int[] array, int index) {
      Integer[] cpy = new Integer[array.length];

      for (int i = 0; i < array.length; i++) {
         cpy[i] = array[i];
      }

      Integer result = getFromThing(cpy, index);
      if (result == null) {
         result = 0;
      }

      return result;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      int bon = this.bonus[this.bonus.length == 1 ? 0 : index];
      if (e.hasValue()) {
         int newValue = e.getValue() + bon;
         e.setValue(newValue);
      }
   }

   @Override
   public boolean showInPanel() {
      return this.showInPanel;
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      return (float)Math.pow(1.9F, tier) * pips;
   }

   @Override
   public boolean isIndexed() {
      return this.bonus.length > 1;
   }

   @Override
   public AffectSideEffect genMult(int mult) {
      return this.bonus.length != 1 ? null : new FlatBonus(GlobalNumberLimit.box(this.bonus[0] * mult));
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   public int getSingleBonus() {
      return this.isIndexed() ? -99 : this.bonus[0];
   }

   @Override
   public String hyphenTag() {
      return this.bonus[0] + "";
   }
}
