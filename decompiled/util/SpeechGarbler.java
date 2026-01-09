package com.tann.dice.util;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpeechGarbler {
   public static String garble(String text) {
      int att = 0;

      String maybe;
      for (maybe = text; maybe.equals(text) && att < 50; att++) {
         maybe = randomGarble(text);
      }

      return maybe;
   }

   public static String garble(Ent ent, String text) {
      text = com.tann.dice.Main.t(text);
      return Tann.chance(0.01F) ? "[notranslate][" + ent.getName(false) + "]: " + garble(text) : "[notranslate]" + garble(text);
   }

   private static String randomGarble(String s) {
      return s.length() < 2 ? s : Tann.pick(SpeechGarbler.GarbleType.values()).affect(s);
   }

   private static String shuffleString(String string) {
      List<String> letters = Arrays.asList(string.split(""));
      Collections.shuffle(letters);
      String shuffled = "";

      for (String letter : letters) {
         shuffled = shuffled + letter;
      }

      return shuffled;
   }

   private static enum GarbleType {
      MiddleLetters,
      Reverse,
      ReplaceVowels,
      ReplaceCons,
      Cough,
      Bold,
      Italics,
      Glitchy,
      Sin,
      Coloured,
      CutOffMiddle,
      FullShuffle,
      Anagrams,
      WordShuffle,
      Items,
      Rotate,
      Count,
      Sandwich,
      TwoRandom,
      ThreeRandom,
      RandomReplace;

      private String affect(String s) {
         try {
            switch (this) {
               case MiddleLetters:
                  if (s.length() < 3) {
                     return s;
                  }

                  return s.substring(0, s.length() / 2) + Tann.randomString(Tann.randomInt(2) + 1) + s.substring(s.length() / 2);
               case Sandwich:
                  return s.substring(0, (int)(s.length() / 4.0F))
                     + s.substring((int)(s.length() / 5.0F), (int)(s.length() * 4 / 5.0F))
                     + s.substring((int)(s.length() * 3 / 4.0F));
               case Reverse:
                  return new StringBuffer(s).reverse().toString();
               case RandomReplace:
                  return s.replaceAll("[" + Tann.randomString(8) + "]", Tann.randomString(Tann.randomInt(2) + 1));
               case ReplaceVowels:
                  return s.replaceAll("[aeiou]", Tann.randomString(Tann.randomInt(2) + 1));
               case ReplaceCons:
                  return s.replaceAll("[tshpc]", Tann.randomString(Tann.randomInt(2) + 1));
               case CutOffMiddle:
                  return s.substring(s.length() / 4, (int)(s.length() * 3 / 4.0F));
               case Cough:
                  return "[i]cough[i] " + s;
               case Bold:
                  return "[b]" + s;
               case Italics:
                  return "[i]" + s;
               case Glitchy:
                  return "[g]" + s;
               case Coloured:
                  return TextWriter.getTag(Tann.pick(Colours.palette)) + s;
               case TwoRandom:
                  return SpeechGarbler.randomGarble(SpeechGarbler.randomGarble(s));
               case ThreeRandom:
                  return SpeechGarbler.randomGarble(SpeechGarbler.randomGarble(SpeechGarbler.randomGarble(s)));
               case FullShuffle:
                  return SpeechGarbler.shuffleString(s);
               case WordShuffle: {
                  String[] words = s.split(" ");
                  Tann.shuffle(words);
                  return Tann.commaList(Arrays.asList(words), " ", " ");
               }
               case Anagrams: {
                  String[] words = s.split(" ");

                  for (int i = 0; i < words.length; i++) {
                     words[i] = SpeechGarbler.shuffleString(words[i]);
                  }

                  return Tann.commaList(Arrays.asList(words), " ", " ");
               }
               case Rotate:
                  int by = Tann.randomInt(s.length() - 1) + 1;
                  return s.substring(by) + s.substring(0, by);
               case Sin:
                  return "[sin]" + s;
               case Items:
                  return s + " [" + ItemLib.random() + "]";
               case Count:
                  return s.length() / 3 + s.substring(s.length() / 3);
               default:
                  return "Unset: " + s;
            }
         } catch (Exception var4) {
            return "crashed";
         }
      }
   }
}
