package com.tann.dice.screens.dungeon.panels.combatEffects.flame;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.FlameActor;
import com.tann.dice.util.Tann;

public class FlameGroup extends CombatEffectActor {
   static final float SPAWN_DURATION = 0.15F;
   static final float FADE_DURATION = 0.3F;
   static final float FLAME_DIST = 2.0F;
   static final int NUM_FLAMES = 15;
   Ent target;

   public FlameGroup(Ent target) {
      this.target = target;
      this.setTransform(false);
   }

   @Override
   protected void start(FightLog fightLog) {
      Sounds.playSound(Sounds.fire);
      EntPanelCombat targetPanel = this.target.getEntPanel();
      this.setSize(20.0F, 20.0F);
      targetPanel.addActor(this);
      this.setPosition(targetPanel.getWidth() / 2.0F, targetPanel.getHeight() / 2.0F);

      for (int i = 0; i < 15; i++) {
         FlameActor flameActor = new FlameActor(15, 10.0F, false, 0.3F);
         this.addActor(flameActor);
         flameActor.animate(i / 15.0F * 0.15F);
         Tann.setPosition(flameActor, Tann.randomRadial(2.0F));
      }
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.075F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.375F;
   }
}
