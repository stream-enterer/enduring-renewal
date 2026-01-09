package com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class PipticleHeart extends Pipticle {
   TextureRegion tr;

   public PipticleHeart(HPHolder.PipSize big) {
      super(0.6F);
      switch (big) {
         case normal:
            this.tr = Images.hearticle;
            break;
         case little:
            this.tr = Images.hearticle_small;
            break;
         case pixel:
            this.tr = Draw.getSq();
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      float alpha = Chrono.i.apply(Math.min(1.0F, this.ratio));
      batch.setColor(Colours.withAlpha(Colours.NEON_RED, alpha));
      Draw.draw(batch, this.tr, (float)((int)this.getX()), (float)((int)this.getY()));
   }
}
