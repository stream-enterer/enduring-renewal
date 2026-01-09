package com.tann.dice.util;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array.ArrayIterator;

public class ShakeAction extends Action {
   final float startTime;
   final float frequency;
   float timeLeft;
   float startMagnitude;
   Interpolation interpolation;
   float startX;
   float startY;
   float prevX;
   float prevY;
   boolean started;
   float rand;
   boolean cancelled;

   public ShakeAction(float magnitude, float frequency, float time, Interpolation interpolation) {
      this.startTime = time;
      this.timeLeft = time;
      this.startMagnitude = magnitude;
      this.frequency = frequency;
      this.interpolation = interpolation;
      this.rand = Tann.random() * 100.0F;
   }

   public void setActor(Actor actor) {
      this.cancelled = true;
      if (actor != null) {
         this.cancelled = false;
         ArrayIterator var2 = actor.getActions().iterator();

         while (var2.hasNext()) {
            Action a = (Action)var2.next();
            if (a instanceof ShakeAction) {
               this.cancelled = true;
            }
         }
      }

      super.setActor(actor);
   }

   private void setupStart() {
      this.startX = this.target.getX();
      this.startY = this.target.getY();
   }

   private void setupSPrev() {
      this.prevX = this.target.getX();
      this.prevY = this.target.getY();
   }

   public boolean act(float delta) {
      if (this.cancelled) {
         return true;
      } else {
         if (!this.started) {
            this.started = true;
            this.setupStart();
         } else if (this.prevX != this.target.getX() && this.startX != this.target.getX()
            || this.prevY != this.target.getY() && this.startY != this.target.getY()) {
            this.setupStart();
         }

         float ratio = this.timeLeft / this.startTime;
         float currentMagnitude = this.startMagnitude * this.interpolation.apply(ratio);
         this.target
            .setPosition(
               (float)(this.startX + Noise.noise(this.rand, this.frequency * com.tann.dice.Main.secs) * currentMagnitude),
               (float)(this.startY + Noise.noise(100.0F + this.rand, this.frequency * com.tann.dice.Main.secs) * currentMagnitude)
            );
         if (ratio <= 0.0F) {
            this.target.setPosition(this.startX, this.startY);
            return true;
         } else {
            this.timeLeft -= delta;
            this.setupSPrev();
            return false;
         }
      }
   }
}
