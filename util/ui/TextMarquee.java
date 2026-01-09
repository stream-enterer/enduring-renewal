package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;

public class TextMarquee extends Actor {
   final String text;
   final Color col;
   final Color bg = Colours.dark;
   final int width;
   final int gap = 4;
   static final TannFont font = TannFont.font;
   float speed = 22.0F;
   float time = 0.0F;

   private TextMarquee(String text, Color col, int width) {
      this.text = "    -   " + text;
      this.col = col;
      this.width = width;
      this.setSize(width, font.getLineHeight() - 1);
   }

   public static Actor marqueeOrDots(String text, Color col, int maxWidth) {
      TextWriter tw = new TextWriter("[notranslate]" + TextWriter.getTag(col) + text);
      if (tw.getWidth() <= maxWidth) {
         return tw;
      } else if (!OptionLib.DISABLE_MARQUEE.c() && !OptionUtils.disableMarqueeFromCR() && FontWrapper.getFont().isTannFont()) {
         return new TextMarquee(TextWriter.stripTags(text), col, maxWidth);
      } else {
         int MAX_DOTTED = 200;
         if (text.length() > 200) {
            text = text.substring(0, 200);
         }

         for (int i = text.length() - 2; i > 4; i--) {
            String sub = text.substring(0, i) + "...";
            if (!Tann.unequalSquareBRackets(sub)) {
               TextWriter dotted = new TextWriter("[notranslate]" + TextWriter.getTag(col) + sub);
               if (dotted.getWidth() <= maxWidth) {
                  return dotted;
               }
            }
         }

         return new TextWriter("[notranslate][red]err");
      }
   }

   public void act(float delta) {
      this.time = this.time + delta * this.speed;
      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      com.tann.dice.Main.requestRendering();
      TannFont font = TannFont.font;
      int totalWidth = font.getWidth(this.text);
      int offset = 4 - (int)(this.time % totalWidth + totalWidth) % totalWidth - 2;
      batch.setColor(this.col);

      for (boolean secondRun : Tann.BOTH) {
         int drawX = offset + (secondRun ? totalWidth : 0);

         for (int i = 0; i < this.text.length(); i++) {
            String part = this.text.charAt(i) + "";
            int pw = font.getWidth(part);
            if (drawX + pw > this.getWidth()) {
               break;
            }

            if (drawX > 0) {
               font.drawString(batch, part, this.getX() + drawX, this.getY());
            }

            drawX += pw + 1;
         }
      }

      batch.setColor(this.bg);
      Draw.fillRectangle(batch, this.getX(), this.getY(), 4.0F, this.getHeight());
      Draw.fillRectangle(batch, this.getX() + this.getWidth() - 4.0F, this.getY(), 4.0F, this.getHeight());
      super.draw(batch, parentAlpha);
   }
}
