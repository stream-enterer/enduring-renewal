package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public class PRNS extends PRNPart {
   final String s;

   public PRNS(String s) {
      this.s = s;
   }

   @Override
   public String regex() {
      return this.s;
   }

   @Override
   protected String describe() {
      return this.regex().replaceAll("\\\\", "").toLowerCase();
   }

   @Override
   protected Color getColour() {
      return Colours.light;
   }
}
