package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNText extends PRNPart {
   final int MAX_LENGTH = 99;

   @Override
   public String regex() {
      return "([a-zA-Z0-9!? éàèùçâêîôûëïüñáíóú¡¿ìòœãõ]{1,99})";
   }

   @Override
   protected String describe() {
      return "text";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
