package com.tann.dice.screens.dungeon.panels.Explanel;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.screens.dungeon.DungeonScreen;

public abstract class InfoPanel extends Group {
   public int getNiceY() {
      return com.tann.dice.Main.isPortrait()
         ? getPortraitPanelY()
         : (int)(DungeonScreen.getBottomButtonHeight() + (com.tann.dice.Main.height - DungeonScreen.getBottomButtonHeight()) / 2 - this.getHeight() / 2.0F);
   }

   public static int getPortraitPanelY() {
      return (int)(com.tann.dice.Main.height * 2 / 3.0F);
   }

   public int getNiceX() {
      return (int)(com.tann.dice.Main.width / 2 - this.getWidth() / 2.0F);
   }
}
