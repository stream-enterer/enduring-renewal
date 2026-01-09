package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import java.util.List;

public interface PhaseProducer {
   List<Phase> get(DungeonContext var1);
}
