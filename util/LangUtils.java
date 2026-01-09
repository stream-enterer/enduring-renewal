package com.tann.dice.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LangUtils {
   public static Actor makeSummary() {
      Pixl p = new Pixl(2);

      for (String s : fetchTop()) {
         p.actor(new Pixl(3).border(Colours.grey).text(s).pix(), com.tann.dice.Main.width * 0.8F);
      }

      return p.pix();
   }

   private static List<String> fetchTop() {
      List<String> all = getStrings();
      clean(all);
      int amt = 1;
      List<String> tops = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         List<String> t = getTop(all, (float)(0.9F / Math.pow(2.0, amt - i - 1)), amt - i);
         tops.addAll(t);
         removeTops(all, t);
         clean(all);
      }

      return tops;
   }

   private static void removeTops(List<String> src, List<String> remove) {
      for (int i = 0; i < src.size(); i++) {
         for (String rs : remove) {
            String s = src.get(i);
            src.set(i, s.replaceAll(Pattern.quote(rs), " "));
         }
      }
   }

   private static String getConcat(List<String> all) {
      StringBuilder rs = new StringBuilder();

      for (String s : all) {
         rs.append(s).append(" ");
      }

      return rs.toString();
   }

   private static List<String> getUnsortedKeys(List<String> src, int targetWords) {
      List<String> possibleWords = new ArrayList<>();

      for (String s : src) {
         String[] words = s.split(" ");

         for (int startIndex = 0; startIndex < words.length - targetWords; startIndex++) {
            StringBuilder r = new StringBuilder();

            for (int workIndex = 0; workIndex < targetWords; workIndex++) {
               r.append(words[startIndex + workIndex]);
               if (workIndex < targetWords - 1) {
                  r.append(" ");
               }
            }

            String toAdd = r.toString();
            if (toAdd != null) {
               possibleWords.add(toAdd);
            }
         }
      }

      Tann.uniquify(possibleWords);
      return possibleWords;
   }

   private static List<String> getTop(List<String> srcList, float threshold, int targetWords) {
      List<String> keys = getUnsortedKeys(srcList, targetWords);
      Map<String, Integer> map = getMap(keys, srcList);
      int numWords = 0;

      for (String s : map.keySet()) {
         numWords += map.get(s);
      }

      return getTop(keys, numWords, map, threshold);
   }

   private static Map<String, Integer> getMap(List<String> keys, List<String> srcList) {
      Map<String, Integer> map = new HashMap<>();

      for (String key : keys) {
         int count = 0;
         String q = Pattern.quote(key);

         for (String s : srcList) {
            int index = 0;

            while (index < s.length() - key.length() && index >= 0) {
               if (s.startsWith(key, index)) {
                  count++;
               }

               index = s.indexOf(" ", index);
               if (index != -1) {
                  index++;
               }
            }
         }

         map.put(key, count);
      }

      return map;
   }

   private static List<String> getTop(List<String> keys, int numWords, final Map<String, Integer> freq, float threshold) {
      Collections.sort(keys, new Comparator<String>() {
         public int compare(String o1, String o2) {
            return Integer.compare(freq.get(o2), freq.get(o1));
         }
      });
      int targetWords = (int)(numWords * threshold);
      List<String> result = new ArrayList<>();

      for (String key : keys) {
         targetWords -= freq.get(key);
         result.add(key);
         if (targetWords < 0) {
            return result;
         }
      }

      throw new RuntimeException("eep");
   }

   private static void clean(List<String> strings) {
      for (int i = 0; i < strings.size(); i++) {
         String s = strings.get(i);
         String rep = s.replaceAll(",", " ")
            .replaceAll(":", "")
            .replaceAll("[0-999]", "N")
            .replaceAll("\\[.*?\\]", " ")
            .replaceAll("'", "")
            .replaceAll(" +", " ")
            .replaceAll(" +$", "")
            .replaceAll("^ +", "")
            .toLowerCase();
         strings.set(i, rep);
      }
   }

   private static List<String> getStrings() {
      List<String> result = new ArrayList<>();

      for (EntSide es : EntSidesLib.getAllSidesWithValue()) {
         result.add(es.getBaseEffect().describe());
      }

      for (Modifier m : ModifierLib.getAll()) {
         result.add(m.getFullDescription());
         result.add(m.describe());
      }

      for (EntType entType : EntTypeUtils.getAll()) {
         for (Trait trait : entType.traits) {
            result.add(trait.personal.describeForSelfBuff());
         }
      }

      for (Item item : ItemLib.getMasterCopy()) {
         result.add(item.describe());
         result.add(item.getDescription());
      }

      Tann.removeNulls(result);
      List<String> transformedResult = new ArrayList<>();

      for (int i = 0; i < result.size(); i++) {
         transformedResult.addAll(Arrays.asList(result.get(i).split("\\[n\\]")));
      }

      return transformedResult;
   }
}
