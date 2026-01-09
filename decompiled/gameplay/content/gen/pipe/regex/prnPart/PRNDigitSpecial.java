package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNDigitSpecial extends PRNPart {
   final int min;
   final int max;

   public PRNDigitSpecial(int min, int max) {
      if (min >= 0 && min <= 9 && max >= 0 && max <= 9 && min < max) {
         this.min = min;
         this.max = max;
      } else {
         throw new RuntimeException("eep");
      }
   }

   @Override
   public String regex() {
      return "([" + this.min + "-" + this.max + "])";
   }

   @Override
   protected String describe() {
      return this.min + "-" + this.max;
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
