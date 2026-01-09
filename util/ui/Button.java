package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;

public class Button extends Actor {
   static int patchey = 3;
   public static NinePatch ninePatch = new NinePatch(ImageUtils.loadExt("patch/buttonBorder"), patchey, patchey, patchey, patchey);
   public static NinePatch ninePatchLeft = new NinePatch(ImageUtils.loadExt("patch/buttonBorderLeftCut"), patchey, patchey, patchey, patchey);
   public static NinePatch ninePatchRight = new NinePatch(ImageUtils.loadExt("patch/buttonBorderRightCut"), patchey, patchey, patchey, patchey);
   public static NinePatch ninePatchAbility = new NinePatch(ImageUtils.loadExt("patch/abilityBorder"), patchey, patchey, patchey, patchey);
   private TextureRegion region;
   Color bg = Colours.dark;
   Color border = Colours.grey;
   int inputBorder = -1;
   int leftRight;
   Boolean specialBorderRight;

   public Button(float width, float height, TextureRegion region, Runnable runnable) {
      this.region = region;
      this.setRunnable(runnable);
      this.setSize(width, height);
   }

   public Button(float width, float height, TextureRegion region, Runnable runnable, Runnable info) {
      this.region = region;
      this.setRunnable(runnable, info);
      this.setSize(width, height);
   }

   public void setRunnable(final Runnable runnable) {
      if (runnable != null) {
         this.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               if (Button.this.inputBorder != -1) {
                  int dist = 0;
                  if (Button.this.leftRight == 0) {
                     dist = (int)Tann.dist(x, y, 0.0F, 0.0F);
                  } else {
                     dist = (int)Tann.dist(x, y, Button.this.getWidth(), 0.0F);
                  }

                  if (dist < Button.this.inputBorder) {
                     return false;
                  }
               }

               if (button == 0) {
                  runnable.run();
               }

               return true;
            }
         });
      }
   }

   public void setRunnable(final Runnable runnable, final Runnable info) {
      if (runnable != null && info != null) {
         this.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               if (Button.this.inputBorder != -1) {
                  int dist = 0;
                  if (Button.this.leftRight == 0) {
                     dist = (int)Tann.dist(x, y, 0.0F, 0.0F);
                  } else {
                     dist = (int)Tann.dist(x, y, Button.this.getWidth(), 0.0F);
                  }

                  if (dist < Button.this.inputBorder) {
                     return false;
                  }
               }

               if (button == 0) {
                  runnable.run();
               }

               return true;
            }

            @Override
            public boolean info(int button, float x, float y) {
               if (Button.this.inputBorder != -1) {
                  int dist = 0;
                  if (Button.this.leftRight == 0) {
                     dist = (int)Tann.dist(x, y, 0.0F, 0.0F);
                  } else {
                     dist = (int)Tann.dist(x, y, Button.this.getWidth(), 0.0F);
                  }

                  if (dist < Button.this.inputBorder) {
                     return false;
                  }
               }

               info.run();
               return true;
            }
         });
      }
   }

   public void setBorder(Color bg, Color border) {
      this.bg = bg;
      this.border = border;
   }

   public NinePatch getPatch() {
      if (this.specialBorderRight == null) {
         return ninePatch;
      } else {
         return this.specialBorderRight ? ninePatchRight : ninePatchLeft;
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.specialBorderRight != null) {
         batch.setColor(Colours.dark);
         Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY(), this.getWidth() - 2.0F, this.getHeight() - 1.0F);
         if (this.specialBorderRight) {
            Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY(), this.getWidth() - 1.0F, this.getHeight() - 1.0F);
         } else {
            Draw.fillRectangle(batch, this.getX(), this.getY(), this.getWidth() - 2.0F, this.getHeight() - 1.0F);
         }
      }

      Tann.drawPatch(batch, this, this.getPatch(), this.bg, this.border, 1);
      if (this.region != null) {
         batch.setColor(this.getColor());
         batch.draw(
            this.region,
            (int)(this.getX() + this.getWidth() / 2.0F - this.region.getRegionWidth() / 2.0F),
            (int)(this.getY() + this.getHeight() / 2.0F - this.region.getRegionHeight() / 2.0F)
         );
      }

      super.draw(batch, parentAlpha);
   }

   public void setInputBorder(int pixels, int leftRight) {
      this.inputBorder = pixels;
      this.leftRight = leftRight;
   }

   public void setSpecialBorderRight(Boolean specialBorderRight) {
      this.specialBorderRight = specialBorderRight;
   }
}
