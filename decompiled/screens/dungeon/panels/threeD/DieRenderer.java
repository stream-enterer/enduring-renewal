package com.tann.dice.screens.dungeon.panels.threeD;

import com.tann.dice.statics.bullet.BulletStuff;

public class DieRenderer extends Actor3d {
   @Override
   protected void draw3d() {
      BulletStuff.render();
   }
}
