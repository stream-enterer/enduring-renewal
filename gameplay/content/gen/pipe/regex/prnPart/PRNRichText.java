package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNRichText extends PRNPart {
   public static boolean invalid(String msg) {
      return false;
   }

   @Override
   public String regex() {
      return "([^&,\\.\\+\\#]*)";
   }

   @Override
   protected String describe() {
      return "richtext";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
