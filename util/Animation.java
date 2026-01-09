package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class Animation extends Group {
   float speed;
   Array<AtlasRegion> regions;

   public Animation(float speed, String textureName) {
      this.speed = speed;
      this.regions = com.tann.dice.Main.atlas.findRegions(textureName);
      this.setSize(((AtlasRegion)this.regions.get(0)).getRegionWidth(), ((AtlasRegion)this.regions.get(0)).getRegionHeight());
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      batch.draw((TextureRegion)this.regions.get((int)(com.tann.dice.Main.secs / this.speed) % this.regions.size), this.getX(), this.getY());
   }
}
