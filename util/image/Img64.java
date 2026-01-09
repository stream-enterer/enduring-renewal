package com.tann.dice.util.image;

import com.badlogic.gdx.graphics.Texture;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import java.util.HashMap;
import java.util.Map;

public abstract class Img64 {
   public static final String MAGPIE = "322c1g2jgc2l4000lpt=XK8tCbkeg3gdg2w1iag8g8hw4i9gz2gahwNxcagQwc9gPAg8PBh8gMhBg8h0hAh8jAh8g2gzh9g2hwiaj0ibg0hcc9j3j7j5j6g0gf0g0gf1";
   public static int imgCounter;
   public static final String FORMAT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=";
   public static final String FORMAT_REGEX = "[a-zA-Z0-9%=]*";
   private static Map<String, Texture> imgCache = new HashMap<>();

   public static Texture fromStringCached(String toParse) {
      if (OptionLib.DISABLE_IMG_CACHE.c()) {
         return fromStringInternal(toParse);
      } else {
         if (imgCache.get(toParse) == null) {
            Texture t = fromStringInternal(toParse);
            imgCache.put(toParse, t);
         }

         return imgCache.get(toParse);
      }
   }

   public static void clearCache() {
      imgCache = new HashMap<>();
   }

   private static Texture fromStringInternal(String toParse) {
      try {
         imgCounter++;
         if (toParse != null && !toParse.isEmpty()) {
            char first = toParse.charAt(0);
            String rest = toParse.substring(1);
            switch (first) {
               case '1':
                  return Img64V1.fromString(rest);
               case '2':
                  return Img64V2.fromString(rest);
               case '3':
                  return Img64V3.fromString(rest);
               default:
                  imgCounter--;
                  return null;
            }
         } else {
            return null;
         }
      } catch (Exception var3) {
         imgCounter--;
         var3.printStackTrace();
         return null;
      }
   }

   public static int index(char c) {
      return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(c);
   }
}
