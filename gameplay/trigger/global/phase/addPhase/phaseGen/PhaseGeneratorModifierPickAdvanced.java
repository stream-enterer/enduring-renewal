package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoiceType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Collision;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorModifierPickAdvanced extends PhaseGenerator {
   final int numOptions;
   final int totalValue;
   final ModifierPickContext context;

   public PhaseGeneratorModifierPickAdvanced(int numOptions, int totalValue, ModifierPickContext context) {
      this.numOptions = numOptions;
      this.totalValue = totalValue;
      this.context = context;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      List<Modifier> mods = ModifierPickUtils.getModifiersAddingUpTo(this.numOptions, this.totalValue, this.context, false, dc);
      List<Choosable> ch = new ArrayList<>(mods);
      return Arrays.asList(new ChoicePhase(new ChoiceType(ChoiceType.ChoiceStyle.PointBuy, this.totalValue), ch));
   }

   @Override
   public String describe() {
      return "Choose stuff";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.MODIFIER;
   }
}
