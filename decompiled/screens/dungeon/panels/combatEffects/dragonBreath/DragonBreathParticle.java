package com.tann.dice.screens.dungeon.panels.combatEffects.dragonBreath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class DragonBreathParticle extends Actor {
   final Color[] cols;

   public DragonBreathParticle(Color[] cols) {
      this.cols = cols;
   }

   public void draw(Batch batch, float parentAlpha) {
      float a = this.getColor().a;
      float alpha = Interpolation.pow4Out.apply(a);
      batch.setColor(Colours.withAlpha(Colours.shiftedTowards(this.cols, 1.0F - a), alpha));
      Draw.fillEllipse(batch, this.getX(), this.getY(), this.getWidth() * this.getScaleX(), this.getHeight() * this.getScaleY());
   }
}
