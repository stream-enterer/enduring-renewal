package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.util.Tann;

public class PRNRect extends PRNPart {
   @Override
   public String regex() {
      return Tann.repeat(PipeRegexNamed.EXACTLY_TWO_DIGITS.regex(), 4);
   }

   @Override
   protected String describe() {
      return "##x4";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
