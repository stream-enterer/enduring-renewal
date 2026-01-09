package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tann.dice.gameplay.save.settings.option.FlOption;
import com.tann.dice.statics.sound.music.JukeboxUtils;

public class Slider extends Actor {
   static final int defaultWidth = 66;
   static final int defaultHeight = 13;
   static final int gap = 1;
   private float value;
   private Color backGround;
   private Color foreGround;
   private boolean dragging;
   private String title;
   Runnable slideAction;

   public Slider(String title, float base, Color bg, Color fg) {
      this.title = title;
      this.backGround = bg;
      this.foreGround = fg;
      this.setSize(66.0F, 13.0F);
      this.setValue(base);
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Slider.this.dragging = true;
            event.cancel();
            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Slider.this.dragging = false;
         }
      });
   }

   private void setValue(float value) {
      this.value = value;
   }

   public void act(float delta) {
      super.act(delta);
      if (this.dragging) {
         this.setValue(this.getValueFromPosition(Gdx.input.getX()));
         if (this.slideAction != null) {
            this.slideAction.run();
         }
      }
   }

   private float getValueFromPosition(float x) {
      Vector2 res = Tann.getAbsoluteCoordinates(this);
      float scaled = x / com.tann.dice.Main.scale;
      float translated = scaled - res.x - 1.0F;
      float max = this.getWidth() - 2.0F;
      float result = translated / max;
      return Math.max(0.0F, Math.min(1.0F, result));
   }

   public void addSlideAction(Runnable r) {
      this.slideAction = r;
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(this.foreGround);
      Draw.fillRectangle(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      batch.setColor(this.backGround);
      if (FlOption.isOff(this.getValue())) {
         batch.setColor(Colours.grey);
      }

      Draw.drawRectangle(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1);
      Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY() + 1.0F, (int)((this.getWidth() - 2.0F) * this.value), this.getHeight() - 2.0F);
      if (FlOption.isOff(this.getValue())) {
         batch.setColor(Colours.grey);
         int h = (int)(this.getHeight() - 1.0F);
         int w = (int)(this.getWidth() - 1.0F);
         Draw.drawLine(batch, this.getX(), this.getY(), this.getX() + w, this.getY() + h, 1.0F);
         Draw.drawLine(batch, this.getX(), this.getY() + h, this.getX() + w, this.getY(), 1.0F);
      }

      batch.setColor(JukeboxUtils.SOUND_COL);
      TannFont.font.drawString(batch, this.title, (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + this.getHeight() / 2.0F), 1);
   }

   public float getValue() {
      return this.value;
   }
}
