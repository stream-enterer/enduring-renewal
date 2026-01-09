package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Colours;
import com.tann.dice.util.FontWrapper;

class TextBox extends Group {
   String text;
   final boolean wiggle;
   final boolean sin;
   final boolean bold;
   final boolean italics;
   final boolean first;
   final boolean glitch;
   private static final Vector3 translation = new Vector3();
   private static final Matrix4 oldTransformation = new Matrix4();
   public static int SkipDraw = 0;
   private final FontWrapper font;

   TextBox(String text, FontWrapper font) {
      this(text, font, false, false, false, false, false, false);
   }

   TextBox(String text, FontWrapper font, boolean wiggle, boolean sin, boolean bold, boolean italics, boolean first, boolean glitch) {
      if (bold) {
         text = text.toLowerCase();
      }

      this.font = font;
      this.text = text;
      this.wiggle = wiggle;
      this.sin = sin;
      this.bold = bold;
      this.italics = italics;
      this.first = first;
      this.glitch = glitch;
      this.setup(text);
      this.setColor(Colours.light);
      this.setTransform(false);
   }

   public void setup(String text) {
      this.text = text;
      this.setSize(this.font.getWidth(text, this.bold, this.italics, this.first), this.font.getHeight());
   }

   public static void preDraw(Batch batch) {
      oldTransformation.set(com.tann.dice.Main.self().backgroundBatch.getTransformMatrix());
      com.tann.dice.Main.self().backgroundBatch.setTransformMatrix(batch.getTransformMatrix());
      com.tann.dice.Main.self().backgroundBatch.getTransformMatrix().scale(com.tann.dice.Main.scale, com.tann.dice.Main.scale, 1.0F);
      com.tann.dice.Main.self().backgroundBatch.getTransformMatrix().getTranslation(translation);
      translation.scl(com.tann.dice.Main.scale, com.tann.dice.Main.scale, 1.0F);
      com.tann.dice.Main.self().backgroundBatch.getTransformMatrix().setTranslation(translation);
      com.tann.dice.Main.self().startBackground();
   }

   public static void postDraw() {
      com.tann.dice.Main.self().stopBackground();
      com.tann.dice.Main.self().backgroundBatch.setTransformMatrix(oldTransformation);
   }

   private void drawHD(float offsetX, float offsetY) {
      if (!this.text.isEmpty()) {
         com.tann.dice.Main.self().backgroundBatch.setColor(this.getColor());
         this.font
            .drawString(
               com.tann.dice.Main.self().backgroundBatch,
               this.text,
               offsetX + this.getX(),
               offsetY + this.getY(),
               false,
               this.wiggle,
               this.sin,
               this.bold,
               this.italics,
               this.first,
               this.glitch
            );
      }
   }

   public static boolean drawHDChildren(Batch batch, Group group, float offsetX, float offsetY, boolean mustStartBatch) {
      if (TestRunner.isTesting()) {
         return !mustStartBatch;
      } else {
         SnapshotArray<Actor> children = group.getChildren();
         Actor[] actors = (Actor[])children.begin();
         float totalOffsetX = offsetX + group.getX();
         float totalOffsetY = offsetY + group.getY();
         int i = 0;

         for (int n = children.size; i < n; i++) {
            Actor child = actors[i];
            if (child.isVisible()) {
               if (child instanceof TextBox) {
                  if (((TextBox)child).font.isHDFont()) {
                     if (mustStartBatch) {
                        com.tann.dice.Main.self().stop2d(true);
                        preDraw(batch);
                        mustStartBatch = false;
                     }

                     ((TextBox)child).drawHD(totalOffsetX, totalOffsetY);
                  }
               } else if (child instanceof ScrollPane) {
                  if (mustStartBatch) {
                     com.tann.dice.Main.self().stop2d(true);
                     preDraw(batch);
                     mustStartBatch = false;
                  }

                  ((ScrollPane)child).drawHD(totalOffsetX, totalOffsetY);
               } else if (child instanceof Group && drawHDChildren(batch, (Group)child, totalOffsetX, totalOffsetY, mustStartBatch)) {
                  mustStartBatch = false;
               }
            }
         }

         children.end();
         return !mustStartBatch;
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (!this.text.isEmpty()) {
         if (!this.font.isHDFont()) {
            batch.setColor(this.getColor());
            this.font
               .drawString(batch, this.text, (int)this.getX(), (int)this.getY(), false, this.wiggle, this.sin, this.bold, this.italics, this.first, this.glitch);
         } else {
            if (TestRunner.isTesting()) {
               return;
            }

            if (SkipDraw > 0) {
               return;
            }

            com.tann.dice.Main.self().stop2d(true);
            preDraw(batch);
            this.drawHD(0.0F, 0.0F);
            postDraw();
            com.tann.dice.Main.self().start2d(true);
         }

         super.draw(batch, parentAlpha);
      }
   }
}
