package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public class PRNCOL extends PRNPart {
   @Override
   public String regex() {
      return "([0-9a-fA-F]{3}|[0-9a-fA-F]{6})";
   }

   @Override
   protected String describe() {
      return this.make("fff", Colours.red, Colours.green, Colours.blue, Colours.grey);
   }

   private String make(String s, Color... cols) {
      String result = "";

      for (int i = 0; i < s.length(); i++) {
         result = result + TextWriter.getTag(cols[i % cols.length]) + s.charAt(i) + "[cu]";
      }

      return result;
   }
}
