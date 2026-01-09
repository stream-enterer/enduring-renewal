package com.tann.dice.screens.dungeon.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;

public class PortBoard {
   public static int getPortboardHeight() {
      return com.tann.dice.Main.height - BackgroundHolder.getHFull() / com.tann.dice.Main.scale;
   }

   static void drawTopIfNecessary(SpriteBatch batch) {
      if (com.tann.dice.Main.isPortrait()) {
         batch.setColor(Colours.z_white);
         TextureRegion tr = ImageUtils.loadExtBig("dungeon/portboard");
         int sz = Gdx.graphics.getHeight() - BackgroundHolder.getHFull();
         batch.draw(tr, 0.0F, Gdx.graphics.getHeight() - sz, Gdx.graphics.getWidth(), sz);
      }
   }
}
