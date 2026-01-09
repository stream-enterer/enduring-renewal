package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ImageActor extends Actor {
   public TextureRegion tr;
   boolean xFlip;

   public ImageActor(TextureRegion tr) {
      this(tr, Colours.z_white);
   }

   public ImageActor(TextureRegion tr, boolean flipped) {
      this(tr, Colours.z_white);
      if (flipped) {
         this.setXFlipped(true);
      }
   }

   public ImageActor(TextureRegion tr, Color color) {
      this.setImage(tr);
      if (color != null) {
         this.setColor(color);
      }
   }

   public void setImage(TextureRegion tr) {
      this.tr = tr;
      this.setSize(tr.getRegionWidth(), tr.getRegionHeight());
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(this.getColor());
      if (this.xFlip) {
         Draw.drawFlipped(batch, this.tr, (int)this.getX(), (int)this.getY(), true, false);
      } else {
         batch.draw(this.tr, (int)this.getX(), (int)this.getY());
      }
   }

   public void setXFlipped(boolean b) {
      this.xFlip = b;
   }
}
