package com.tann.dice.screens.dungeon.panels.combatEffects.triBolt;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.LineActor;

public class TriBoltActor extends Actor {
   private static TextureRegion triBolt = ImageUtils.loadExt("combatEffects/misc/triBolt");
   float prevX;
   float prevY;

   public void act(float delta) {
      if (this.prevX != 0.0F) {
         Vector2 rot = new Vector2(this.getX() - this.prevX, this.getY() - this.prevY).nor().rotate(90.0F);

         for (int i = 0; i < 3; i++) {
            float bonusX = rot.x * (1 - i) * 2.0F;
            float bonusY = rot.y * (1 - i) * 2.0F;
            LineActor la = new LineActor(this.prevX + bonusX, this.prevY + bonusY, this.getX() + bonusX, this.getY() + bonusY);
            la.setColor(Colours.red);
            DungeonScreen.get().addActor(la);
            la.addAction(Actions.sequence(Actions.fadeOut(0.2F), Actions.removeActor()));
         }
      }

      this.toFront();
      this.prevX = this.getX();
      this.prevY = this.getY();
      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.z_white);
      batch.draw(triBolt, this.getX() - triBolt.getRegionWidth() / 2, this.getY() - triBolt.getRegionHeight() / 2);
   }
}
