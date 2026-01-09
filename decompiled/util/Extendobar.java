package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Extendobar extends Actor {
   final Color col;

   public Extendobar(Color col) {
      this.col = col;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.getParent() != null) {
         batch.setColor(this.col);
         Draw.fillRectangle(batch, Math.round(this.getX() - this.getParent().getWidth() / 2.0F), this.getY(), this.getParent().getWidth(), 1.0F);
      }
   }
}
