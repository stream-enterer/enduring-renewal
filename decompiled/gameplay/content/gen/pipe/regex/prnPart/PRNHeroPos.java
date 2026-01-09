package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;

public class PRNHeroPos extends PRNPart {
   @Override
   public String regex() {
      String firstGroup = "(";
      HeroPosition[] vals = HeroPosition.values();

      for (int i = 0; i < vals.length; i++) {
         HeroPosition hp = vals[i];
         firstGroup = firstGroup + hp.shortName().toLowerCase() + "|" + hp.veryShortName().toLowerCase();
         if (i < vals.length - 1) {
            firstGroup = firstGroup + "|";
         }
      }

      return firstGroup + ")";
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }

   @Override
   protected String describe() {
      return "heropos";
   }
}
