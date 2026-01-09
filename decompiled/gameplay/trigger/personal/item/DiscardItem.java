package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class DiscardItem extends Personal {
   final Item discarded;

   public DiscardItem(Item item) {
      this.discarded = item;
   }

   @Override
   public boolean ignoreItem(Item item) {
      return item == this.discarded;
   }

   @Override
   public void endOfLevel(EntState entState, Snapshot snapshot) {
      snapshot.getFightLog().getContext().getParty().discardItem(this.discarded);
   }

   @Override
   public boolean persistThroughDeathBuff() {
      return true;
   }
}
