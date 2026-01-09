package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNBracket extends PRNPart {
   final boolean right;

   public PRNBracket(boolean right) {
      this.right = right;
   }

   @Override
   public String regex() {
      return "\\" + this.describe();
   }

   @Override
   protected String describe() {
      return this.right ? ")" : "(";
   }

   @Override
   protected Color getColour() {
      return Colours.light;
   }
}
