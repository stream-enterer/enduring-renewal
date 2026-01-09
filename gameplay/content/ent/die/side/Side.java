package com.tann.dice.gameplay.content.ent.die.side;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public abstract class Side {
   public abstract TextureRegion getTexture();

   public void draw(Batch batch, Ent source, float x, float y, Color colour, TextureRegion lapel2D) {
      x = (int)x;
      y = (int)y;
      int sz = this.getSZ(source);
      batch.setColor(Colours.dark);
      Draw.fillRectangle(batch, x + 1.0F, y + 1.0F, sz - 2, sz - 2);
      if (colour != null) {
         batch.setColor(colour);
         Draw.drawRectangle(batch, x, y, sz, sz, 1);
      }

      if (lapel2D != null) {
         Draw.draw(batch, lapel2D, x, y);
      }
   }

   protected int getSZ(Ent source) {
      return source != null ? source.getSize().getPixels() : 16;
   }
}
