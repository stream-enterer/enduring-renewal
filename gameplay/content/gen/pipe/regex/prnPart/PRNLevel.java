package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNLevel extends PRNDigits {
   public PRNLevel() {
      super(3, false);
   }

   @Override
   protected String describe() {
      return "lvl";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
