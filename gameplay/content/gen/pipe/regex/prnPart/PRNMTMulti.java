package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNMTMulti extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      String s = "+";
      return "monster[light]" + s + ellipses();
   }

   @Override
   protected Color getColour() {
      return Colours.orange;
   }
}
