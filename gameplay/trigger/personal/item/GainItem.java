package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.sound.Sounds;

public class GainItem extends Personal {
   private final Item gained;

   public GainItem(Item gained) {
      this.gained = gained;
   }

   @Override
   public void endOfLevel(EntState entState, Snapshot snapshot) {
      DungeonContext dc = snapshot.getFightLog().getContext();
      if (!dc.allowInventory()) {
         Sounds.playSound(Sounds.error);
      } else {
         dc.getParty().addItem(this.gained);
         PhaseManager.get().pushPhase(new RandomRevealPhase(this.gained));
      }
   }
}
