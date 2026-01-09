package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNSideSingle extends PRNPart {
   @Override
   public String regex() {
      return "(\\d+(-\\d+){0,1})";
   }

   @Override
   protected String describe() {
      String s1 = ":";
      String s2 = "-";
      return "[green]#[light]" + s2 + "[green]#[light]";
   }

   @Override
   protected Color getColour() {
      return Colours.yellow;
   }
}
