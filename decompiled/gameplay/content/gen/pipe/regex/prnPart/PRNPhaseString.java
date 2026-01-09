package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNPhaseString extends PRNPart {
   @Override
   public String regex() {
      return "(.+)";
   }

   @Override
   protected String describe() {
      return "phase";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
