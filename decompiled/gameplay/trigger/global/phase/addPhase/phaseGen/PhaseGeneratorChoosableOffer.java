package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoiceType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PhaseGeneratorChoosableOffer extends PhaseGenerator {
   public abstract List<Choosable> getChoices(DungeonContext var1);

   @Override
   public List<Phase> generate(DungeonContext dc) {
      List<Choosable> c = this.getChoices(dc);
      return (List<Phase>)(c == null ? new ArrayList<>() : Arrays.asList(new ChoicePhase(new ChoiceType(ChoiceType.ChoiceStyle.Optional, 1), c)));
   }
}
