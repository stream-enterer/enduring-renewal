package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNDelta extends PRNPart {
   final int digits;

   public PRNDelta(int digits) {
      this.digits = digits;
   }

   @Override
   public String regex() {
      return "([-+]{0,1}\\d{1," + this.digits + "})";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }

   @Override
   protected String describe() {
      return "+-";
   }
}
