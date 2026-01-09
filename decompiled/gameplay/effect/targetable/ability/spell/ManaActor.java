package com.tann.dice.gameplay.effect.targetable.ability.spell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;

public class ManaActor extends Actor {
   public boolean struck;

   public ManaActor() {
      TextureRegion tr = this.getRegion();
      this.setSize(tr.getRegionWidth(), tr.getRegionHeight());
   }

   private TextureRegion getRegion() {
      return this.struck ? Images.manaStruck : Images.mana;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      batch.draw(this.getRegion(), this.getX(), this.getY());
      super.draw(batch, parentAlpha);
   }

   public void setStruck(boolean struck) {
      this.struck = struck;
   }
}
