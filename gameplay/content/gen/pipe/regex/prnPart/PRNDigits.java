package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Tann;

public class PRNDigits extends PRNPart {
   final int maxDigits;
   final boolean exactly;

   public PRNDigits(int maxDigits) {
      this(maxDigits, false);
   }

   public PRNDigits(int maxDigits, boolean exactly) {
      this.maxDigits = maxDigits;
      this.exactly = exactly;
   }

   @Override
   public String regex() {
      String optionalMinus = "-{0,1}";
      return this.exactly ? "(" + optionalMinus + "\\d{" + this.maxDigits + "})" : "(" + optionalMinus + "\\d{" + 1 + "," + this.maxDigits + "})";
   }

   @Override
   protected String describe() {
      return Tann.repeat("#", this.maxDigits);
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
