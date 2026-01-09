package com.tann.dice.gameplay.content.ent.die;

import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;

public class EntDieUtils {
   public static SpecificSidesType fromIndex(int index) {
      switch (index) {
         case 0:
            return SpecificSidesType.Top;
         case 1:
            return SpecificSidesType.Bot;
         case 2:
            return SpecificSidesType.Left;
         case 3:
            return SpecificSidesType.Right;
         case 4:
            return SpecificSidesType.Middle;
         case 5:
         default:
            return SpecificSidesType.RightMost;
      }
   }
}
