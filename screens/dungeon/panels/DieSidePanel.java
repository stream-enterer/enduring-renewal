package com.tann.dice.screens.dungeon.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class DieSidePanel extends Group {
   public static final float EQUIP_BONUS_FLASH_DURATION = 0.4F;
   public static final Color EQUIP_BONUS_FLASH_COLOUR = Colours.light;
   public static final Interpolation EQUIP_BONUS_FLASH_INTERPOLATION = Interpolation.smooth;
   public EntSide side;
   Ent ent;

   public DieSidePanel(EntSide side, Ent ent) {
      this.side = side;
      this.ent = ent;
      if (ent != null) {
         this.setSize(ent.getPixelSize(), ent.getPixelSize());
      } else {
         this.setSize(side.getTexture().getRegionWidth(), side.getTexture().getRegionHeight());
      }

      this.setTransform(false);
   }

   private String balFloat(float power, float extraPower) {
      String s = this.bfs(power);
      if (extraPower != 0.0F) {
         s = s + "[n]" + this.bfs(extraPower);
      }

      return s;
   }

   private String bfs(float input) {
      float powerM10 = Math.round(input * 10.0F);
      float roundedPower = powerM10 / 10.0F;
      String fs = roundedPower + "";
      if (fs.length() > 2) {
         fs = fs.substring(0, 3);
      }

      if (fs.length() > 2 && fs.charAt(2) == '.') {
         fs = fs.substring(0, 2);
      }

      return fs;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.ent == null) {
         this.side.draw(batch, null, this.getX(), this.getY(), Colours.purple, null);
      } else {
         this.side.draw(batch, this.ent, this.getX(), this.getY(), this.ent.getColour(), this.ent.get2DLapel());
      }

      super.draw(batch, parentAlpha);
   }

   public void drawHighlight(Batch batch) {
      batch.setColor(Colours.light);
      Draw.drawRectangle(batch, this.getX() + this.getParent().getX(), this.getY() + this.getParent().getY(), this.getWidth(), this.getHeight(), 1);
   }
}
