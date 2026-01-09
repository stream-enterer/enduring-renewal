package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.sound.Sounds;

public class GainModifier extends Personal {
   private final Modifier gained;

   public GainModifier(Modifier gained) {
      this.gained = gained;
   }

   @Override
   public void endOfLevel(EntState entState, Snapshot snapshot) {
      DungeonContext dc = snapshot.getFightLog().getContext();
      if (!dc.allowInventory()) {
         Sounds.playSound(Sounds.error);
      } else {
         dc.addModifier(this.gained, true);
         if (!this.gained.skipNotifyRandomReveal()) {
            PhaseManager.get().pushPhase(new RandomRevealPhase(this.gained));
         }
      }
   }
}
