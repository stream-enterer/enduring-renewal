package com.tann.dice.screens.titleScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Draw;

public class ScaleRegionActor extends Actor {
   final TextureRegion tr;

   public ScaleRegionActor(TextureRegion tr) {
      this.tr = tr;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.scaleRegion(batch, this.tr, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
   }
}
