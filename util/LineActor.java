package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LineActor extends Actor {
   float startX;
   float startY;
   public float endX;
   public float endY;

   public LineActor(float startX, float startY, float endX, float endY) {
      this.startX = startX;
      this.startY = startY;
      this.endX = endX;
      this.endY = endY;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.drawLine(batch, this.startX, this.startY, this.endX, this.endY, 1.0F);
   }
}
