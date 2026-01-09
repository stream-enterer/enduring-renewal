package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.Colours;

public class BorderText extends Group {
   public BorderText(String text) {
      this(text, Colours.dark);
   }

   public BorderText(String text, Color outer) {
      this(text, outer, 1);
   }

   public BorderText(String text, Color outer, int border) {
      this.setTransform(false);
      String outerString = TextWriter.getTag(outer) + TextWriter.stripTags(text);
      TextWriter ex = TextWriter.withTannFontOverride(text);
      this.setSize(ex.getWidth() + border * 2, ex.getHeight() + border * 2);
      int size = 1 + border * 2;

      for (int x = 0; x < size; x++) {
         for (int y = 0; y < size; y++) {
            int dx = Math.abs(border - x);
            int dy = Math.abs(border - y);
            float dist = (float)Math.sqrt(dx * dx + dy * dy);
            if (!(dist > border)) {
               TextWriter tw = TextWriter.withTannFontOverride(outerString);
               this.addActor(tw);
               tw.setPosition(x, y);
            }
         }
      }

      this.addActor(ex);
      ex.setPosition(border, border);
   }
}
