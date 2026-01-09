package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;

public class NotCondition extends AffectSideCondition {
   final AffectSideCondition cond;

   public NotCondition(AffectSideCondition cond) {
      this.cond = cond;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return !this.cond.validFor(sideState, owner, triggerAffectSides);
   }

   @Override
   public String describe() {
      return "non-" + this.cond.describe();
   }

   @Override
   public Actor getPrecon() {
      Actor a = this.cond.getPrecon();
      return a == null ? null : new Pixl().text("[purple]non-").gap(3).actor(a).pix();
   }

   @Override
   public GenericView getActor() {
      return this.cond.getActor();
   }

   @Override
   public boolean needsGraphic() {
      return this.cond.needsGraphic();
   }

   @Override
   public boolean hasSideImage() {
      return this.cond.hasSideImage();
   }

   @Override
   public EffectDraw getAddDraw() {
      final EffectDraw ed = this.cond.getAddDraw();
      return ed != null ? new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            ed.draw(batch, x, y, index);
            batch.setColor(Colours.red);
            int dist = 4;
            int xOffset = 3;
            int yOffset = 6;
            Draw.drawLine(batch, x + xOffset, y + yOffset, x + xOffset + dist, y + yOffset + dist, 2.0F);
            super.draw(batch, x, y, index);
         }
      } : super.getAddDraw();
   }
}
