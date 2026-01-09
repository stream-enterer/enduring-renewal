package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.Random;

public enum SpecificSidesType {
   Left(new int[]{2}, Images.replaceLeft, new Vector2[]{new Vector2(0.0F, 5.0F)}, "the left side", "left"),
   Middle(new int[]{4}, Images.replaceCentral, new Vector2[]{new Vector2(5.0F, 5.0F)}, "the middle side", "mid"),
   Top(new int[]{0}, Images.replaceTop, new Vector2[]{new Vector2(5.0F, 5.0F)}, "the top side", "top"),
   Bot(new int[]{1}, Images.replaceBot, new Vector2[]{new Vector2(5.0F, 0.0F)}, "the bottom side", "bot"),
   Right(new int[]{3}, Images.replaceOther, new Vector2[]{new Vector2(5.0F, 5.0F)}, "the right side", "right"),
   RightMost(new int[]{5}, Images.replaceRight, new Vector2[]{new Vector2(5.0F, 0.0F)}, "the rightmost side", "rightmost"),
   RightTwo(new int[]{3, 5}, Images.replaceRightmostTwo, new Vector2[]{new Vector2(5.0F, 5.0F), new Vector2(20.0F, 5.0F)}, "the two right sides", "right2"),
   RightThree(
      new int[]{4, 3, 5},
      Images.replaceRightmostThree,
      new Vector2[]{new Vector2(5.0F, 5.0F), new Vector2(20.0F, 5.0F), new Vector2(35.0F, 5.0F)},
      "the three right sides",
      "right3"
   ),
   RightFive(
      new int[]{0, 4, 1, 3, 5},
      Images.replaceRightmostFive,
      new Vector2[]{new Vector2(5.0F, 30.0F), new Vector2(5.0F, 15.0F), new Vector2(5.0F, 0.0F), new Vector2(20.0F, 15.0F), new Vector2(35.0F, 15.0F)},
      "the five right sides",
      "right5"
   ),
   LeftTwo(new int[]{2, 4}, Images.replaceLeftTwo, new Vector2[]{new Vector2(0.0F, 5.0F), new Vector2(15.0F, 5.0F)}, "the two left sides", "left2"),
   Row(
      new int[]{2, 4, 3, 5},
      Images.replaceRow,
      new Vector2[]{new Vector2(0.0F, 5.0F), new Vector2(15.0F, 5.0F), new Vector2(30.0F, 5.0F), new Vector2(45.0F, 5.0F)},
      "the middle row",
      "row"
   ),
   Column(
      new int[]{0, 4, 1},
      Images.replaceColumn,
      new Vector2[]{new Vector2(5.0F, 30.0F), new Vector2(5.0F, 15.0F), new Vector2(5.0F, 0.0F)},
      "the middle column",
      "col"
   ),
   MiddleFour(
      new int[]{0, 4, 1, 3},
      Images.replaceMiddleFour,
      new Vector2[]{new Vector2(5.0F, 30.0F), new Vector2(5.0F, 15.0F), new Vector2(5.0F, 0.0F), new Vector2(20.0F, 15.0F)},
      "the middle four sides",
      "mid4"
   ),
   MiddleTwo(new int[]{4, 3}, Images.replaceMiddleTwo, new Vector2[]{new Vector2(5.0F, 5.0F), new Vector2(20.0F, 5.0F)}, "the middle two sides", "mid2"),
   Wings(new int[]{0, 1}, Images.replaceWings, new Vector2[]{new Vector2(5.0F, 30.0F), new Vector2(5.0F, 0.0F)}, "the top and bottom sides", "topbot"),
   All(
      new int[]{2, 4, 0, 1, 3, 5},
      Images.replaceAll,
      new Vector2[]{
         new Vector2(0.0F, 15.0F),
         new Vector2(15.0F, 15.0F),
         new Vector2(15.0F, 30.0F),
         new Vector2(15.0F, 0.0F),
         new Vector2(30.0F, 15.0F),
         new Vector2(45.0F, 15.0F)
      },
      "all sides",
      "all"
   ),
   PetrifyOrder(
      new int[]{0, 2, 4, 3, 5, 1},
      Images.replaceAll,
      new Vector2[]{
         new Vector2(15.0F, 30.0F),
         new Vector2(0.0F, 15.0F),
         new Vector2(15.0F, 15.0F),
         new Vector2(30.0F, 15.0F),
         new Vector2(45.0F, 15.0F),
         new Vector2(15.0F, 0.0F)
      },
      "petrif??",
      "????",
      true
   ),
   Any(new int[]{2, 4, 0, 1, 3, 5}, Images.replaceAny, new Vector2[]{new Vector2(0.0F, 0.0F)}, "all sides", "???????", true);

   public final int[] sideIndices;
   public final TextureRegion templateImage;
   public final Vector2[] sidePositions;
   public final String description;
   private final String shortName;
   private final boolean weird;

   private SpecificSidesType(int[] indices, TextureRegion tr, Vector2[] pos, String description, String shortName) {
      this(indices, tr, pos, description, shortName, false);
   }

   private SpecificSidesType(int[] indices, TextureRegion tr, Vector2[] pos, String description, String shortName, boolean weird) {
      this.sideIndices = indices;
      this.templateImage = tr;
      this.sidePositions = pos;
      this.description = description;
      this.shortName = shortName;
      this.weird = weird;
   }

   public static SpecificSidesType getNiceSidesTypeSingle(Random r) {
      for (int i = 0; i < 10; i++) {
         SpecificSidesType sst = Tann.random(values(), r);
         if (sst.sideIndices.length == 1) {
            return sst;
         }
      }

      return Left;
   }

   public boolean isWeird() {
      return this.weird;
   }

   public static SpecificSidesType byName(String posName) {
      SpecificSidesType[] vv = values();

      for (int i = 0; i < vv.length; i++) {
         if (vv[i].getShortName().equalsIgnoreCase(posName) || vv[i].name().equalsIgnoreCase(posName)) {
            return vv[i];
         }
      }

      return null;
   }

   public boolean validFor(EntSideState sideState, EntState owner) {
      return this.validIndex(sideState, owner) != -1;
   }

   public int validIndex(EntSideState sideState, EntState owner) {
      for (int index = 0; index < this.sideIndices.length; index++) {
         int replaceIndex = this.sideIndices[index];
         if (sideState.getIndex() == replaceIndex) {
            return index;
         }
      }

      return -1;
   }

   public long getCollisionBits(Boolean player) {
      if (this == All && player != null && player) {
         return Collision.GENERIC_ALL_SIDES_HERO;
      } else if (player == null) {
         TannLog.log("SST coll null player");
         return 0L;
      } else {
         long bit = 0L;

         for (int sideIndex : this.sideIndices) {
            bit |= Collision.sideIndexBit(player, sideIndex);
         }

         if (this.sideIndices.length == 6) {
            bit |= Collision.allSides(player);
         }

         return bit;
      }
   }

   public String getShortName() {
      return this.shortName;
   }

   public String getLowercaseName() {
      return this.name().toLowerCase();
   }

   public TextureRegion getArrowImage() {
      switch (this) {
         case Left:
         case LeftTwo:
            return Images.arrowLeft;
         case Right:
         case RightMost:
         case RightThree:
         case RightTwo:
            return Images.arrowRight;
         case Top:
            return Images.arrowUp;
         case Bot:
            return Images.arrowDown;
         case All:
         case Wings:
         case Column:
         case Middle:
         case MiddleFour:
         case Row:
            return Images.arrowCenter;
         case Any:
         case PetrifyOrder:
            return Images.qmark;
         default:
            return Images.arrowCenter;
      }
   }

   public float getFactor() {
      float result = 0.0F;

      for (int side : this.sideIndices) {
         switch (side) {
            case 0:
            case 1:
               result += 0.165F;
               break;
            case 2:
               result += 0.32F;
               break;
            case 3:
               result += 0.09F;
               break;
            case 4:
               result += 0.22F;
               break;
            case 5:
               result += 0.04F;
         }
      }

      return result;
   }

   public static SpecificSidesType getNiceSidesType(Random r) {
      for (int i = 0; i < 5; i++) {
         SpecificSidesType sst = Tann.random(values(), r);
         if (sst != PetrifyOrder && sst != Any && sst != All) {
            return sst;
         }
      }

      return Wings;
   }

   public boolean validForPipe() {
      return !this.getShortName().contains("?");
   }
}
