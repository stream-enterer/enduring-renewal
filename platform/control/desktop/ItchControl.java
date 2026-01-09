package com.tann.dice.platform.control.desktop;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class ItchControl extends DesktopControl {
   @Override
   public Color getCol() {
      return Colours.blue;
   }

   @Override
   public String getMainFileString() {
      return "slice-and-dice-3";
   }

   @Override
   public boolean checkVersion() {
      return true;
   }
}
