package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ImageButton extends Actor {
   TextureRegion image;

   public ImageButton(TextureRegion image, int gap) {
      this.image = image;
      this.setSize(image.getRegionWidth() + gap * 2, image.getRegionWidth() + gap * 2);
   }

   public void draw(Batch batch, float parentAlpha) {
      int BORDER = 1;
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, BORDER);
      batch.setColor(Colours.z_white);
      batch.draw(
         this.image,
         this.getX() + (int)(this.getWidth() / 2.0F - this.image.getRegionWidth() / 2),
         this.getY() + (int)(this.getHeight() / 2.0F - this.image.getRegionHeight() / 2)
      );
      super.draw(batch, parentAlpha);
   }
}
