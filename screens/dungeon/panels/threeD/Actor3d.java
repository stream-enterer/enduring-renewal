package com.tann.dice.screens.dungeon.panels.threeD;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.test.util.TestRunner;

public abstract class Actor3d extends Actor {
   public void draw(Batch batch, float parentAlpha) {
      if (!TestRunner.isTesting()) {
         super.draw(batch, parentAlpha);
         com.tann.dice.Main.self().stop2d(true);
         this.draw3d();
         com.tann.dice.Main.self().start2d(true);
      }
   }

   protected abstract void draw3d();
}
