package com.tann.dice.gameplay.fightLog.event.snapshot;

import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.statics.sound.Sounds;

public class ManaGainEvent extends SnapshotEvent {
   final int gained;
   final String source;

   public ManaGainEvent(int gained, String source) {
      this.gained = gained;
      this.source = source;
   }

   @Override
   public void act(AbilityHolder holder) {
      Sounds.playSound(Sounds.magic);
      holder.addWisp("[blue]+" + this.gained + " (" + this.source + ")");
      super.act(holder);
   }
}
