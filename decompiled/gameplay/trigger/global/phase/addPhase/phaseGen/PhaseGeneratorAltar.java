package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosableRange;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.trade.TradePhase;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorAltar extends PhaseGenerator {
   final ChoosableType choosableType;

   public PhaseGeneratorAltar(ChoosableType type) {
      this.choosableType = type;
   }

   @Override
   public String describe() {
      return "Altar Phase";
   }

   private static int getLossTier(HeroType ht) {
      return ModifierLib.byName("add." + ht.getName()).getTier();
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      HeroType top = dc.getParty().getHeroes().get(0).getHeroType();
      int tier = getLossTier(top);
      return Arrays.asList(
         new TradePhase(Arrays.asList(ModifierLib.byName("Top.Missing"), new RandomTieredChoosableRange(tier - 1, tier + 1, 1, this.choosableType)))
      );
   }
}
