package com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.FlameActor;

public class FireBladeActor extends SimpleStrike {
   private static final float FLAME_DURATION = 0.3F;
   private static final float FLAME_SPAWN_DURATION = 0.08F;
   private static final int NUM_FLAMES = 15;

   public FireBladeActor(Ent target, int damage, TextureRegion image, float impactTime, float stickTime, float retractTime) {
      super(target, damage, image, impactTime, stickTime, retractTime);
   }

   @Override
   protected void impact() {
      super.impact();
      Sounds.playSound(Sounds.fire);

      for (int i = 0; i < 15; i++) {
         FlameActor flameActor = new FlameActor(8, 15.0F, true, 0.3F);
         flameActor.setPosition(this.getX() + this.getWidth(), this.getY());
         DungeonScreen.get().addActor(flameActor);
         flameActor.animate(i / 15.0F * 0.08F);
      }
   }
}
