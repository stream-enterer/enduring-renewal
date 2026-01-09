package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNHSL extends PRNPart {
   @Override
   public String regex() {
      return "\\.hs[lv]\\.";
   }

   @Override
   public String getActualReplacement() {
      return ".hsv.";
   }

   @Override
   protected String describe() {
      return this.getActualReplacement();
   }

   @Override
   protected Color getColour() {
      return Colours.light;
   }
}
