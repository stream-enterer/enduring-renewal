package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LinePathActor extends Actor {
   Vector2[] vector2s;

   public LinePathActor(Vector2... vector2s) {
      for (Vector2 vector2 : vector2s) {
         vector2.x = (float)Math.floor(vector2.x);
         vector2.y = (float)Math.floor(vector2.y);
      }

      this.vector2s = vector2s;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());

      for (int i = 0; i < this.vector2s.length - 1; i++) {
         Draw.drawLine(batch, this.vector2s[i], this.vector2s[i + 1], 1);
      }
   }
}
