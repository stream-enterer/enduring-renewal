package com.tann.dice.gameplay.phase.gameplay;

import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.Tann;

public class DamagePhase extends Phase {
   @Override
   public void activate() {
      if (this.fromSave) {
         this.s();
      } else {
         Tann.delay(0.1F, new Runnable() {
            @Override
            public void run() {
               DungeonScreen.get().save();
               DamagePhase.this.s();
            }
         });
      }
   }

   private void s() {
      this.getFightLog().enemyTurn();
      if (this.fromSave) {
         this.getFightLog().instantCatchup();
      }
   }

   @Override
   public void deactivate() {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         Snapshot present = ds.getFightLog().getSnapshot(FightLog.Temporality.Present);
         if (present.warrantsSurrender()) {
            if (OptionLib.AUTO_FLEE.c()) {
               new SurrenderPhase().accept();
            } else {
               PhaseManager.get().pushPhase(new SurrenderPhase());
            }

            return;
         }
      }

      PhaseManager.get().pushPhase(new EnemyRollingPhase());
   }

   @Override
   public boolean isDuringCombat() {
      return true;
   }

   @Override
   public String serialise() {
      return "d";
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean highlightDice() {
      return true;
   }

   @Override
   public boolean disallowRescale() {
      return true;
   }

   @Override
   public void tick(float delta) {
      com.tann.dice.Main.requestRendering();
   }
}
