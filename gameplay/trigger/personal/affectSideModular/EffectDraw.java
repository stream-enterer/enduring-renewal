package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.util.Colours;

public class EffectDraw {
   TextureRegion textureRegion;
   Color col = Colours.z_white;
   private boolean notAlone = false;

   public EffectDraw(TextureRegion textureRegion) {
      this.textureRegion = textureRegion;
   }

   public EffectDraw(TextureRegion textureRegion, Color col) {
      this.textureRegion = textureRegion;
      this.col = col;
   }

   public EffectDraw() {
   }

   public void draw(Batch batch, int x, int y, int index) {
      this.draw(batch, x, y);
   }

   public void draw(Batch batch, int x, int y) {
      if (this.textureRegion != null) {
         int size = 16;
         int off = size / 2;
         batch.setColor(this.col);
         batch.draw(this.textureRegion, x + off - this.textureRegion.getRegionWidth() / 2, y + off - this.textureRegion.getRegionHeight() / 2);
      }
   }

   public void markNotAlone() {
      this.notAlone = true;
   }

   public boolean isNotAlone() {
      return this.notAlone;
   }
}
