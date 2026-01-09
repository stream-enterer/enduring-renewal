package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNHTLazy extends PRNPart {
   final boolean monster;

   public PRNHTLazy(boolean monster) {
      this.monster = monster;
   }

   @Override
   public String regex() {
      return "(.+?)";
   }

   @Override
   protected String describe() {
      return this.monster ? "monster" : "hero";
   }

   @Override
   protected Color getColour() {
      return Colours.blue;
   }
}
