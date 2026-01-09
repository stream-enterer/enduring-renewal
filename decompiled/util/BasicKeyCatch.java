package com.tann.dice.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class BasicKeyCatch extends Group implements KeyListen {
   public BasicKeyCatch(Actor a) {
      this.addActor(a);
      this.setSize(a.getWidth(), a.getHeight());
      this.setTransform(false);
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 111:
            return false;
         default:
            return true;
      }
   }
}
