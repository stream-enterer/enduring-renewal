package com.tann.dice.gameplay.trigger;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Collision {
   public static final long REROLLS = bit(12);
   public static final long SPELL = bit(13);
   public static final long ENEMY_SHIELD = bit(14);
   public static final long INCOMING_BONUS = bit(15);
   public static final long PLAYER_HP = bit(16);
   public static final long MONSTER_HP = bit(17);
   public static final long PLAYER_KEYWORD = bit(18);
   public static final long MONSTER_KEYWORD = bit(19);
   public static final long NUM_HEROES = bit(20);
   public static final long TRIGGER_PIP = bit(21);
   public static final long POISON = bit(22);
   public static final long ITEM_REWARD = bit(23);
   public static final long LEVELUP_REWARD = bit(24);
   public static final long RANGED = bit(25);
   public static final long ITEM = bit(26);
   public static final long HERO_POSITION = bit(27);
   public static final long SPECIFIC_LEVEL = bit(28);
   public static final long SPECIFIC_TURN = bit(29);
   public static final long PHYSICAL_DAMAGE = bit(30);
   public static final long MAX_VALUE = bit(31);
   public static final long UNDYING = bit(32);
   public static final long TACTIC = bit(33);
   public static final long VERY_BAD_ITEM = bit(34);
   public static final long HEAL = bit(35);
   public static final long SHIELD = bit(36);
   public static final long MODIFIER = bit(37);
   public static final long BLANK_SIDE = bit(38);
   public static final long DEBUFF = bit(39);
   public static final long GENERIC_ALL_SIDES_HERO = bit(40);
   public static final long ALL_SIDES_MONSTER = bit(41);
   public static final long MONSTER_DEATH = bit(42);
   public static final long PLAYER_DEATH = bit(43);
   public static final long COL_RED = bit(44);
   public static final long COL_BLUE = bit(45);
   public static final long COL_GREY = bit(46);
   public static final long COL_YELLOW = bit(47);
   public static final long COL_ORANGE = bit(48);
   public static final long COL_GREEN = bit(49);
   public static final long COL_PINK = bit(50);
   public static final long LARGE_VALUES = bit(51);
   public static final long PHASE = bit(52);
   public static final long REGEN = bit(53);
   public static final long CURSED_MODE = bit(54);
   public static long ALL_SIDES_HERO_COMPOSITE = sideIndexBit(true, 0)
      + sideIndexBit(true, 1)
      + sideIndexBit(true, 2)
      + sideIndexBit(true, 3)
      + sideIndexBit(true, 4)
      + sideIndexBit(true, 5);
   public static long SPECIFIC_LEVEL_WIDE = SPECIFIC_LEVEL | ITEM_REWARD | LEVELUP_REWARD;
   public static final int NUM_BITS = 51;

   private static long bit(int shift) {
      return 1L << shift;
   }

   public static long sideIndexBit(boolean player, int index) {
      return bit((player ? 0 : 6) + index);
   }

   public static long allSides(Boolean player) {
      if (player == null) {
         return ALL_SIDES_MONSTER | GENERIC_ALL_SIDES_HERO;
      } else {
         return player ? GENERIC_ALL_SIDES_HERO : ALL_SIDES_MONSTER;
      }
   }

   public static List<Modifier> getAllModifiersCollidingWith(long bit) {
      List<Modifier> result = new ArrayList<>();

      for (Modifier m : ModifierLib.getAll()) {
         if (ChoosableUtils.collides(m, bit)) {
            result.add(m);
         }
      }

      return result;
   }

   public static String nameFor(long chk) {
      if (chk == 0L) {
         return "none";
      } else {
         List<String> name = new ArrayList<>();

         for (Field f : Collision.class.getFields()) {
            if (f.getType() == long.class) {
               try {
                  long l = (Long)f.get(null);
                  if ((l & chk) != 0L) {
                     String fieldName = f.getName();
                     if (!fieldName.equalsIgnoreCase("ALL_SIDES_HERO_COMPOSITE")) {
                        name.add(f.getName());
                     }
                  }
               } catch (IllegalAccessException var10) {
                  throw new RuntimeException(var10);
               }
            }
         }

         int ch = 12;

         for (int i = 0; i < 12; i++) {
            if ((chk & 1L << i) != 0L) {
               name.add(firstSideBitsName(i));
            }
         }

         return name.isEmpty() ? "unknown" : Tann.commaList(name);
      }
   }

   private static String firstSideBitsName(int i) {
      if (i < 0 || i > 11) {
         return "hmm??" + i + "?";
      } else {
         return i <= 5 ? "PLAYER_SIDE_" + i : "MONSTER_SIDE_" + (i - 6);
      }
   }

   public static long keyword(Boolean player) {
      if (player == null) {
         return MONSTER_KEYWORD | PLAYER_KEYWORD;
      } else {
         return player ? PLAYER_KEYWORD : MONSTER_KEYWORD;
      }
   }

   public static long hpFor(Boolean player) {
      if (player == null) {
         return PLAYER_HP | MONSTER_HP;
      } else {
         return player ? PLAYER_HP : MONSTER_HP;
      }
   }

   public static long death(Boolean player) {
      if (player == null) {
         return MONSTER_DEATH | PLAYER_DEATH;
      } else {
         return player ? PLAYER_DEATH : MONSTER_DEATH;
      }
   }

   public static long ignored(long bit, long toIgnore) {
      return bit - (bit & toIgnore);
   }

   public static boolean collides(long a, long b) {
      return (a & b) != 0L;
   }
}
