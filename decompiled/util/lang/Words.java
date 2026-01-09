package com.tann.dice.util.lang;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;

public class Words {
   public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
   public static final String DOUBLE_ALPHABET = "abcdefghijklmnopqrstuvwxyz" + "abcdefghijklmnopqrstuvwxyz".toUpperCase();
   public static final String ALL_EFF = "all damage and enemy effects";
   private static final int MAX_MANA_STRING = 3;
   public static String[] numerals = new String[]{
      "-",
      "I",
      "II",
      "III",
      "IV",
      "V",
      "VI",
      "VII",
      "VIII",
      "IX",
      "X",
      "XI",
      "XII",
      "XIII",
      "XIV",
      "XV",
      "XVI",
      "XVII",
      "XVIII",
      "XIX",
      "XX",
      "XXI",
      "XXII",
      "XXIII",
      "XXIV",
      "XXV",
      "XXVI",
      "XXVII",
      "XXVIII",
      "XXIX",
      "XXX",
      "XXXI",
      "XXXII",
      "XXXIII",
      "XXXIV",
      "XXXV",
      "XXXVI",
      "XXXVII",
      "XXXVIII",
      "XXXIX",
      "XL",
      "XLI",
      "XLII",
      "XLIII",
      "XLIV",
      "XLV",
      "XLVI",
      "XLVII",
      "XLVIII",
      "XLIX",
      "L"
   };

   public static String entName(Eff eff, Boolean plural) {
      return entName(null, eff.isFriendly(), plural);
   }

   public static String entName(boolean player, Boolean plural) {
      return entName(null, player, plural);
   }

   public static String entName(Boolean playerSource, boolean ally, Boolean plural) {
      if (playerSource != null) {
      }

      if (ally) {
         if (plural == null) {
            return "ally";
         } else {
            return plural ? "allies" : "an ally";
         }
      } else if (plural == null) {
         return "enemy";
      } else {
         return plural ? "enemies" : "an enemy";
      }
   }

   public static String ordinal(int i) {
      return i == 0 ? ordinalLong(i) : ordinalShort(i);
   }

   public static String ordinalLong(int i) {
      if (i < 0) {
         return "negative-" + ordinalLong(Math.abs(i));
      } else {
         switch (i) {
            case 0:
               return "zeroth";
            case 1:
               return "first";
            case 2:
               return "second";
            case 3:
               return "third";
            case 4:
               return "fourth";
            case 5:
               return "fifth";
            case 6:
               return "sixth";
            case 7:
               return "seventh";
            case 8:
               return "eighth";
            case 9:
               return "ninth";
            case 10:
               return "tenth";
            default:
               return i + "th";
         }
      }
   }

   public static String ordinalShort(Integer i) {
      if (i < 0) {
         return "negative-" + ordinalShort(Math.abs(i));
      } else {
         switch (i) {
            case 0:
               return "0th";
            case 1:
               return "1st";
            case 2:
               return "2nd";
            case 3:
               return "3rd";
            case 4:
               return "4th";
            case 5:
               return "5th";
            case 6:
               return "6th";
            case 7:
               return "7th";
            case 8:
               return "8th";
            case 9:
               return "9th";
            case 10:
               return "10th";
            default:
               return i + "th";
         }
      }
   }

   public static String nTimes(int n) {
      switch (n) {
         case 0:
            return "never";
         case 1:
            return "once";
         case 2:
            return "twice";
         case 3:
            return "thrice";
         default:
            return n + " times";
      }
   }

   public static String multiple(int value) {
      switch (value) {
         case 2:
            return "double";
         case 3:
            return "triple";
         case 4:
            return "quadruple";
         case 5:
            return "quintuple";
         default:
            return "x" + value;
      }
   }

   public static String singular(String name) {
      if (name == null) {
         return "!!null plural!!";
      } else if (name.length() == 0) {
         return "!!0-length plural!!";
      } else {
         return isVowel(TextWriter.stripTags(name).charAt(0)) ? "an " + name : "a " + name;
      }
   }

   public static String fullPlural(String name, int amt) {
      return amt == 1 ? singular(name) : amt + " " + plural(name, amt);
   }

   private static boolean isVowel(char charAt) {
      switch (charAt) {
         case 'A':
         case 'E':
         case 'I':
         case 'O':
         case 'U':
         case 'a':
         case 'e':
         case 'i':
         case 'o':
         case 'u':
            return true;
         default:
            return false;
      }
   }

   public static String manaString(boolean picture) {
      return picture ? "[white][mana][cu]" : "mana";
   }

   public static String manaString() {
      return manaString(false);
   }

   public static String manaString(int amt) {
      return manaString(amt, false);
   }

   public static String manaString(int amt, boolean picture) {
      if (amt < 0) {
         return "[pink]" + amt + "[cu][nbp][nbp][white][mana][cu]??";
      } else if (amt == 0) {
         return "0 " + manaString(picture);
      } else {
         return amt <= 3 && picture ? "[white]" + Tann.repeat(manaString(true), "[nbp][nbp]", amt) + "[cu]" : amt + " " + manaString(picture);
      }
   }

   public static String plusString(int bonus) {
      return plusString(bonus >= 0);
   }

   public static String plusString(boolean positive) {
      return positive ? "[plus]" : "[purple][minus]";
   }

   public static String getMaybeInfinityString(int i) {
      return i == 999 ? "[p][infinite]" : "" + i;
   }

   public static String plural(String word) {
      return plural(word, true);
   }

   public static String plural(String word, int value) {
      return plural(word, Math.abs(value) != 1);
   }

   public static String plural(String word, boolean plural) {
      if (!plural) {
         return word;
      } else {
         switch (word) {
            case "this":
               return "these";
            case "is":
               return "are";
            case "does":
               return "do";
            default:
               if (word.endsWith("o")) {
                  return word + "es";
               } else if (word.endsWith("lf")) {
                  return word.substring(0, word.length() - 1) + "ves";
               } else if (word.endsWith("s") || word.endsWith("z")) {
                  return word;
               } else {
                  return word.endsWith("ty") ? word.substring(0, word.length() - 1) + "ies" : word + "s";
               }
         }
      }
   }

   public static String getTierString(int tier) {
      return getTierString(tier, false);
   }

   public static String getTierString(int tier, boolean includeColour) {
      String result = "";
      if (includeColour) {
         if (tier > 0) {
            result = "[green]";
         } else if (tier < 0) {
            result = "[purple]";
         } else {
            result = "[grey]";
         }
      }

      boolean roman = OptionLib.ROMAN_MODE.c();
      int maybeAbs = includeColour ? Math.abs(tier) : tier;
      String character = roman ? getRomanNumerals(maybeAbs) : "" + maybeAbs;
      return result + character + (result.length() == 0 ? "" : "[cu]");
   }

   public static String getRomanNumerals(int i) {
      if (i < 0 && Math.abs(i) < numerals.length) {
         return "-" + numerals[Math.abs(i)];
      } else {
         return i < numerals.length ? numerals[i] : "???";
      }
   }

   public static boolean startsWithVowel(String str) {
      return "aeiouAEIOU".indexOf(str.charAt(0)) != -1;
   }

   public static String capitaliseFirst(String s) {
      return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
   }

   public static String capitaliseWords(String text) {
      if (text != null && !text.isEmpty()) {
         StringBuilder converted = new StringBuilder();
         boolean convertNext = true;

         for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
               convertNext = true;
            } else if (convertNext) {
               ch = Character.toTitleCase(ch);
               convertNext = false;
            } else {
               ch = Character.toLowerCase(ch);
            }

            converted.append(ch);
         }

         return converted.toString();
      } else {
         return text;
      }
   }

   public static String describePipDelta(int bonus) {
      return Tann.delta(bonus) + " " + plural("pip", bonus);
   }

   public static String aOrAn(String str) {
      return (startsWithVowel(str) ? "an " : "a ") + str;
   }

   public static String spabKeyword(boolean plural) {
      return spab(plural, false);
   }

   public static String spab(boolean plural) {
      return spab(plural, UnUtil.isLocked(Feature.TACTICS));
   }

   private static String spab(boolean plural, boolean locked) {
      return plural("ability", plural);
   }

   public static String capitalsOnly(String s) {
      return s.replaceAll("[a-z]", "");
   }
}
