package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffect;
import com.tann.dice.screens.dungeon.panels.combatEffects.endTurn.EndTurnController;

public class EndTurnCommand extends Command {
   public final boolean player;

   public EndTurnCommand(String saved) {
      if (saved.length() != 1) {
         throw new RuntimeException(saved + "(endturncommand)");
      } else {
         this.player = false;
      }
   }

   public EndTurnCommand(boolean player) {
      this.player = player;
      this.lockSave(null);
   }

   @Override
   public Ent getSource() {
      return null;
   }

   @Override
   public void internalEnact(Snapshot snapshot) {
      snapshot.endTurn(this.player);
   }

   @Override
   protected boolean shouldSkipAnimation(Snapshot beforeShot) {
      return false;
   }

   @Override
   public CombatEffect makeCombatEffect() {
      return this.player ? null : new CombatEffect(new EndTurnController(), null);
   }

   @Override
   protected void showAnimation(CombatEffect combatEffect) {
      combatEffect.internalStart();
   }

   @Override
   public boolean canUndo() {
      return false;
   }

   @Override
   public String toSave(Snapshot previous) {
      return this.player ? SKIP : "4";
   }
}
