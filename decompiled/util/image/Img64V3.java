package com.tann.dice.util.image;

import com.badlogic.gdx.graphics.Texture;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class Img64V3 {
   public static Texture fromString(String toParse) {
      int numTags = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(toParse.charAt(0));
      toParse = toParse.substring(1);
      if (numTags == -1) {
         return null;
      } else {
         List<TP<Character, String>> tags = new ArrayList<>();

         for (int i = 0; i < numTags; i++) {
            char first = toParse.charAt(0);
            int index = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(first);
            if (index == -1) {
               return null;
            }

            char toRep = toParse.charAt(1);
            String rep = toParse.substring(2, 2 + index);
            tags.add(new TP<>(toRep, rep));
            toParse = toParse.substring(2 + index);
         }

         for (int i = tags.size() - 1; i >= 0; i--) {
            if (toParse.length() >= 2000000) {
               TannLog.error("Reached limit parsing texture");
               break;
            }

            toParse = toParse.replace("" + tags.get(i).a, (CharSequence)tags.get(i).b);
         }

         return Img64.fromStringCached(toParse);
      }
   }
}
