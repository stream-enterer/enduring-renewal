package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public abstract class PRNPart {
   protected static final Color DEF_USER_DEFINED = Colours.green;

   public abstract String regex();

   protected abstract String describe();

   protected Color getColour() {
      return Colours.text;
   }

   public String getColDesc() {
      return TextWriter.getTag(this.getColour()) + this.describe() + "[cu]";
   }

   public String getActualReplacement() {
      return this.regex();
   }

   @Override
   public String toString() {
      return this.describe();
   }

   protected static String ellipses() {
      return "[grey]...[cu]";
   }
}
