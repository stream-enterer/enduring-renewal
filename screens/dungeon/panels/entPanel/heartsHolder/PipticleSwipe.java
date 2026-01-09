package com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class PipticleSwipe extends Pipticle {
   static final float d = -3.9F;
   static final float dx = -3.9F;
   static final float dy = -3.9F;
   final boolean poison;

   public PipticleSwipe(boolean poison) {
      super(0.6F);
      this.poison = poison;
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      float lineDist = Interpolation.pow3Out.apply(Math.min(1.0F, (1.0F - this.ratio) * 2.0F));
      float alpha = Interpolation.pow2In.apply(Math.min(1.0F, this.ratio * 2.0F));
      batch.setColor(Colours.withAlpha(this.poison ? Colours.green : Colours.yellow, alpha));
      int heartOffset = 4;
      Draw.drawLine(
         batch,
         this.getX() + this.getWidth() + heartOffset,
         this.getY() + heartOffset,
         this.getX() + this.getWidth() + heartOffset + -3.9F * lineDist,
         this.getY() + this.getHeight() + heartOffset + -3.9F * lineDist,
         2.0F
      );
   }
}
