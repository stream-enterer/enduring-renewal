package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffect;

public class StartTurnCommand extends Command {
   public StartTurnCommand(String saved) {
      if (saved.length() != 1) {
         throw new RuntimeException(saved.length() + "(startturncommand)");
      }
   }

   public StartTurnCommand() {
   }

   @Override
   public Ent getSource() {
      return null;
   }

   @Override
   public void internalEnact(Snapshot snapshot) {
      snapshot.startTurn();
   }

   @Override
   protected boolean shouldSkipAnimation(Snapshot beforeShot) {
      return true;
   }

   @Override
   public CombatEffect makeCombatEffect() {
      return null;
   }

   @Override
   protected void showAnimation(CombatEffect combatEffect) {
   }

   @Override
   public boolean canUndo() {
      return false;
   }

   @Override
   public String toSave(Snapshot previous) {
      return SKIP;
   }
}
