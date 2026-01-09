package com.tann.dice.util.ui.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class PixMoveBy extends TemporalAction {
   private final int dX;
   private final int dY;
   float pp = 0.0F;

   public PixMoveBy(float duration, Interpolation interpolation, int dX, int dY) {
      super(duration, interpolation);
      this.dX = dX;
      this.dY = dY;
   }

   protected void update(float percent) {
      int px = this.getXD(this.pp);
      int py = this.getYD(this.pp);
      int cx = this.getXD(percent);
      int cy = this.getYD(percent);
      this.pp = percent;
      this.target.setPosition(this.target.getX() + cx - px, this.target.getY() + cy - py);
   }

   private int getXD(float percent) {
      return (int)(this.dX * percent);
   }

   private int getYD(float percent) {
      return (int)(this.dY * percent);
   }
}
