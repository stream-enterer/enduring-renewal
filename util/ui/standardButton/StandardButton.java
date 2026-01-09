package com.tann.dice.util.ui.standardButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;

public class StandardButton extends Group {
   private StandardButtonStyle style = getDefault();
   private static final int STANDARD_GAP = 4;
   private static final int MIN_HEIGHT = 18;
   private static final int MIN_WIDTH = 30;
   private static final int TINY_WIDTH = 13;
   private static final int TINY_HEIGHT = 14;
   Color col;
   String text;
   static final int gap = 2;
   final Actor contents;
   Runnable runnable;

   private static StandardButtonStyle getDefault() {
      int chopVal = OptionLib.BUTT_STYLE.c();
      return chopVal == 0 ? StandardButtonStyle.Rounded : StandardButtonStyle.values()[chopVal];
   }

   public StandardButton(String text) {
      this(text, Colours.grey, -1, -1);
   }

   public StandardButton(String text, Color col) {
      this(text, col, -1, -1);
   }

   public StandardButton(String text, Color col, int width, int height) {
      this(new TextWriter(text), col, width, height);
      this.text = text;
   }

   public StandardButton(TextureRegion image) {
      this(image, Colours.grey, -1, -1);
   }

   public StandardButton(TextureRegion image, Color imgCol) {
      this(new ImageActor(image, imgCol), Colours.grey, -1, -1);
   }

   public StandardButton(TextureRegion image, Color col, int width, int height) {
      this(image, null, col, width, height);
   }

   public StandardButton(TextureRegion image, Color imgCol, Color col, int width, int height) {
      this(new ImageActor(image, imgCol), col, width, height);
   }

   public StandardButton(Actor contents, Color col) {
      this(contents, col, (int)(contents.getWidth() + 4.0F), (int)(contents.getHeight() + 4.0F));
   }

   public StandardButton(Actor contents, Color col, int width, int height) {
      this.contents = contents;
      this.col = col;
      this.setTransform(false);
      if (width <= 0) {
         width = (int)(contents.getWidth() + 8.0F);
      }

      if (height <= 0) {
         height = (int)(contents.getHeight() + 8.0F);
      }

      width = Math.max(width, 30);
      height = Math.max(height, 18);
      this.setSize(width, height);
      this.addActor(contents);
      Tann.center(contents);
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (StandardButton.this.runnable != null) {
               StandardButton.this.runnable.run();
               return true;
            } else {
               return false;
            }
         }
      });
   }

   public static StandardButton create(String s, TannListener tannListener) {
      StandardButton sb = new StandardButton(s);
      sb.addListener(tannListener);
      return sb;
   }

   public StandardButton makeTiny() {
      return this.makeSize(13.0F, 14.0F);
   }

   public StandardButton makeSize(float width, float height) {
      this.setSize(Math.max(width, this.contents.getWidth() + 4.0F), Math.max(height, this.contents.getHeight() + 2.0F));
      Tann.center(this.contents);
      return this;
   }

   public StandardButton setRunnable(Runnable runnable) {
      this.runnable = runnable;
      return this;
   }

   public StandardButton setStyle(StandardButtonStyle style) {
      this.style = style;
      return this;
   }

   public void draw(Batch batch, float parentAlpha) {
      switch (this.style) {
         case Rounded:
            batch.setColor(Colours.shiftedTowards(Colours.dark, this.col, 0.15F));
            Images.textButtonPatchRoundMiddle.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            batch.setColor(this.col);
            Images.textButtonPatchRoundBorder.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            break;
         case Sides:
            batch.setColor(Colours.dark);
            Draw.fillActor(batch, this);
            batch.setColor(this.col);
            Images.textButtonPatchSides.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            break;
         case Corners:
            batch.setColor(Colours.dark);
            Draw.fillActor(batch, this);
            batch.setColor(this.col);
            Images.textButtonPatchCorners.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            break;
         case SimpleSquare:
            Draw.fillActor(batch, this, Colours.dark, this.col, 1);
      }

      super.draw(batch, parentAlpha);
   }

   public String getText() {
      return this.text;
   }

   public void setBorder(Color color) {
      this.col = color;
   }
}
