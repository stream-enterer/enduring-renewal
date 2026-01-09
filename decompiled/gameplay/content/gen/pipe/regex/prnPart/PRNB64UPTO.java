package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Tann;

public class PRNB64UPTO extends PRNPart {
   final int numDigits;

   public PRNB64UPTO(int numDigits) {
      this.numDigits = numDigits;
   }

   @Override
   public String regex() {
      return "(" + "[a-zA-Z0-9%=]*".replace("*", "{1," + this.numDigits + "}") + ")";
   }

   @Override
   protected String describe() {
      return this.numDigits > 4 ? "0$..." : Tann.repeat("$", this.numDigits);
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
