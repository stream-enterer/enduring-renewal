package com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.LightningActor;
import com.tann.dice.util.Tann;

public class ElectroBladeActor extends SimpleStrike {
   private static final float SPARK_DURATION = 0.1F;

   public ElectroBladeActor(Ent target, int damage, TextureRegion image, float impactTime, float stickTime, float retractTime) {
      super(target, damage, image, impactTime, stickTime, retractTime);
   }

   @Override
   protected void impact() {
      Sounds.playSound(Sounds.lightning);
      float yVariance = this.target.getEntPanel().getHeight() / 2.0F + 40.0F;
      int sparks = 8;

      for (int i = 0; i < sparks; i++) {
         LightningActor lightningActor = new LightningActor(
            this.getX(),
            this.getY(),
            this.getX() + Tann.random(40.0F) + 40.0F,
            this.getY() + (Tann.random(yVariance / 2.0F) + yVariance / 2.0F) * (Tann.half() ? 1 : -1),
            5.0F,
            5.0F
         );
         DungeonScreen.get().addActor(lightningActor);
         lightningActor.setColor(Colours.withAlpha(Tann.half() ? Colours.light : Colours.blue, 0.0F));
         lightningActor.addAction(Actions.sequence(Actions.delay((float)i / sparks * 0.1F), Actions.alpha(1.0F), Tann.fadeAndRemove(0.4F)));
      }
   }
}
