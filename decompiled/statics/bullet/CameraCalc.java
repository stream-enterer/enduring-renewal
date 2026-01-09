package com.tann.dice.statics.bullet;

import com.badlogic.gdx.Gdx;

public class CameraCalc {
   public static float calcDistFromEstimatedBounds(float w, float h) {
      float res = twoNineAlphaAlgo(w, h);
      return makeExtremeRatioAdjustment(res, w, h);
   }

   private static float makeExtremeRatioAdjustment(float res, float w, float h) {
      if (com.tann.dice.Main.isPortrait()) {
         return res;
      } else {
         float estHeight = h * Gdx.graphics.getHeight();
         float estWidth = w * Gdx.graphics.getWidth() - 0.0F;
         float dr = Math.min(9.0F, estWidth / estHeight - 1.5F) * 0.6F;
         if (dr > 0.0F) {
            System.out.println("aab" + dr);
            return res + dr;
         } else {
            return res;
         }
      }
   }

   private static float twoNineAlphaAlgo(float w, float h) {
      float estHeight = h * Gdx.graphics.getHeight();
      float estWidth = w * Gdx.graphics.getWidth() - 0.0F;
      float cancelNaturalHeightScale = estHeight * 0.015F;
      float calc = 1.0F / (estWidth * estHeight);
      float pow = 0.5F;
      float mult = 300.0F;
      float res = (float)(Math.pow(calc, pow) * mult) * cancelNaturalHeightScale;
      return res * (com.tann.dice.Main.isPortrait() ? 1.6F : 1.0F);
   }
}
