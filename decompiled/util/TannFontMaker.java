package com.tann.dice.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Arrays;
import java.util.HashMap;

public class TannFontMaker {
   public static TannFont fromTextureRegion(TextureRegion font) {
      int[] heights = new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 6};
      TannFont result = new TannFont(heights[0], heights[0] + 1);
      boolean[] boldConfig = new boolean[]{false, false, false, false, false, true, true, true, true, true, false, false, false, true, false, false};
      boolean[] italicsConfig = new boolean[]{false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, false};
      String[] chars = new String[]{
         "abcdefghijklmnopqrstuvwxyz",
         "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
         "0123456789.,!?:()[]{}\"+-/_%='@<>∞&|;*#$£€¥₩^~",
         "абвгдежзийклмнопрстуфхцчшщъыьэюя",
         "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ",
         "abcdefghijklmnopqrstuvwxyz",
         "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
         "0123456789.,!?:()\"+-/",
         "абвгдежзийклмнопрстуфхцчшщъыьэюя",
         "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ",
         "abcdefghijklmnopqrstuvwxyz",
         "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
         "éàèùçâêîôûëïüñáíóú¡¿ìòœãõłø",
         "éàèùçâêîôûëïüñáíóú¡¿ìòœãõ",
         "éàèùçâêîôûëïüñáíóú¡¿ìòœãõ",
         "éàèùçâêîôûëïüñáíóú¡¿ìòœãõ".toUpperCase()
      };
      if (heights.length != chars.length) {
         throw new RuntimeException("Invalid font data");
      } else {
         Pixmap p = Draw.getPixmap(font);
         int x = 0;
         int y = 0;

         for (int row = 0; row < heights.length; row++) {
            boolean bold = boldConfig[row];
            boolean italics = italicsConfig[row];
            char[] charArray = chars[row].toCharArray();

            for (int ii = 0; ii < charArray.length; ii++) {
               char c = charArray[ii];
               HashMap<Character, TextureRegion> g;
               if (italics) {
                  g = result.italicsGlyphs;
               } else if (bold) {
                  g = result.boldGlyphs;
               } else {
                  g = result.glyphs;
               }

               int dx = 0;

               while (true) {
                  boolean empty = true;

                  for (int dy = 0; dy < heights[row]; dy++) {
                     int col = p.getPixel(font.getRegionX() + x + dx, font.getRegionY() + y + dy);
                     int alpha = col & 0xFF;
                     if (alpha != 0) {
                        empty = false;
                        break;
                     }
                  }

                  if (empty) {
                     g.put(c, new TextureRegion(font, x, y, dx, heights[row]));
                     x += dx + 1;
                     break;
                  }

                  dx++;
               }
            }

            x = 0;
            y += heights[row] + 1;
         }

         int spaceWidth = 1;
         result.glyphs.put(' ', new TextureRegion(font, font.getRegionWidth() - spaceWidth, 0, spaceWidth, 0));
         result.kerningPairs.put('>', Arrays.asList('='));
         result.kerningPairs.put('e', Arrays.asList('a'));
         result.kerningPairs.put('h', Arrays.asList('t'));
         result.kerningPairs.put('p', Arrays.asList('a'));
         result.kerningPairs.put('r', Arrays.asList('d', 'a'));

         for (char c : "eyadfbuovcwUOCGWQS".toCharArray()) {
            result.italicsShiftRightBefore.put(c, 0);
         }

         for (char c : "qTtVOY".toCharArray()) {
            result.italicsShiftRightBefore.put(c, 1);
         }

         for (char c : "tpkneomahLQVWROM".toCharArray()) {
            result.italicsShiftLeftAfter.put(c, 0);
         }

         for (char c : "swrxcqyuvzgYAPSDB".toCharArray()) {
            result.italicsShiftLeftAfter.put(c, 1);
         }

         for (char c : "lfjidJHUFEGIZNCXTK".toCharArray()) {
            result.italicsShiftLeftAfter.put(c, 1);
         }

         return result;
      }
   }
}
