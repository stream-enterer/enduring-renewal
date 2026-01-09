package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ui.TextWriter;

public class SpeechBubble extends Group {
   public static final int LEFT = 7;
   public static final int RIGHT = 5;
   public static final int UP = 5;
   public static final int DOWN = 13;
   public static final int GAP = 1;
   public static final int TEXT_MAX_WIDTH = 60;
   boolean xFlip;
   TextWriter tw;

   public SpeechBubble(String text) {
      this(text, false);
   }

   public SpeechBubble(String text, boolean xFlip) {
      this.xFlip = xFlip;
      this.setTransform(false);
      this.setTouchable(Touchable.disabled);
      this.tw = new TextWriter(text, 60);
      this.addActor(this.tw);
      this.setSize(this.tw.getWidth() + 7.0F + 5.0F - 2.0F, this.tw.getHeight() + 5.0F + 13.0F - 2.0F);
      if (xFlip) {
         this.tw.setPosition(-this.getWidth() + 7.0F - 1.0F - 2.0F, 12.0F);
      } else {
         this.tw.setPosition(6.0F, 12.0F);
      }

      this.setColor(Colours.red);
   }

   public void act(float delta) {
      super.act(delta);
      float alpha = this.getColor().a;
      this.tw.setAlpha(alpha);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.withAlpha(Colours.dark, this.getColor().a));
      Images.speechPatchCenter.draw(batch, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.xFlip ? -1.0F : 1.0F, 1.0F, 0.0F);
      batch.setColor(this.getColor());
      Images.speechPatchBorder.draw(batch, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.xFlip ? -1.0F : 1.0F, 1.0F, 0.0F);
      super.draw(batch, parentAlpha);
   }
}
