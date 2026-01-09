package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Rectactor extends Actor {
   final Color outer;
   final Color inner;
   final int thickness;

   public Rectactor(int width, int height, Color outer) {
      this(width, height, outer, null);
   }

   public Rectactor(int width, int height, Color outer, Color inner) {
      this(width, height, 1, outer, inner);
   }

   public Rectactor(int width, int height, int thickness, Color col) {
      this(width, height, thickness, col, null);
   }

   public Rectactor(int width, int height, int thickness, Color outer, Color inner) {
      if (outer == null && inner == null) {
         throw new RuntimeException("eep");
      } else {
         this.setSize(width, height);
         this.outer = outer;
         this.inner = inner;
         this.thickness = thickness;
      }
   }

   public Rectactor(Actor a, Color col) {
      this((int)a.getWidth(), (int)a.getHeight(), null, col);
   }

   public static void fill(Group g, Color outer) {
      fill(g, outer, Colours.dark);
   }

   public static void fill(Group g, Color outer, Color inner) {
      Rectactor ra = new Rectactor((int)g.getWidth(), (int)g.getHeight(), outer, inner);
      g.addActor(ra);
      ra.toBack();
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.outer == null) {
         Draw.fillActor(batch, this, this.inner);
      } else if (this.inner == null) {
         batch.setColor(this.outer);
         Draw.drawRectangle(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.thickness);
      } else {
         Draw.fillActor(batch, this, this.inner, this.outer, this.thickness);
      }
   }
}
