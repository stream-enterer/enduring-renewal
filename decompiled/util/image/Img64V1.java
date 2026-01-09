package com.tann.dice.util.image;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;

public class Img64V1 {
   static final int maxWidth = 60;
   static final int maxHeight = 60;
   static final int maxPix = 3600;
   static final int totalBits = 6;

   public static Texture fromString(String toParse) {
      int width = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(toParse.charAt(0));
      int numCols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(toParse.charAt(1));
      int colEnd = 2 + numCols * 3;
      String cols = toParse.substring(2, colEnd);
      String dataString = toParse.substring(colEnd);
      if (width > 60) {
         throw new RuntimeException("Dimensions too great");
      } else {
         Color[] palette = new Color[numCols];
         int paletteBits = (int)Math.ceil(Math.log(palette.length) / Math.log(2.0));

         for (int i = 0; i < numCols; i++) {
            String c = cols.substring(i * 3, (i + 1) * 3);
            if (c.equalsIgnoreCase("alp")) {
               palette[i] = Colours.transparent;
            } else {
               palette[i] = Colours.fromHex(c);
            }
         }

         List<Integer> extraBits = makeExtraBits(paletteBits, palette.length);
         int totalPix = 0;

         for (int ix = 0; ix < dataString.length(); ix++) {
            char c = dataString.charAt(ix);
            if ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(c) == -1) {
               throw new RuntimeException("Invalid char: " + c);
            }

            int paletteIndex = paletteIndex(c, paletteBits, extraBits, palette.length);
            if (paletteIndex < 0 || paletteIndex >= palette.length) {
               throw new RuntimeException("Invalid palette index: " + c);
            }

            totalPix += numPix(c, paletteBits, palette.length, extraBits);
            if (totalPix >= 3600) {
               return null;
            }
         }

         int height = (int)Math.ceil((float)totalPix / width);
         return height > 60 ? null : createTexture(width, height, palette, dataString, paletteBits);
      }
   }

   public static Texture createTexture(int width, int height, Color[] palette, String dataString, int paletteBits) {
      Pixmap p = new Pixmap(width, height, Format.RGBA8888);
      List<Integer> extraBits = makeExtraBits(paletteBits, palette.length);
      int pixelsLeft = 0;
      int x = 0;
      int y = 0;

      for (int dataIndex = 0; dataIndex < dataString.length(); dataIndex++) {
         char dataVal = dataString.charAt(dataIndex);
         int numPix = numPix(dataVal, paletteBits, palette.length, extraBits);
         int paletteIndex = paletteIndex(dataVal, paletteBits, extraBits, palette.length);
         if (paletteIndex < 0 || paletteIndex > palette.length) {
            TannLog.error("invalid palette index");
            return null;
         }

         Color c = palette[paletteIndex];
         p.setColor(c);
         pixelsLeft += numPix;

         while (pixelsLeft > 0) {
            int recWidth = Math.min(pixelsLeft, width - x);
            p.fillRectangle(x, y, recWidth, 1);
            x += recWidth;
            if (x > width) {
               TannLog.error("x>width");
               return null;
            }

            if (x == width) {
               y++;
               x = 0;
            }

            pixelsLeft -= recWidth;
         }
      }

      return new Texture(p);
   }

   static List<Integer> makeExtraBits(int paletteBits, int paletteLength) {
      List<Integer> extraBits = new ArrayList<>();
      int epsc = (int)(Math.pow(2.0, paletteBits) - paletteLength);

      while (epsc > 0) {
         int half = Math.round(epsc / 2.0F);
         extraBits.add(half);
         epsc -= half;
      }

      return extraBits;
   }

   static int paletteIndex(char c, int bitsForPalette, List<Integer> extraBits, int paletteSize) {
      int shift = 6 - bitsForPalette;
      int palMask = (1 << bitsForPalette) - 1 << 6 - bitsForPalette;
      int fIndex = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(c);
      int pIndex = (fIndex & palMask) >> shift;
      if (pIndex < paletteSize) {
         return pIndex;
      } else {
         int extra = pIndex - paletteSize;

         for (int i = 0; i < extraBits.size(); i++) {
            extra -= extraBits.get(i);
            if (extra < 0) {
               return i;
            }
         }

         return -1;
      }
   }

   static int numPix(char c, int bitsForPalette, int paletteSize, List<Integer> extraBits) {
      int baseMax = (int)Math.pow(2.0, 6 - bitsForPalette);
      int posMask = (int)Math.pow(2.0, 6 - bitsForPalette) - 1;
      int val = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(c) & posMask;
      int smallPart = val + 1;
      int largePart = 0;
      int shift = 6 - bitsForPalette;
      int palMask = (1 << bitsForPalette) - 1 << 6 - bitsForPalette;
      int fIndex = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".indexOf(c);
      int pIndex = (fIndex & palMask) >> shift;
      int extra = pIndex - paletteSize;
      if (extra >= 0) {
         for (int i = 0; i < extraBits.size(); i++) {
            extra -= extraBits.get(i);
            if (extra < 0) {
               largePart = (extra + extraBits.get(i) + 1) * baseMax;
               break;
            }
         }
      }

      return smallPart + largePart;
   }
}
