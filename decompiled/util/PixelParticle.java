package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PixelParticle extends Actor {
   public PixelParticle(Color color) {
      this.setColor(color);
   }

   public void act(float delta) {
      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.fillRectangle(batch, this.getX(), this.getY(), 1.0F * this.getScaleX(), 1.0F * this.getScaleY());
   }
}
