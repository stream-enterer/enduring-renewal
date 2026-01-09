package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Tann;

public class PRNHEXUPTO extends PRNPart {
   final int numDigits;

   public PRNHEXUPTO(int numDigits) {
      this.numDigits = numDigits;
   }

   @Override
   public String regex() {
      return "([0-9a-fA-F]{1," + this.numDigits + "})";
   }

   @Override
   protected String describe() {
      return this.numDigits > 4 ? "ff..." : Tann.repeat("f", this.numDigits);
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
