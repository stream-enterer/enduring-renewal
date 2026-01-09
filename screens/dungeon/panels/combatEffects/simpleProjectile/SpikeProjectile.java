package com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;

public class SpikeProjectile extends SimpleAbstractProjectile {
   public SpikeProjectile(Ent source, Ent target, int damage) {
      super(source, target, damage, 0.35F);
   }

   @Override
   protected void internalStart() {
      Sounds.playSound(Sounds.arrowFly);
   }

   @Override
   protected TextureRegion getImage() {
      return Images.combatEffectSpikeProjectile;
   }

   @Override
   protected void arrowImpact() {
      Sounds.playSound(Sounds.impacts, 0.55F, 1.5F);
      this.remove();
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.15F;
   }
}
