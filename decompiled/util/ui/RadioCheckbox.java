package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class RadioCheckbox extends Checkbox {
   static final Color c = Colours.grey;

   public RadioCheckbox(boolean initial) {
      super(initial, 10);
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(c);
      batch.draw(Images.radio, this.getX(), this.getY());
      if (this.on) {
         float onW = 4.0F;
         batch.setColor(c);
         Draw.fillEllipse(batch, this.getX() + this.getWidth() / 2.0F, this.getY() + this.getHeight() / 2.0F, onW, onW);
      }
   }
}
