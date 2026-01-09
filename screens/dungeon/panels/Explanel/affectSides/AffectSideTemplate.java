package com.tann.dice.screens.dungeon.panels.Explanel.affectSides;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class AffectSideTemplate {
   public final SpecificSidesType type;

   public AffectSideTemplate(SpecificSidesType type) {
      this.type = type;
   }

   public void draw(Batch batch, float x, float y) {
      this.draw(batch, x, y, Colours.AS_BORDER);
   }

   public void draw(Batch batch, float x, float y, Color col) {
      batch.setColor(col);
      Draw.drawRotatedScaled(batch, this.type.templateImage, x, y, 1.0F, 1.0F, 0.0F);
   }

   public Vector2[] getSidePositions() {
      return this.type.sidePositions;
   }
}
