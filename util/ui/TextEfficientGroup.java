package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.util.FontWrapper;

public class TextEfficientGroup extends Group {
   public TextEfficientGroup() {
      this.setTransform(false);
   }

   public void draw(Batch batch, float parentAlpha, boolean efficientDraw) {
      if (efficientDraw) {
         TextBox.SkipDraw++;
         super.draw(batch, parentAlpha);
         TextBox.SkipDraw--;
         if (TextBox.SkipDraw == 0 && FontWrapper.getFont().isHDFont() && TextBox.drawHDChildren(batch, this, 0.0F, 0.0F, true)) {
            TextBox.postDraw();
            com.tann.dice.Main.self().start2d(true);
         }
      } else {
         int old = TextBox.SkipDraw;
         TextBox.SkipDraw = 0;
         super.draw(batch, parentAlpha);
         TextBox.SkipDraw = old;
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      this.draw(batch, parentAlpha, true);
   }
}
