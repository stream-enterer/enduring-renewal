package com.tann.dice.screens.dungeon.panels.combatEffects.batSwarm;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.EntContainer;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Animation;
import com.tann.dice.util.Tann;

public class BatSwarmController extends CombatEffectController {
   private static final int NUM_BATS = 30;
   private static final float BAT_SPAWN_TIME = 0.3F;
   private static final float BAT_DURATION = 0.5F;

   @Override
   protected void start() {
      Sounds.playSound(Sounds.bats);

      for (int i = 0; i < 30; i++) {
         Animation bat = new Animation(0.1F, "combatEffects/misc/bat");
         bat.setColor(1.0F, 1.0F, 1.0F, 0.0F);
         EntContainer entContainer = DungeonScreen.get().hero;
         entContainer.addActor(bat);
         boolean top = Tann.half();
         float from = top ? entContainer.getHeight() : 0.0F;
         float to = entContainer.getHeight() - from;
         bat.setPosition(Tann.random(entContainer.getWidth()), from);
         bat.addAction(
            Actions.sequence(
               Actions.delay(this.getImpactDuration() / 30.0F * i),
               Actions.alpha(1.0F),
               Actions.moveTo(Tann.random(entContainer.getWidth()), to, 0.5F),
               Actions.removeActor()
            )
         );
      }
   }

   @Override
   protected float getImpactDuration() {
      return 0.3F * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return 0.5F * OptionUtils.unkAnim();
   }
}
