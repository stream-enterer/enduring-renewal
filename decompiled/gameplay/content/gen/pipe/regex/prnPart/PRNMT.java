package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNMT extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      return "monster";
   }

   @Override
   protected Color getColour() {
      return Colours.orange;
   }
}
