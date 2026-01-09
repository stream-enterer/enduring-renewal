package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.util.Colours;

public class PRNHC extends PRNPart {
   @Override
   public String regex() {
      String cols = "(";
      HeroCol[] vals = HeroCol.values();

      for (int i = 0; i < vals.length; i++) {
         cols = cols + vals[i].colName + "|";
      }

      for (int i = 0; i < vals.length; i++) {
         cols = cols + vals[i].shortName();
         if (i < vals.length - 1) {
            cols = cols + "|";
         }
      }

      return cols + ")";
   }

   @Override
   protected String describe() {
      return "herocol";
   }

   @Override
   protected Color getColour() {
      return Colours.red;
   }
}
