package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class Glowverlay extends Actor {
   final Color color;
   private static final float amp = 0.2F;

   public Glowverlay(Color color) {
      this.setTouchable(Touchable.disabled);
      this.color = color;
   }

   public Glowverlay() {
      this(Colours.yellow);
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      Group parent = this.getParent();
      drawGlow(batch, this.getX(), this.getY(), parent.getWidth(), parent.getHeight(), this.color);
   }

   public static void drawGlow(Batch batch, Actor a, Color color) {
      drawGlow(batch, a.getX(), a.getY(), a.getWidth(), a.getHeight(), color);
   }

   public static void drawGlow(Batch batch, float x, float y, float w, float h, Color color) {
      int maxDist = (int)Math.pow((w + h) / 2.0F, 0.35);

      for (int dist = 0; dist < maxDist; dist++) {
         float alpha = (float)(maxDist - dist) / maxDist;
         float bonus = com.tann.dice.Main.pulsateFactor() * 0.2F;
         alpha += bonus;
         batch.setColor(Colours.withAlpha(color, alpha));

         for (int i = 0; i < 4; i++) {
            int startX = i != 3 ? 0 : (int)w - 1;
            int startY = i != 1 ? 0 : (int)h - 1;
            int width = i < 2 ? (int)w : 1;
            int height = i >= 2 ? (int)h : 1;
            switch (i) {
               case 0:
                  startX += dist;
                  width -= dist * 2;
                  startY -= dist;
                  break;
               case 1:
                  startX += dist;
                  width -= dist * 2;
                  startY += dist;
                  break;
               case 2:
                  startY += dist;
                  height -= dist * 2;
                  startX -= dist;
                  break;
               case 3:
                  startY += dist;
                  height -= dist * 2;
                  startX += dist;
            }

            Draw.fillRectangle(batch, startX + x, startY + y, width, height);
         }
      }
   }
}
