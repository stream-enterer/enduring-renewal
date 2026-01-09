package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;

public class PRNLinked extends PRNPart {
   final PRNPart[] parts;

   public PRNLinked(PRNPart... parts) {
      this.parts = parts;
   }

   @Override
   public String regex() {
      String s = "";

      for (PRNPart part : this.parts) {
         s = s + part.regex();
      }

      return s;
   }

   @Override
   protected String describe() {
      String s = "[grey]";

      for (int i = 0; i < this.parts.length; i++) {
         PRNPart part = this.parts[i];
         s = s + part.getColDesc();
         if (i < this.parts.length - 1) {
            s = s + "[cu]";
         }
      }

      return s;
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }
}
