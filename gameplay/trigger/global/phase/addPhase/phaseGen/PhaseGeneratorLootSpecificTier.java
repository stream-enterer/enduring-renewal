package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.SimpleChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorLootSpecificTier extends PhaseGenerator {
   final int tier;

   public PhaseGeneratorLootSpecificTier(int tier) {
      this.tier = tier;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      List<Choosable> loot = dc.getLootForPreviousLevel(this.tier);
      return Arrays.asList(new SimpleChoicePhase(loot));
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM_REWARD;
   }

   @Override
   public String describe() {
      return "Choose a tier " + Words.getTierString(this.tier) + " item";
   }
}
