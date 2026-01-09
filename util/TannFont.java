package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.statics.ImageUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TannFont {
   public static boolean GLOBAL_GLITCH = false;
   public static TannFont font = TannFontMaker.fromTextureRegion(ImageUtils.loadExt("junk/font/font"));
   final int height;
   final int lineHeight;
   final HashMap<Character, TextureRegion> glyphs = new HashMap<>();
   final HashMap<Character, TextureRegion> boldGlyphs = new HashMap<>();
   final HashMap<Character, TextureRegion> italicsGlyphs = new HashMap<>();
   final Map<Character, List<Character>> kerningPairs = new HashMap<>();
   final Map<Character, Integer> italicsShiftLeftAfter = new HashMap<>();
   final Map<Character, Integer> italicsShiftRightBefore = new HashMap<>();
   public static final int BASE_ITALICS_SHIFT = 1;
   private static final int kerningGap = 1;
   public static float bonusSin = 0.0F;
   private static final float freq = 6.5F;
   private static final float amp = 2.0F;
   private static final float letterAdd = 0.08F;

   public TannFont(int height, int lineHeight) {
      this.height = height;
      this.lineHeight = lineHeight;
   }

   public static int guessMaxTextLength() {
      return com.tann.dice.Main.width / 4;
   }

   public static int guessMaxTextLength(float ratio) {
      return (int)(com.tann.dice.Main.width / 4 * ratio);
   }

   public void drawString(Batch batch, String text, float x, float y) {
      this.drawString(batch, text, x, y, false);
   }

   public void drawString(Batch batch, String text, float x, float y, boolean fixedWidth) {
      this.drawString(batch, text, x, y, fixedWidth, false, false, false, false, false, false);
   }

   public void drawString(
      Batch batch, String text, float x, float y, boolean fixedWidth, boolean wiggle, boolean sin, boolean bold, boolean italics, boolean first, boolean glitch
   ) {
      this.drawString(batch, text, x, y, fixedWidth, wiggle, sin, bold, italics, first, glitch, 1.0F);
   }

   public void drawString(
      Batch batch,
      String text,
      float x,
      float y,
      boolean fixedWidth,
      boolean wiggle,
      boolean sin,
      boolean bold,
      boolean italics,
      boolean first,
      boolean glitch,
      float fontScale
   ) {
      if (sin || wiggle || glitch) {
         com.tann.dice.Main.requestRendering();
      }

      if (text == null) {
         text = "?!?";
      }

      float wiggleNoise = (com.tann.dice.Main.getNoiseFromTicks() + 1.0F) / 2.0F;
      char prevChar = '`';

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (!italics && !bold && this.kerningPairs.get(prevChar) != null && this.kerningPairs.get(prevChar).contains(c)) {
            x -= 1.0F * fontScale;
         }

         prevChar = c;
         TextureRegion t = this.getGlyph(c, bold, italics);
         int plusY = 0;
         if (wiggle && Math.random() * wiggleNoise > 0.5) {
            plusY = (int)(Math.random() * 2.0) - 0;
         }

         if (sin) {
            plusY = (int)(Math.sin((com.tann.dice.Main.secs + bonusSin) * 6.5F) * 2.0);
            bonusSin += 0.08F;
         }

         if (italics && (!first || i > 0) && this.italicsShiftRightBefore.get(c) != null) {
            x += this.italicsShiftRightBefore.get(c).intValue();
         }

         if (!GLOBAL_GLITCH && !glitch) {
            Draw.drawScaled(batch, t, x, y + plusY, fontScale, fontScale);
         } else {
            float scale = 0.53F;
            float freq = 1.5F;
            float rand = 0.0F;
            float noiseFreq = 0.005F * (glitch ? 3 : 1);
            float noiseScale = 0.72F;
            float noiseZSpeed = 0.5F;
            TextureRegion actuallyDraw = this.getGlyph(
               (char)Math.round(
                  text.charAt(i) + 0.72F * Noise.noise((double)(x * noiseFreq), (double)(y * noiseFreq), (double)(com.tann.dice.Main.secs * 0.5F), 2)
               ),
               bold,
               italics
            );
            Draw.drawScaled(
               batch, actuallyDraw, (float)Math.round(x + t.getRegionWidth() / 2.0F - actuallyDraw.getRegionWidth() / 2.0F), y + plusY, fontScale, fontScale
            );
         }

         if (fixedWidth) {
            x += (this.getDefaultWidth() + 1) * fontScale;
         } else {
            x += (t.getRegionWidth() + 1) * fontScale;
         }

         if (italics) {
            x--;
            if (this.italicsShiftLeftAfter.get(c) != null) {
               x -= this.italicsShiftLeftAfter.get(c).intValue();
            }
         }
      }
   }

   public void drawString(Batch batch, String text, float x, float y, int align) {
      if (align == 1) {
         this.drawString(batch, text, x - this.getWidth(text) / 2, y - this.getHeight() / 2, false);
      }
   }

   public int getWidth(String text) {
      return this.getWidth(text, false, false, false);
   }

   public int getWidth(String text, boolean bold, boolean italics, boolean first) {
      if (text == null) {
         return 0;
      } else {
         int total = 0;
         char prevChar = '`';

         for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!italics && !bold && this.kerningPairs.get(prevChar) != null && this.kerningPairs.get(prevChar).contains(c)) {
               total--;
            }

            if (italics) {
               if (this.italicsShiftLeftAfter.get(c) != null) {
                  total -= this.italicsShiftLeftAfter.get(c);
               }

               if ((!first || i > 0) && this.italicsShiftRightBefore.get(c) != null) {
                  total += this.italicsShiftRightBefore.get(c);
               }

               if (i < text.length() - 1) {
                  total--;
               }
            }

            prevChar = c;
            TextureRegion t = this.getGlyph(c, bold, italics);
            total += t.getRegionWidth();
            if (i < text.length() - 1) {
               total++;
            }
         }

         return total;
      }
   }

   private int getDefaultWidth() {
      return 3;
   }

   public int getHeight() {
      return this.height;
   }

   public int getLineHeight() {
      return this.lineHeight;
   }

   public int getSpaceWidth(boolean italics) {
      return italics ? this.getSpaceWidth() - 1 - 0 : this.getSpaceWidth();
   }

   public int getSpaceWidth() {
      return 4;
   }

   public TextureRegion getGlyph(char c) {
      return this.getGlyph(c, false, false);
   }

   private TextureRegion getGlyph(char c, boolean bold, boolean italics) {
      TextureRegion tr = (italics ? this.italicsGlyphs : (bold ? this.boldGlyphs : this.glyphs)).get(c);
      if (tr != null) {
         return tr;
      } else {
         return italics && this.glyphs.get(c) != null ? this.glyphs.get(c) : this.glyphs.get('?');
      }
   }

   public boolean hasChar(char c) {
      return this.glyphs.get(c) != null;
   }
}
