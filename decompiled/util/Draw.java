package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.ImageUtils;
import java.util.HashMap;
import java.util.Map;

public class Draw {
   public static TextureRegion circle50;
   private static TextureRegion wSq;
   private static Map<String, Texture> arcs = new HashMap<>();
   static float[] triangle;

   public static void setup() {
      circle50 = ImageUtils.loadExt("junk/misc/circle50");
   }

   public static void draw(Batch batch, Texture t, float x, float y) {
      drawRotatedScaled(batch, t, x, y, 1.0F, 1.0F, 0.0F);
   }

   public static void drawScaled(Batch batch, Texture t, float x, float y, float scaleX, float scaleY) {
      drawRotatedScaled(batch, t, x, y, scaleX, scaleY, 0.0F);
   }

   public static void drawRotatedScaled(Batch batch, Texture t, float x, float y, float scaleX, float scaleY, float radianRotation) {
      drawRotatedScaledFlipped(batch, t, x, y, scaleX, scaleY, radianRotation, false, false);
   }

   public static void drawRotatedScaledFlipped(
      Batch batch, Texture t, float x, float y, float scaleX, float scaleY, float radianRotation, boolean xFlip, boolean yFlip
   ) {
      batch.draw(t, x, y, 0.0F, 0.0F, t.getWidth(), t.getHeight(), scaleX, scaleY, rad2deg(radianRotation), 0, 0, t.getWidth(), t.getHeight(), xFlip, yFlip);
   }

   public static void drawCentered(Batch batch, Texture t, float x, float y) {
      drawCenteredRotatedScaled(batch, t, x, y, 1.0F, 1.0F, 0.0F);
   }

   public static void drawCenteredScaled(Batch batch, Texture t, float x, float y, float scaleX, float scaleY) {
      drawCenteredRotatedScaled(batch, t, x, y, scaleX, scaleY, 0.0F);
   }

   public static void drawCenteredRotated(Batch batch, Texture t, float x, float y, float radianRotation) {
      drawCenteredRotatedScaled(batch, t, x, y, 1.0F, 1.0F, radianRotation);
   }

   public static void drawCenteredRotatedScaled(Batch batch, Texture t, float x, float y, float xScale, float yScale, float radianRotation) {
      drawCenteredRotatedScaledFlipped(batch, t, x, y, xScale, yScale, radianRotation, false, false);
   }

   public static void drawCenteredRotatedScaledFlipped(
      Batch batch, Texture t, float x, float y, float xScale, float yScale, float radianRotation, boolean xFlip, boolean yFlip
   ) {
      batch.draw(
         t,
         x - t.getWidth() / 2,
         y - t.getHeight() / 2,
         t.getWidth() / 2.0F,
         t.getHeight() / 2.0F,
         t.getWidth(),
         t.getHeight(),
         xScale,
         yScale,
         rad2deg(radianRotation),
         0,
         0,
         t.getWidth(),
         t.getHeight(),
         xFlip,
         yFlip
      );
   }

   public static void draw(Batch batch, TextureRegion t, float x, float y) {
      drawRotatedScaled(batch, t, x, y, 1.0F, 1.0F, 0.0F);
   }

   public static void drawScaled(Batch batch, TextureRegion t, float x, float y, float scaleX, float scaleY) {
      drawRotatedScaled(batch, t, x, y, scaleX, scaleY, 0.0F);
   }

   public static void drawRotatedScaled(Batch batch, TextureRegion t, float x, float y, float scaleX, float scaleY, float radianRotation) {
      batch.draw(t, x, y, 0.0F, 0.0F, t.getRegionWidth(), t.getRegionHeight(), scaleX, scaleY, rad2deg(radianRotation));
   }

   public static void drawCentered(Batch batch, TextureRegion t, float x, float y) {
      drawCenteredRotatedScaled(batch, t, x, y, 1.0F, 1.0F, 0.0F);
   }

   public static void drawCenteredScaled(Batch batch, TextureRegion t, float x, float y, float scaleX, float scaleY) {
      drawCenteredRotatedScaled(batch, t, x, y, scaleX, scaleY, 0.0F);
   }

   public static void drawCenteredRotated(Batch batch, TextureRegion t, float x, float y, float radianRotation) {
      drawCenteredRotatedScaled(batch, t, x, y, 1.0F, 1.0F, radianRotation);
   }

   public static void drawCenteredRotatedScaled(Batch batch, TextureRegion t, float x, float y, float xScale, float yScale, float radianRotation) {
      drawCenteredRotatedScaledFlipped(batch, t, x, y, xScale, yScale, radianRotation);
   }

   public static void drawCenteredRotatedScaledFlipped(Batch batch, TextureRegion t, float x, float y, float scaleX, float scaleY, float radianRotation) {
      batch.draw(
         t,
         (int)(x - t.getRegionWidth() / 2.0F),
         (int)(y - t.getRegionHeight() / 2.0F),
         t.getRegionWidth() / 2.0F,
         t.getRegionHeight() / 2.0F,
         t.getRegionWidth(),
         t.getRegionHeight(),
         scaleX,
         scaleY,
         rad2deg(radianRotation)
      );
   }

   public static void drawFlipped(Batch batch, TextureRegion t, float x, float y, boolean xFlip, boolean yFlip) {
      batch.draw(
         t,
         xFlip ? x + t.getRegionWidth() : x,
         yFlip ? y + t.getRegionHeight() : y,
         0.0F,
         0.0F,
         t.getRegionWidth(),
         t.getRegionHeight(),
         xFlip ? -1.0F : 1.0F,
         yFlip ? -1.0F : 1.0F,
         0.0F
      );
   }

   public static void drawRectangle(Batch batch, float x, float y, float width, float height, int lineWidth) {
      fillRectangle(batch, x, y, width, lineWidth);
      fillRectangle(batch, x, y + height - lineWidth, width, lineWidth);
      fillRectangle(batch, x, y, lineWidth, height);
      fillRectangle(batch, x + width - lineWidth, y, lineWidth, height);
   }

   public static void drawRectangle(Batch batch, float x, float y, float width, float height) {
      drawRectangle(batch, x, y, width, height, 1);
   }

   public static void fillRectangle(Batch batch, float x, float y, float width, float height) {
      drawScaled(batch, getSq(), x, y, width, height);
   }

   public static void fillRectangle(Batch batch, float x, float y, float width, float height, Color inside, Color edge, int border) {
      batch.setColor(edge);
      fillRectangle(batch, x, y, width, height);
      batch.setColor(inside);
      fillRectangle(batch, x + border, y + border, width - border * 2, height - border * 2);
   }

   public static void fillEllipse(Batch batch, float x, float y, float width, float height) {
      drawScaled(batch, circle50, x - width / 2.0F, y - height / 2.0F, width / 50.0F, height / 50.0F);
   }

   public static void drawLine(Batch batch, Vector2 start, Vector2 end, int width) {
      drawLine(batch, start.x, start.y, end.x, end.y, width);
   }

   public static void drawLine(Batch batch, float x, float y, float tX, float tY, float width) {
      float dist = (float)Math.sqrt((tX - x) * (tX - x) + (tY - y) * (tY - y)) + 0.01F;
      float radians = (float)Math.atan2(tY - y, tX - x);
      batch.draw(getSq(), x + 0.5F, y, 0.0F, 0.5F, 1.0F, 1.0F, dist, width, (float)Math.toDegrees(radians));
   }

   public static void drawDottedLine(Batch batch, float x, float y, float tX, float tY, float width, float segmentSize, float gapSize, float speed) {
      float dist = (float)Math.sqrt((tX - x) * (tX - x) + (tY - y) * (tY - y)) + 0.01F;
      float radians = (float)Math.atan2(tY - y, tX - x);
      float position = com.tann.dice.Main.secs * speed * (gapSize + segmentSize);
      position %= segmentSize + gapSize;

      for (float var16 = position - (segmentSize + gapSize); var16 < dist; var16 += gapSize + segmentSize) {
         float drawX = (float)(x + Math.cos(radians) * Math.max(0.0F, var16));
         float drawY = (float)(y + Math.sin(radians) * Math.max(0.0F, var16));
         float segmentWidth = Math.min(var16 + segmentSize, dist) - var16;
         if (var16 > -segmentSize) {
            if (var16 < 0.0F) {
               segmentWidth += var16;
            }

            batch.draw(getSq(), drawX, drawY, 0.0F, 0.5F, 1.0F, 1.0F, segmentWidth, width, (float)Math.toDegrees(radians));
         }
      }

      com.tann.dice.Main.requestRendering();
   }

   public static float rad2deg(float rad) {
      return (float)(rad * 180.0F / Math.PI);
   }

   public static TextureRegion getSq() {
      if (wSq == null) {
         wSq = ImageUtils.loadExt("junk/misc/pixel");
      }

      return wSq;
   }

   public static Pixmap getPixmap(Texture t) {
      t.getTextureData().prepare();
      return t.getTextureData().consumePixmap();
   }

   public static Pixmap getPixmap(TextureRegion t) {
      return getPixmap(t.getTexture());
   }

   public static void drawSize(Batch batch, TextureRegion textureRegion, float x, float y, float width, float height) {
      batch.draw(
         textureRegion,
         x,
         y,
         0.0F,
         0.0F,
         textureRegion.getRegionWidth(),
         textureRegion.getRegionHeight(),
         width / textureRegion.getRegionWidth(),
         height / textureRegion.getRegionHeight(),
         0.0F
      );
   }

   public static void drawSizeCentered(Batch batch, TextureRegion textureRegion, float x, float y, float width, float height) {
      drawCenteredScaled(batch, textureRegion, x, y, width / textureRegion.getRegionWidth(), height / textureRegion.getRegionHeight());
   }

   public static void fillActor(Batch batch, Actor a, Color col) {
      batch.setColor(col);
      fillActor(batch, a);
   }

   public static void fillActor(Batch batch, Actor a, Color col, int allOffset) {
      batch.setColor(col);
      fillRectangle(batch, (int)a.getX() + allOffset, (int)a.getY() + allOffset, a.getWidth() - allOffset * 2, a.getHeight() - allOffset * 2);
   }

   public static void fillActor(Batch batch, Actor a) {
      fillRectangle(batch, (int)a.getX(), (int)a.getY(), a.getWidth(), a.getHeight());
   }

   public static void fillActor(Batch batch, Actor a, int border) {
      fillRectangle(batch, (int)a.getX() + border, (int)a.getY() + border, a.getWidth() - border * 2, a.getHeight() - border * 2);
   }

   public static void borderActor(Batch batch, Actor a) {
      drawRectangle(batch, a.getX(), a.getY(), a.getWidth(), a.getHeight(), 1);
   }

   public static void fillActor(Batch batch, Actor a, Color bg, Color border, int borderSize) {
      fillRectangle(batch, a.getX(), a.getY(), a.getWidth(), a.getHeight(), bg, border, borderSize);
   }

   public static void fillActor(Batch batch, Actor a, Color bg, Color border) {
      fillRectangle(batch, a.getX(), a.getY(), a.getWidth(), a.getHeight(), bg, border, 1);
   }

   public static void fillActor(Batch batch, Actor a, Color bg, Color border, int borderSize, boolean pix) {
      if (!pix) {
         fillActor(batch, a, bg, border, borderSize);
      } else {
         fillRectangle(batch, (int)a.getX(), (int)a.getY(), (int)a.getWidth(), (int)a.getHeight(), bg, border, borderSize);
      }
   }

   public static void drawArrow(Batch batch, float x, float y, float x1, float y1, int width) {
      drawLine(batch, x, y, x1, y1, width);
      double angle = Math.atan2(y1 - y, x1 - x);
      int angleDiff = -40;
      int dist = 20;
      double angle1 = angle + angleDiff;
      double angle2 = angle - angleDiff;
      drawLine(batch, x1, y1, x1 + (float)(Math.cos(angle1) * dist), y1 + (float)(Math.sin(angle1) * dist), width);
      drawLine(batch, x1, y1, x1 + (float)(Math.cos(angle2) * dist), y1 + (float)(Math.sin(angle2) * dist), width);
   }

   public static void drawLoadingAnimation(Batch batch, float x, float y, float radius, float size, float speed, int numDots) {
      for (int i = 0; i < numDots; i++) {
         double angle = (Math.PI * 2) / numDots * i;
         angle += com.tann.dice.Main.secs * speed;
         float drawX = (float)(x + Math.sin(angle) * radius);
         float drawY = (float)(y + Math.cos(angle) * radius);
         fillEllipse(batch, drawX - size / 2.0F, drawY - size / 2.0F, size, size);
      }
   }

   public static void fillArc(Batch batch, float x, float y, int radius, float startRadians, float endRadians) {
      String key = radius + ":" + startRadians + ":" + endRadians;
      Texture t = arcs.get(key);
      if (t == null) {
         Pixmap p = new Pixmap(radius * 2, radius * 2, Format.RGBA8888);
         p.setColor(1.0F, 1.0F, 1.0F, 1.0F);

         for (int pX = -radius; pX < radius; pX++) {
            for (int pY = -radius; pY < radius; pY++) {
               float dist = Maths.dist(pX, pY);
               if (!(dist > radius)) {
                  double angle = Math.atan2(pY, pX);
                  if (is_angle_between((float)angle, startRadians + (float) Math.PI, endRadians + (float) Math.PI)) {
                     p.drawPixel(pX + radius, pY + radius);
                  }
               }
            }
         }

         t = new Texture(p);
         arcs.put(key, t);
      }

      batch.draw(t, x - radius, y - radius);
   }

   static boolean is_angle_between(float x, float a, float b) {
      b = modN(b - a);
      x = modN(x - a);
      return b < (float) Math.PI ? x < b : b < x;
   }

   static float modN(float x) {
      float m = x % (float) (Math.PI * 2);
      if (m < 0.0F) {
         m += (float) (Math.PI * 2);
      }

      return m;
   }

   public static void setAlpha(Batch batch, float alpha) {
      batch.setColor(Colours.withAlpha(batch.getColor(), alpha));
   }

   public static void drawTriangle(Batch batch, float x0, float y0, float x1, float y1, float x2, float y2) {
      if (triangle == null) {
         triangle = new float[]{
            0.0F,
            0.0F,
            0.0F,
            getSq().getU(),
            getSq().getV2(),
            0.0F,
            0.0F,
            0.0F,
            getSq().getU(),
            getSq().getV(),
            0.0F,
            0.0F,
            0.0F,
            getSq().getU2(),
            getSq().getV(),
            0.0F,
            0.0F,
            0.0F,
            getSq().getU2(),
            getSq().getV2()
         };
      }

      float colorBits = batch.getPackedColor();
      if (colorBits != triangle[2]) {
         for (int i = 0; i < 4; i++) {
            triangle[i * 5 + 2] = colorBits;
         }
      }

      triangle[0] = x0;
      triangle[1] = y0;
      triangle[5] = x1;
      triangle[6] = y1;
      triangle[10] = x2;
      triangle[11] = y2;
      triangle[15] = x2;
      triangle[16] = y2;
      batch.draw(getSq().getTexture(), triangle, 0, 20);
   }

   public static float getScale(TextureRegion tr, int width, int height) {
      float trWidth = tr.getRegionWidth();
      float trHeight = tr.getRegionHeight();
      return Math.max(width / trWidth, height / trHeight);
   }

   public static float getScale(Texture t, int width, int height) {
      float trWidth = t.getWidth();
      float trHeight = t.getHeight();
      return Math.min(width / trWidth, height / trHeight);
   }

   public static void scaleRegion(Batch batch, Texture tr, int x, int y, int width, int height) {
      float trWidth = tr.getWidth();
      float trHeight = tr.getHeight();
      float scale = getScale(tr, width, height);
      drawScaled(batch, tr, (float)((int)(x + (width - trWidth * scale) / 2.0F)), (float)y, scale, scale);
   }

   public static void scaleRegion(Batch batch, TextureRegion tr, int x, int y, int width, int height) {
      float trWidth = tr.getRegionWidth();
      float trHeight = tr.getRegionHeight();
      float scale = getScale(tr, width, height);
      drawScaled(batch, tr, (float)((int)(x + (width - trWidth * scale) / 2.0F)), (float)y, scale, scale);
   }

   public static void pixel(Batch batch, int x, int y) {
      draw(batch, getSq(), (float)x, (float)y);
   }
}
