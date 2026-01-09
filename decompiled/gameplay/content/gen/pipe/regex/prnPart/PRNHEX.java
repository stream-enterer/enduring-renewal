package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

public class PRNHEX extends PRNPart {
   final int numDigits;

   public PRNHEX(int numDigits) {
      this.numDigits = numDigits;
   }

   @Override
   public String regex() {
      return "[0-9a-fA-F]{" + this.numDigits + "}";
   }

   @Override
   protected String describe() {
      String s = "-";

      for (int i = 0; i < this.numDigits; i++) {
         s = "0" + s;
         s = s + "F";
      }

      return s;
   }
}
