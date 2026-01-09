package com.tann.dice.screens.debugScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.TannLog;

public class TransSection extends Actor {
   final TextureRegion from;
   final TextureRegion to;
   final int tWidth;
   final int tHeight;
   final int pxPerSection = 1;

   public TransSection(TextureRegion from, TextureRegion to) {
      this(from, to, from.getRegionWidth(), from.getRegionHeight());
   }

   private TransSection(TextureRegion from, TextureRegion to, int tWidth, int tHeight) {
      this.from = from;
      this.to = to;
      this.tWidth = tWidth;
      this.tHeight = tHeight;
      if (from.getRegionWidth() != to.getRegionWidth() || from.getRegionHeight() != to.getRegionHeight()) {
         TannLog.error("bad tiling section: " + from + ":" + to);
      }

      this.setSize(tWidth, tHeight);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.z_white);
      Draw.drawSize(batch, this.from, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      TextureRegion tr = new TextureRegion(this.to.getTexture());
      int minWidth = Math.min(this.tWidth, Math.min(this.from.getRegionWidth(), this.to.getRegionWidth()));
      int minHeight = Math.min(this.tHeight, Math.min(this.from.getRegionHeight(), this.to.getRegionHeight()));
      int px = 1;
      float scale = this.getHeight() / minHeight;

      for (int pixelX = 0; pixelX <= minWidth; pixelX += px) {
         tr.setRegion(this.to.getRegionX() + pixelX, this.to.getRegionY(), px, this.to.getRegionHeight());
         batch.setColor(1.0F, 1.0F, 1.0F, Math.abs((float)pixelX / this.tWidth));
         Draw.drawSize(batch, tr, this.getX() + (pixelX + this.tWidth - minWidth) * scale, this.getY(), (float)Math.ceil(px * scale), this.getHeight());
      }

      tr.setRegion(this.to.getRegionX() + this.to.getRegionWidth() - 1, this.to.getRegionY(), 1, this.to.getRegionHeight());
      batch.setColor(Colours.z_white);
      Draw.drawSize(batch, tr, this.getX() + this.getWidth() - 1.0F, this.getY(), scale, this.getHeight());
      super.draw(batch, parentAlpha);
   }
}
