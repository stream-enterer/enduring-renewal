package com.tann.dice.gameplay.trigger.global.phase;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.List;

public class GlobalSkipPhase extends Global {
   private final boolean skipAll;

   public GlobalSkipPhase() {
      this(false);
   }

   public GlobalSkipPhase(boolean skipAll) {
      this.skipAll = skipAll;
   }

   @Override
   public String describeForSelfBuff() {
      return this.skipAll ? "Skip all events/rewards" : "Skip rewards";
   }

   @Override
   public void affectPhasesPost(int currentLevel, DungeonContext context, List<Phase> result) {
      int numRemoved = 0;

      for (int i = result.size() - 1; i >= 0; i--) {
         Phase p = result.get(i);
         if (p.isPositive() || this.skipAll) {
            result.remove(i);
            numRemoved++;
         }
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM_REWARD | Collision.LEVELUP_REWARD | Collision.SPECIFIC_LEVEL;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isDescribedAsBeforeFight() {
      return true;
   }
}
