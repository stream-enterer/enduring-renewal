package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CrossActor extends Actor {
   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.drawLine(batch, this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.0F);
      Draw.drawLine(batch, this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth(), this.getY(), 1.0F);
      super.draw(batch, parentAlpha);
   }

   public static void drawCross(Batch batch, Color c, Actor a, Vector2 absoluteCoordinates) {
      a.setPosition(a.getX() + absoluteCoordinates.x, a.getY() + absoluteCoordinates.y);
      batch.setColor(c);
      Draw.drawLine(batch, a.getX(), a.getY(), a.getX() + a.getWidth(), a.getY() + a.getHeight(), 1.0F);
      Draw.drawLine(batch, a.getX(), a.getY() + a.getHeight(), a.getX() + a.getWidth(), a.getY(), 1.0F);
      a.setPosition(a.getX() - absoluteCoordinates.x, a.getY() - absoluteCoordinates.y);
   }
}
