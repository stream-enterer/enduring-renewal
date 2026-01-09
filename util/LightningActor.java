package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LightningActor extends Actor {
   float[] points;

   public LightningActor(float startX, float startY, float endX, float endY, float segLength, float variance) {
      float xDist = endX - startX;
      float yDist = endY - startY;
      float length = Tann.length(xDist, yDist);
      int segments = (int)(length / segLength) + 1;
      this.points = new float[(segments + 1) * 2];

      for (int i = 0; i < segments; i++) {
         float ratio = (float)i / segments;
         this.points[i * 2] = startX + xDist * ratio + Tann.random(-variance, variance);
         this.points[i * 2 + 1] = startY + yDist * ratio + Tann.random(-variance, variance);
      }

      this.points[this.points.length - 2] = endX;
      this.points[this.points.length - 1] = endY;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());

      for (int i = 0; i < this.points.length - 2; i += 2) {
         Draw.drawLine(batch, this.points[i], this.points[i + 1], this.points[i + 2], this.points[i + 3], 1.0F);
      }

      super.draw(batch, parentAlpha);
   }
}
