package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;

public class BorderImage extends Group {
   public BorderImage(TextureRegion img, Color... cols) {
      this.setTransform(false);
      ImageActor ia = new ImageActor(img, cols[0]);
      int border = cols.length - 1;
      this.setSize(ia.getWidth() + border * 2, ia.getHeight() + border * 2);

      for (int colIndex = 0; colIndex < cols.length - 1; colIndex++) {
         int size = 1 + border * 1 * (cols.length - colIndex);
         Color col = cols[colIndex];

         for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
               int dx = Math.abs(border - x);
               int dy = Math.abs(border - y);
               float dist = (float)Math.sqrt(dx * dx + dy * dy);
               if (!(dist >= size)) {
                  ImageActor tw = new ImageActor(img, col);
                  this.addActor(tw);
                  tw.setPosition(x + colIndex, y + colIndex);
               }
            }
         }
      }

      ImageActor tw = new ImageActor(img, cols[cols.length - 1]);
      this.addActor(tw);
      Tann.center(tw);
   }
}
