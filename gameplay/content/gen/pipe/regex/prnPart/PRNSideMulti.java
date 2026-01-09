package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNSideMulti extends PRNPart {
   @Override
   public String regex() {
      return "([\\d\\-:]*)";
   }

   @Override
   protected String describe() {
      String s1 = ":";
      String s2 = "-";
      return "[green]#[light]" + s2 + "[green]#[light]" + s1 + "[grey]...";
   }

   @Override
   protected Color getColour() {
      return Colours.yellow;
   }
}
