package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNSCapturedOneOf extends PRNPart {
   final String[] s;

   public PRNSCapturedOneOf(String[] s) {
      this.s = s;
   }

   @Override
   public String regex() {
      String firstGroup = "(";

      for (int i = 0; i < this.s.length; i++) {
         String pref = this.s[i];
         firstGroup = firstGroup + pref;
         if (i < this.s.length - 1) {
            firstGroup = firstGroup + "|";
         }
      }

      return firstGroup + ")";
   }

   @Override
   protected String describe() {
      return "[light]" + this.s[0] + "[cu]";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
