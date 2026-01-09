package com.tann.dice.platform.control.desktop;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class WindowsControlCopyingAndroid extends DesktopControl {
   @Override
   public Color getCol() {
      return Colours.blue;
   }

   @Override
   public boolean unloadWhilePaused() {
      return true;
   }
}
