package com.tann.dice.gameplay.fightLog.event.snapshot;

import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;

public class SnapshotTextEvent extends SnapshotEvent {
   final String text;

   public SnapshotTextEvent(String text) {
      this.text = text;
   }

   @Override
   public void act(AbilityHolder holder) {
      holder.addWisp(this.text);
      super.act(holder);
   }
}
