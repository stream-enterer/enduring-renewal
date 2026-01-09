package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.SimpleChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Collision;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorStandardLoot extends PhaseGenerator {
   @Override
   public List<Phase> generate(DungeonContext dc) {
      if (dc.getNumLootItems() < 0) {
         return Arrays.asList(new MessagePhase("Negative loot options"));
      } else {
         List<Choosable> loot = dc.getLootForPreviousLevel();
         return Arrays.asList(new SimpleChoicePhase(loot));
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM_REWARD;
   }
}
