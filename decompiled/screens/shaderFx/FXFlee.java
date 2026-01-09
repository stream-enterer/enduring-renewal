package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FXFlee extends FXContainer {
   final int distance;

   public FXFlee(Actor group, boolean player) {
      super(group);
      this.distance = (int)(group.getWidth() + 20.0F) * (player ? -1 : 1);
      this.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      this.addAction(Actions.sequence(Actions.alpha(1.0F, this.getDuration()), Actions.removeActor()));
   }

   public void draw(Batch batch, float parentAlpha) {
      this.group.setPosition(this.originalPosition.x + this.getColor().a * this.distance, this.originalPosition.y);
      this.group.draw(batch, parentAlpha);
      super.draw(batch, parentAlpha);
   }

   @Override
   public float getDuration() {
      return 0.55F;
   }
}
