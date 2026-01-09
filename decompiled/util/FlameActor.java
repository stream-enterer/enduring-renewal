package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FlameActor extends Actor {
   static final Color[] cols = new Color[]{Colours.light, Colours.orange, Colours.red, Colours.dark, Colours.withAlpha(Colours.dark, 0.0F).cpy()};
   int size;
   boolean radial;
   float moveAmount;
   float duration;

   public FlameActor(int size, float moveAmount, boolean radial, float duration) {
      this.size = size;
      this.radial = radial;
      this.moveAmount = moveAmount;
      this.duration = duration;
   }

   public void animate(float delay) {
      this.setScale(0.3F);
      this.setColor(0.0F, 0.0F, 0.0F, 0.0F);
      float moveX;
      float moveY;
      if (this.radial) {
         Vector2 random = Tann.randomRadial(this.moveAmount);
         moveX = random.x;
         moveY = random.y;
      } else {
         float randomSide = 10.0F;
         float randomUp = 10.0F;
         moveX = Tann.random(randomSide, -randomSide);
         moveY = this.moveAmount + Tann.random(randomUp);
      }

      this.addAction(
         Actions.sequence(
            Actions.delay(delay),
            Actions.parallel(
               Actions.moveBy(moveX, moveY, this.duration),
               new RainbowAction(this.duration, cols),
               Actions.scaleTo(1.0F, 1.0F, this.duration, Interpolation.pow3Out)
            ),
            Actions.removeActor()
         )
      );
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.fillEllipse(batch, this.getX(), this.getY(), this.size * this.getScaleX(), this.size * this.getScaleY());
   }
}
