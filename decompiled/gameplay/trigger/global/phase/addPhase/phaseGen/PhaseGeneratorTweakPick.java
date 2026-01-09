package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Collision;
import java.util.ArrayList;
import java.util.List;

public class PhaseGeneratorTweakPick extends PhaseGeneratorChoosableOffer {
   @Override
   public List<Choosable> getChoices(DungeonContext dc) {
      return new ArrayList<>(ModifierPickUtils.generateModifiers(0, 0, 1, ModifierPickContext.Difficulty, dc));
   }

   @Override
   public String describe() {
      return "optional tweak";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.MODIFIER;
   }
}
