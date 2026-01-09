package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public enum ChoosableType {
   Levelup(0.0F, 10.0F),
   Item(-8.0F, 20.0F),
   Hero(0.0F, 9.0F),
   Modifier,
   Random(true),
   RandomRange(true),
   Or(true),
   And(true),
   MISSINGNO,
   Skip,
   Enu,
   Replace,
   Value;

   final float lower;
   final float upper;
   boolean meta;

   private ChoosableType() {
      this.lower = -50.0F;
      this.upper = 50.0F;
   }

   private ChoosableType(boolean meta) {
      this();
      this.meta = meta;
   }

   private ChoosableType(float lower, float upper) {
      this.lower = lower;
      this.upper = upper;
   }

   public float getUpper() {
      return this.upper;
   }

   public float getLower() {
      return this.lower;
   }

   public boolean isMeta() {
      return this.meta;
   }

   public String niceName(int t) {
      switch (this) {
         case Modifier:
            if (t < 0) {
               return "curse";
            } else {
               if (t > 0) {
                  return "blessing";
               }

               return "tweak";
            }
         case Item:
            if (t < 0) {
               return "cursed item";
            } else {
               if (t > 0) {
                  return "item";
               }

               return "junk item";
            }
         default:
            return this.name().toLowerCase();
      }
   }

   public Color getColour() {
      return this.getColour(0);
   }

   public Color getColour(int tier) {
      switch (this) {
         case Modifier:
            return tier == 0 ? Colours.yellow : (tier > 0 ? Colours.green : Colours.purple);
         case Item:
            return Colours.grey;
         case Levelup:
            return Colours.yellow;
         case Hero:
            return Colours.yellow;
         default:
            return Colours.grey;
      }
   }

   public char getTag() {
      switch (this) {
         case Modifier:
            return 'm';
         case Item:
            return 'i';
         case Levelup:
            return 'l';
         case Hero:
            return 'g';
         case Random:
            return 'r';
         case Or:
            return 'o';
         case And:
            return 'a';
         case MISSINGNO:
            return '?';
         case RandomRange:
            return 'q';
         case Skip:
            return 's';
         case Enu:
            return 'e';
         case Value:
            return 'v';
         case Replace:
            return 'p';
         default:
            throw new RuntimeException("ach: " + this);
      }
   }

   public static ChoosableType fromTag(char tag) {
      for (ChoosableType value : values()) {
         if (value.getTag() == tag) {
            return value;
         }
      }

      return MISSINGNO;
   }
}
