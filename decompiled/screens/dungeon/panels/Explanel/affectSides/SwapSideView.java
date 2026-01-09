package com.tann.dice.screens.dungeon.panels.Explanel.affectSides;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class SwapSideView extends Group {
   final AffectSideTemplate affectSideTemplate;
   final boolean relative;
   final Color[] colours = new Color[]{Colours.light, Colours.grey, Colours.blue};

   public SwapSideView(SpecificSidesType sst) {
      this(sst, false);
   }

   public SwapSideView(SpecificSidesType sst, boolean relative) {
      this.affectSideTemplate = new AffectSideTemplate(sst);
      this.relative = relative;
      TextureRegion tr = this.affectSideTemplate.type.templateImage;
      this.setSize(tr.getRegionWidth(), tr.getRegionHeight());
      this.setTransform(false);
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      this.affectSideTemplate.draw(batch, this.getX(), this.getY());
      Vector2[] sidePositions = this.affectSideTemplate.type.sidePositions;

      for (int i = 0; i < sidePositions.length; i++) {
         Vector2 pos = sidePositions[i];
         batch.setColor(this.colours[this.relative ? i : 0]);
         int size = 4;
         Draw.fillRectangle(
            batch,
            (int)(this.getX() + pos.x + EntSize.reg.getPixels() / 2.0F - size / 2.0F),
            (int)(this.getY() + pos.y + EntSize.reg.getPixels() / 2.0F - size / 2.0F),
            size,
            size
         );
      }
   }
}
