package com.tann.dice.screens.dungeon.panels.threeD;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.statics.bullet.BulletStuff;

public class DieSpinner extends Actor3d {
   Die d;

   public DieSpinner(Die d, float size) {
      this.d = d;
      this.setSize(size, size);
      this.setTouchable(Touchable.disabled);
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      if (!OptionLib.HIDE_SPINNERS.c()) {
         com.tann.dice.Main.requestRendering();
         super.draw(batch, parentAlpha);
      }
   }

   @Override
   protected void draw3d() {
      float scale = 1.0F;
      Actor parent = this;

      while ((parent = parent.getParent()) != null) {
         scale *= parent.getScaleX();
      }

      float width = this.getWidth() * scale;
      float height = this.getHeight() * scale;
      int scaleFactor = com.tann.dice.Main.scale;
      Vector2 result = this.localToStageCoordinates(new Vector2());
      BulletStuff.drawSpinnyDie3(this.d, (result.x + width / 2.0F) * scaleFactor, (result.y + height / 2.0F) * scaleFactor, width * scaleFactor);
   }
}
