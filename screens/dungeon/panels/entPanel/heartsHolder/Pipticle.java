package com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Pipticle extends Actor {
   float life;
   float ratio;
   float maxLife;

   public Pipticle(float maxLife) {
      this.maxLife = maxLife;
      this.life = maxLife;
   }

   public void act(float delta) {
      super.act(delta);
      com.tann.dice.Main.requestRendering();
      this.life -= delta;
      this.ratio = this.life / this.maxLife;
      if (this.life <= 0.0F) {
         this.remove();
      }
   }
}
