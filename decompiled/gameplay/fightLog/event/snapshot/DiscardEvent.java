package com.tann.dice.gameplay.fightLog.event.snapshot;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.statics.sound.Sounds;

public class DiscardEvent extends SnapshotEvent {
   final Item i;
   final String message;

   public DiscardEvent(Item i) {
      this(i, "[purple]Discarded");
   }

   public DiscardEvent(Item i, String message) {
      this.i = i;
      this.message = message;
   }

   @Override
   public void act(AbilityHolder holder) {
      holder.addWisp(this.message);
      Sounds.playSound(Sounds.lightning);
      super.act(holder);
   }
}
