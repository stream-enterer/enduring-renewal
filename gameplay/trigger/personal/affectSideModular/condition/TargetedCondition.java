package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;

public class TargetedCondition extends AffectSideCondition {
   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedEffect().needsTarget();
   }

   @Override
   public String describe() {
      return "targeted";
   }

   @Override
   public GenericView getActor() {
      return new RandomSidesView(1);
   }

   @Override
   public EffectDraw getAddDraw() {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            int offset = 3;
            batch.setColor(Colours.grey);
            batch.draw(Images.targetIcon, x + 3, y + 3 + 1);
            super.draw(batch, x, y);
         }
      };
   }

   @Override
   public boolean needsGraphic() {
      return false;
   }

   @Override
   public boolean hasSideImage() {
      return true;
   }

   @Override
   public boolean isPlural() {
      return true;
   }
}
