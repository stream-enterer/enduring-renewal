package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNSideID extends PRNPart {
   @Override
   public String regex() {
      return "([a-z][a-z][a-z][0-9]+)";
   }

   @Override
   protected String describe() {
      return "[orange]typ[grey]###";
   }

   @Override
   protected Color getColour() {
      return Color.ORANGE;
   }
}
