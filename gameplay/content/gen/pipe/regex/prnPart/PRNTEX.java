package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNTEX extends PRNPart {
   @Override
   public String regex() {
      return "([a-zA-Z0-9%=]*)";
   }

   @Override
   protected String describe() {
      return "tx";
   }

   @Override
   protected Color getColour() {
      return Colours.pink;
   }
}
