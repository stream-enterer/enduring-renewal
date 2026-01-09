package com.tann.dice.screens.dungeon.panels.combatEffects.slime;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;

public class SlimeDrop extends Actor {
   int size;

   public SlimeDrop(float startX, float startY, int slimeAttackSize) {
      this.size = (int)(slimeAttackSize / 3.0F + Tann.random() * 2.0F);
      this.setColor(Colours.green);
      this.setPosition(startX, startY);
      float duration = Tann.random();
      Vector2 add = new Vector2(1.0F, 0.0F).rotate(Tann.random() * 360.0F).scl(Tann.random() * (this.size + 15));
      this.addAction(
         Actions.sequence(Actions.parallel(Actions.moveBy(add.x, add.y, duration, Interpolation.pow2Out), Actions.fadeOut(duration)), Actions.removeActor())
      );
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.fillEllipse(batch, this.getX(), this.getY(), this.size, this.size);
      super.draw(batch, parentAlpha);
   }
}
