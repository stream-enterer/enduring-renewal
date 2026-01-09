package com.tann.dice.util.image;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TextureCache;

public class ImageFilter {
   private static TextureCache cache = new TextureCache();

   public static void clearCaches() {
      cache = new TextureCache();
   }

   private static void border(Pixmap p, Color col) {
      p.setColor(col);

      for (int x = 0; x < p.getWidth(); x++) {
         for (int y = 0; y < p.getHeight(); y++) {
            int main = p.getPixel(x, y);
            if (main == 0 && hasSurroundingRealPixels(p, x, y, col)) {
               p.drawPixel(x, y);
            }
         }
      }
   }

   private static boolean hasSurroundingRealPixels(Pixmap p, int x, int y, Color color) {
      boolean diags = false;
      int colAsInt = Color.rgba8888(color.r, color.g, color.b, color.a);

      for (int dx = -1; dx <= 1; dx++) {
         if (x + dx >= 0 && x + dx < p.getWidth()) {
            for (int dy = -1; dy <= 1; dy++) {
               if (Math.abs(dx) + Math.abs(dy) == 1 && y + dy >= 0 && y + dy < p.getHeight()) {
                  int other = p.getPixel(x + dx, y + dy);
                  if (other != colAsInt && other != 0) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static Texture paletteSwap(TextureRegion textureRegion, Color fromColour, Color toColour, float threshold, String keytag) {
      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap pixmap = getPixmap(textureRegion);

         for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
               int col = pixmap.getPixel(x, y);
               if (col != 0) {
                  Color srcCol = new Color(col);
                  float colDist = getColDist(fromColour, srcCol);
                  if (!(colDist > threshold)) {
                     pixmap.setColor(Colours.shiftedTowards(toColour, srcCol, colDist / threshold).cpy());
                     pixmap.drawPixel(x, y);
                  }
               }
            }
         }

         return cacheReturn(pixmap, keytag);
      }
   }

   public static Texture hslDelta(TextureRegion textureRegion, int hueDelta, int satDelta, int lightnessDelta, String keytag) {
      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap pixmap = getPixmap(textureRegion);
         float[] hsv = new float[3];

         for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
               int col = pixmap.getPixel(x, y);
               if (col != 0) {
                  Color srcCol = new Color(col);
                  srcCol.toHsv(hsv);
                  srcCol.fromHsv(
                     (hsv[0] + hueDelta * 3.6F + 360.0F) % 360.0F,
                     Math.min(1.0F, Math.max(0.0F, hsv[1] + satDelta / 100.0F)),
                     Math.min(1.0F, Math.max(0.0F, hsv[2] + lightnessDelta / 100.0F))
                  );
                  pixmap.setColor(srcCol);
                  pixmap.drawPixel(x, y);
               }
            }
         }

         return cacheReturn(pixmap, keytag);
      }
   }

   public static Texture hslDeltaPicked(
      TextureRegion textureRegion, Color pickedCol, float threshold, int hueDelta, int satDelta, int lightnessDelta, String keytag
   ) {
      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap pixmap = getPixmap(textureRegion);
         boolean shiftBySimilarity = false;
         Color tmp = new Color();
         float[] hsv = new float[3];

         for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
               int col = pixmap.getPixel(x, y);
               if (col != 0) {
                  Color srcCol = new Color(col);
                  float colDist = getColDist(pickedCol, srcCol);
                  if (!(colDist > threshold)) {
                     if (shiftBySimilarity) {
                        tmp.toHsv(hsv);
                        Color other = new Color();
                        other.fromHsv(
                           (hsv[0] + hueDelta * 3.6F + 360.0F) % 360.0F,
                           Math.min(1.0F, Math.max(0.0F, hsv[1] + satDelta / 100.0F)),
                           Math.min(1.0F, Math.max(0.0F, hsv[2] + lightnessDelta / 100.0F))
                        );
                        float shiftAmount = 1.0F - colDist / threshold;
                        tmp = Colours.shiftedTowards(tmp, other, shiftAmount).cpy();
                        pixmap.setColor(tmp);
                     } else {
                        tmp.toHsv(hsv);
                        tmp.fromHsv(
                           (hsv[0] + hueDelta * 3.6F + 360.0F) % 360.0F,
                           Math.min(1.0F, Math.max(0.0F, hsv[1] + satDelta / 100.0F)),
                           Math.min(1.0F, Math.max(0.0F, hsv[2] + lightnessDelta / 100.0F))
                        );
                        pixmap.setColor(tmp);
                     }

                     srcCol.toHsv(hsv);
                     srcCol.fromHsv(
                        (hsv[0] + hueDelta * 3.6F + 360.0F) % 360.0F,
                        Math.min(1.0F, Math.max(0.0F, hsv[1] + satDelta / 100.0F)),
                        Math.min(1.0F, Math.max(0.0F, hsv[2] + lightnessDelta / 100.0F))
                     );
                     pixmap.setColor(srcCol);
                     pixmap.drawPixel(x, y);
                  }
               }
            }
         }

         return cacheReturn(pixmap, keytag);
      }
   }

   private static float getColDist(Color a, Color b) {
      float rd = a.r - b.r;
      float gd = a.g - b.g;
      float bd = a.b - b.b;
      return (float)(Math.sqrt(rd * rd + gd * gd + bd * bd) / Math.sqrt(3.0));
   }

   public static Texture stamp(TextureRegion textureRegion, TextureRegion decal, int x, int y, String keytag) {
      if (keytag == null) {
         keytag = textureRegion.hashCode() + ":" + decal.hashCode() + ":" + x + ":" + y;
      }

      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap pixmap = getPixmap(textureRegion);
         TextureData decalData = decal.getTexture().getTextureData();
         if (!decalData.isPrepared()) {
            decalData.prepare();
         }

         pixmap.drawPixmap(decalData.consumePixmap(), x, y, decal.getRegionX(), decal.getRegionY(), decal.getRegionWidth(), decal.getRegionHeight());
         return cacheReturn(pixmap, keytag);
      }
   }

   public static Texture stampArrowOnItem(TextureRegion textureRegion, TextureRegion decal) {
      return stamp(textureRegion, decal, 1, 1, null);
   }

   public static Texture rect(TextureRegion textureRegion, int x, int y, int w, int h, Color col, String keytag) {
      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap pixmap = getPixmap(textureRegion);
         pixmap.setColor(col);
         pixmap.fillRectangle(x, y, w, h);
         return cacheReturn(pixmap, keytag);
      }
   }

   public static Texture border(TextureRegion textureRegion, Color col, String keytag) {
      if (cache.get(keytag) != null) {
         return cache.get(keytag);
      } else {
         Pixmap p = getPixmap(textureRegion);
         border(p, col);
         return cacheReturn(p, keytag);
      }
   }

   private static Pixmap getPixmap(TextureRegion textureRegion) {
      TextureData originalData = textureRegion.getTexture().getTextureData();
      if (!originalData.isPrepared()) {
         originalData.prepare();
      }

      int ow = textureRegion.getRegionWidth();
      int oh = textureRegion.getRegionHeight();
      Pixmap pixmap = new Pixmap(ow, oh, originalData.getFormat());
      pixmap.drawPixmap(originalData.consumePixmap(), 0, 0, textureRegion.getRegionX(), textureRegion.getRegionY(), ow, oh);
      return pixmap;
   }

   private static Texture cacheReturn(Pixmap p, String key) {
      TannLog.log("Creating texture: " + key);
      Texture result = new Texture(p);
      cache.put(key, result);
      return result;
   }

   private static Texture cacheReturn(Texture t, String key) {
      cache.put(key, t);
      return t;
   }

   public static Texture makeFrom64RLE(String key) {
      return cache.get(key) != null ? cache.get(key) : cacheReturn(Img64.fromStringCached(key), key);
   }
}
