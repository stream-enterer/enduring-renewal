package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNHTA extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      return "ability";
   }

   @Override
   protected Color getColour() {
      return Colours.blue;
   }
}
