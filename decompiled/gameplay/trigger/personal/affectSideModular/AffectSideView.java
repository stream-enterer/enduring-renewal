package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.screens.dungeon.panels.Explanel.affectSides.AffectSideTemplate;
import com.tann.dice.util.Colours;

public class AffectSideView extends GenericView {
   final AffectSideTemplate affectSideTemplate;

   public AffectSideView(AffectSideTemplate affectSideTemplate) {
      this.affectSideTemplate = affectSideTemplate;
      this.setSize(affectSideTemplate.type.templateImage.getRegionWidth(), affectSideTemplate.type.templateImage.getRegionHeight());
   }

   @Override
   public Vector2[] getSidePositions() {
      return this.affectSideTemplate.getSidePositions();
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      this.affectSideTemplate.draw(batch, this.getX(), this.getY(), Colours.AS_BORDER);
      super.draw(batch, parentAlpha);
   }
}
