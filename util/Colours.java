package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.tann.dice.util.image.Img64;
import java.util.Random;

public class Colours {
   public static Color dark;
   public static Color light;
   public static Color yellow;
   public static Color orange;
   public static Color red;
   public static Color blue;
   public static Color grey;
   public static Color purple;
   public static Color green;
   public static Color pink;
   public static final Color text = make(158, 168, 166);
   public static final Color brown = make(137, 92, 67);
   public static final Color NEON_RED = make(253, 77, 79);
   public static final Color BLURPLE = make(88, 101, 242);
   public static final Color SHIFTER = new Color();
   public static Color[] palette;
   public static Color AS_BORDER = text;
   public static final Color z_white = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   public static final Color z_black = new Color(0.0F, 0.0F, 0.0F, 1.0F);
   public static final Color transparent = new Color(0.0F, 0.0F, 0.0F, 0.0F);
   private static Pixmap p;
   private static Color alphaCol = new Color();
   private static Color shiftedCol = new Color();
   private static Color colCache = new Color();
   private static Random randomCache = new Random();

   public static void init() {
      Texture t = new Texture(Gdx.files.internal("misc/palette.png"));
      p = Draw.getPixmap(t);
      dark = palette(0, 0);
      light = palette(1, 0);
      yellow = palette(2, 0);
      orange = palette(3, 0);
      red = palette(4, 0);
      blue = palette(5, 0);
      grey = palette(6, 0);
      purple = palette(7, 0);
      green = palette(8, 0);
      pink = palette(9, 0);
      palette = new Color[]{dark, light, yellow, orange, red, blue, grey, purple, green, text, pink};
   }

   public static Color palette(int x, int y) {
      return new Color(p.getPixel(x, y));
   }

   public static Color withAlpha(Color c, float alpha) {
      return alphaCol.set(c.r, c.g, c.b, alpha);
   }

   public static Color shiftedTowards(Color source, Color target, float amount) {
      if (amount > 1.0F) {
         amount = 1.0F;
      }

      if (amount < 0.0F) {
         amount = 0.0F;
      }

      float r = source.r + (target.r - source.r) * amount;
      float g = source.g + (target.g - source.g) * amount;
      float b = source.b + (target.b - source.b) * amount;
      float a = source.a + (target.a - source.a) * amount;
      return shiftedCol.set(r, g, b, a);
   }

   public static Color shiftedTowards(Color[] cols, float amount) {
      if (cols.length == 0) {
         throw new RuntimeException("null colour");
      } else if (!(amount >= 1.0F) && cols.length != 1) {
         int numCols = cols.length;
         int lowerIndex = (int)(amount * (numCols - 1));
         int upperIndex = lowerIndex + 1;
         float ratio = amount * (numCols - 1) % 1.0F;
         return shiftedTowards(cols[lowerIndex], cols[upperIndex], ratio);
      } else {
         return cols[cols.length - 1];
      }
   }

   public static Color multiply(Color source, Color target) {
      return new Color(source.r * target.r, source.g * target.g, source.b * target.b, 1.0F);
   }

   public static Color make(int r, int g, int b) {
      return new Color(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
   }

   public static Color monochrome(Color c) {
      float brightness = (c.r + c.g + c.b) / 3.0F;
      return new Color(brightness, brightness, brightness, c.a);
   }

   public static boolean equals(Color a, Color b) {
      return a.a == b.a && a.r == b.r && a.g == b.g && a.b == b.b;
   }

   public static boolean wigglyEquals(Color a, Color aa) {
      float r = Math.abs(a.r - aa.r);
      float g = Math.abs(a.g - aa.g);
      float b = Math.abs(a.b - aa.b);
      float wiggle = 0.01F;
      return r < wiggle && g < wiggle && b < wiggle;
   }

   public static void setBatchColour(Batch batch, Color c, float a) {
      batch.setColor(c.r, c.g, c.b, a);
   }

   public static Vector3 v3(Color col) {
      return new Vector3(col.r, col.g, col.b);
   }

   public static Color random() {
      return colCache.set((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0F);
   }

   public static Color randomHashed(int hash) {
      randomCache.setSeed(hash);

      for (int i = 0; i < 3; i++) {
         randomCache.nextInt();
      }

      return colCache.set(randomCache.nextFloat(), randomCache.nextFloat(), randomCache.nextFloat(), 1.0F);
   }

   public static Color secretCol(int hash) {
      Color hashCol = randomHashed(hash);
      return shiftedTowards(hashCol, new Color(0.0F, 0.0F, 0.0F, 1.0F), 0.5F).cpy();
   }

   public static Color from64(String str) {
      if (str != null && str.length() == 3) {
         int r = Img64.index(str.charAt(0));
         int g = Img64.index(str.charAt(1));
         int b = Img64.index(str.charAt(2));
         return new Color(r / 64.0F, g / 64.0F, b / 64.0F, 1.0F);
      } else {
         return pink;
      }
   }

   public static Color fromHex(String str) {
      if (str == null) {
         return pink;
      } else {
         if (str.length() == 3) {
            String r = "" + str.charAt(0);
            String g = "" + str.charAt(1);
            String b = "" + str.charAt(2);
            str = r + '0' + g + '0' + b + '0';
         }

         return str.length() != 6 ? pink : Color.valueOf(str);
      }
   }

   public static String toHex(Color col) {
      String colStr = col.toString();
      return colStr.charAt(0) + "" + colStr.charAt(2) + "" + colStr.charAt(4);
   }
}
