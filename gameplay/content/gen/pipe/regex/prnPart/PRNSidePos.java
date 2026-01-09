package com.tann.dice.gameplay.content.gen.pipe.regex.prnPart;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import java.util.ArrayList;
import java.util.List;

public class PRNSidePos extends PRNPart {
   @Override
   public String regex() {
      String firstGroup = "(";
      SpecificSidesType[] vals = SpecificSidesType.values();

      for (int i = 0; i < makeValids().size(); i++) {
         SpecificSidesType sst = vals[i];
         firstGroup = firstGroup + sst.getShortName();
         if (i < vals.length - 1) {
            firstGroup = firstGroup + "|";
         }
      }

      if (firstGroup.endsWith("|")) {
         firstGroup = firstGroup.substring(0, firstGroup.length() - 1);
      }

      return firstGroup + ")";
   }

   public static List<SpecificSidesType> makeValids() {
      List<SpecificSidesType> l = new ArrayList<>();
      SpecificSidesType[] vals = SpecificSidesType.values();

      for (int i = 0; i < vals.length; i++) {
         SpecificSidesType sst = vals[i];
         if (sst.validForPipe()) {
            l.add(sst);
         }
      }

      return l;
   }

   @Override
   protected Color getColour() {
      return DEF_USER_DEFINED;
   }

   @Override
   protected String describe() {
      return "sidepos";
   }
}
