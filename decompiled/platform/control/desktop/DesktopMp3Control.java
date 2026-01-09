package com.tann.dice.platform.control.desktop;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class DesktopMp3Control extends DesktopControl {
   @Override
   public Color getCol() {
      return Colours.blue;
   }

   @Override
   public String getMusicExtension() {
      return ".mp3";
   }
}
