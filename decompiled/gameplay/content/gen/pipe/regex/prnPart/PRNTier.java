package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNTier extends PRNDigits {
   public PRNTier(int digits) {
      super(digits, digits == 1);
   }

   @Override
   protected String describe() {
      return "#";
   }

   @Override
   protected Color getColour() {
      return Colours.green;
   }
}
