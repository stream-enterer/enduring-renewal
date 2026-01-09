package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNEntityOrItem extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      return "[yellow]a[cu][orange]n[cu][grey]y[cu]";
   }

   @Override
   protected Color getColour() {
      return Colours.yellow;
   }
}
