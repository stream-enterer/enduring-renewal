package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorHardcoded extends PhaseGenerator {
   final Phase phase;

   public PhaseGeneratorHardcoded(Phase phase) {
      this.phase = phase;
   }

   @Override
   public String describe() {
      return "Add " + Words.aOrAn(this.phase.toString().toLowerCase());
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      return Arrays.asList(this.phase.copy());
   }
}
