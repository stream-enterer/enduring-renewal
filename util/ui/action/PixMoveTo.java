package com.tann.dice.util.ui.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class PixMoveTo extends TemporalAction {
   private float startX;
   private float startY;
   private float endX;
   private float endY;
   private int alignment = 12;

   PixMoveTo(int endX, int endY, float duration, Interpolation interpolation) {
      super(duration, interpolation);
      this.endX = endX;
      this.endY = endY;
   }

   protected void begin() {
      this.startX = this.target.getX(this.alignment);
      this.startY = this.target.getY(this.alignment);
   }

   protected void update(float percent) {
      float x;
      float y;
      if (percent == 0.0F) {
         x = this.startX;
         y = this.startY;
      } else if (percent == 1.0F) {
         x = this.endX;
         y = this.endY;
      } else {
         x = this.startX + (this.endX - this.startX) * percent;
         y = this.startY + (this.endY - this.startY) * percent;
      }

      this.target.setPosition((int)Math.floor(x), (int)Math.floor(y), this.alignment);
   }
}
