package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.PositionSwapPhase;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorPositionSwap extends PhaseGenerator {
   @Override
   public List<Phase> generate(DungeonContext dc) {
      return Arrays.asList(new PositionSwapPhase(dc.getParty().getHeroes().size()));
   }
}
