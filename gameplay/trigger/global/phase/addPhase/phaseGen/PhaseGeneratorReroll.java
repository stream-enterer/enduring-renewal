package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.HeroChangePhase;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorReroll extends PhaseGenerator {
   final HeroChangePhase.HeroRerollType hst;

   public PhaseGeneratorReroll() {
      this.hst = null;
   }

   public PhaseGeneratorReroll(HeroChangePhase.HeroRerollType hst) {
      this.hst = hst;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      return Arrays.asList(new HeroChangePhase((int)(Math.random() * dc.getParty().getHeroes().size()), this.hst));
   }
}
