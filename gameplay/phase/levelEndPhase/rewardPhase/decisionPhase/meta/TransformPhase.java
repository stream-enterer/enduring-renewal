package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;

public abstract class TransformPhase extends Phase {
   private void pushPhaseAndEnd(Phase p) {
      PhaseManager.get().pushPhaseNext(p);
      PhaseManager.get().popPhase(this.getClass());
   }

   @Override
   public void activate() {
      DungeonContext dc = this.getContext();
      if (dc == null) {
         this.pushPhaseAndEnd(new MessagePhase("Failed to find context"));
      } else {
         Phase p = this.makePhase(dc);
         this.pushPhaseAndEnd(p);
      }
   }

   @Override
   public void deactivate() {
   }

   protected abstract Phase makePhase(DungeonContext var1);

   @Override
   protected boolean isEphemeral() {
      return true;
   }
}
