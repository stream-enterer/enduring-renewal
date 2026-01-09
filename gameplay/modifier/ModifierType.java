package com.tann.dice.gameplay.modifier;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public enum ModifierType {
   Blessing(Colours.green),
   Curse(Colours.purple),
   Tweak(Colours.yellow),
   Unrated(Colours.orange);

   final Color c;

   private ModifierType(Color c) {
      this.c = c;
   }

   public static ModifierType fromTier(int tier) {
      if (tier == 0) {
         return Tweak;
      } else {
         return tier > 0 ? Blessing : Curse;
      }
   }

   public Color getC() {
      return this.c;
   }

   public static ModifierType fromTier(Integer min, Integer max) {
      if (min == null) {
         return Curse;
      } else if (max == null) {
         return Blessing;
      } else if (Math.signum((float)min.intValue()) != Math.signum((float)max.intValue())) {
         return null;
      } else if (min == 0 && max == 0) {
         return Tweak;
      } else {
         return min > 0 ? Blessing : Curse;
      }
   }
}
