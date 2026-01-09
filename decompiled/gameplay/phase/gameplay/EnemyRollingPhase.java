package com.tann.dice.gameplay.phase.gameplay;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.bullet.BulletStuff;

public class EnemyRollingPhase extends Phase {
   @Override
   public void activate() {
      DungeonScreen.get().save();
      FightLog f = this.getFightLog();
      f.maybeStart();
      DungeonScreen.get().slideSpellHolder(AbilityHolder.TuckState.OffScreen, true);
      BulletStuff.refreshEntities(f.getSnapshot(FightLog.Temporality.Present).getAliveEntities());
      DungeonScreen.get().enemy.afterInPlace(new Runnable() {
         @Override
         public void run() {
            BulletStuff.setupWalls();
            DungeonScreen.get().rollManager.resetForRoll(false);
         }
      });
   }

   @Override
   public void tick(float delta) {
      com.tann.dice.Main.requestRendering();
   }

   @Override
   public void deactivate() {
      PhaseManager.get().pushPhase(new PlayerRollingPhase());
   }

   @Override
   public long getSwitchingDelay() {
      return 500L;
   }

   @Override
   protected boolean doneCheck() {
      for (Ent m : this.getFightLog().getSnapshot(FightLog.Temporality.Present).getAliveMonsterEntities()) {
         if (m.getDie().getState() != Die.DieState.Locked) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String serialise() {
      return "3";
   }

   @Override
   public boolean isDuringCombat() {
      return true;
   }

   @Override
   public boolean highlightDice() {
      return true;
   }

   @Override
   public boolean updateDice() {
      return true;
   }

   @Override
   public boolean canSave() {
      return false;
   }

   @Override
   public boolean disallowRescale() {
      return true;
   }

   @Override
   public boolean showTargetButton() {
      return false;
   }
}
