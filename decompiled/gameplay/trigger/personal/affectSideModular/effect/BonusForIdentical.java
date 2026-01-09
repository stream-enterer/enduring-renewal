package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import java.util.List;

public class BonusForIdentical extends AffectSideEffect {
   final int bonus = 1;

   @Override
   public String describe() {
      return "Weird wording...";
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return "All sides get " + Tann.delta(1) + " for each other identical side";
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            float mid = 8.5F;
            batch.setColor(Colours.blue);

            for (int i = 0; i < 2; i++) {
               float add = (i * 2 - 1) * 2.5F;
               float yAdd = add / 2.0F;
               Draw.drawCentered(batch, Images.plusBig, x + 8.5F + add, y + 8.5F + yAdd);
            }
         }
      };
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff myEff = sideState.getCalculatedEffect();
      if (myEff.hasValue()) {
         int myHash = myEff.hashEff();
         int numDupes = 0;

         for (EntSide es : owner.getEnt().getSides()) {
            if (es != sideState.getOriginal()) {
               EntSideState ess = new EntSideState(owner, es, sourceIndex);
               if (ess.getCalculatedEffect().hashEff() == myHash) {
                  numDupes++;
               }
            }
         }

         if (numDupes != 0) {
            int newValue = myEff.getValue() + numDupes;
            myEff.setValue(newValue);
         }
      }
   }
}
