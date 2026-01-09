package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.Snapshot;

public class SimpleCommand extends TargetableCommand {
   SimpleTargetable targetable;

   public SimpleCommand(Ent target, SimpleTargetable targetable) {
      super(targetable, target);
      this.targetable = targetable;
   }

   @Override
   public Ent getSource() {
      return this.targetable.getSource();
   }

   @Override
   protected boolean shouldSkipAnimation(Snapshot beforeShot) {
      return false;
   }

   @Override
   public boolean canUndo() {
      return false;
   }
}
