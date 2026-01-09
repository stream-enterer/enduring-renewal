package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNHTMulti extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      String s = "+";
      return "hero[light]" + s + "[yellow]hero" + ellipses();
   }

   @Override
   protected Color getColour() {
      return Colours.yellow;
   }
}
