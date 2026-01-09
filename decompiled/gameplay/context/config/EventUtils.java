package com.tann.dice.gameplay.context.config;

import com.tann.dice.gameplay.context.config.event.EventGenerator;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoiceType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.OrChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosableRange;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.PhaseGeneratorTransformPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.HeroChangePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.ItemCombinePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.trade.TradePhase;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.container.GlobalContainerAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhaseBag;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorChallenge;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorPositionSwap;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorReroll;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorTrade;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorTweakPick;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAND;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementHash;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.NotLevelRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventUtils {
   public static List<EventGenerator> makeEvents() {
      List<EventGenerator> result = new ArrayList<>();
      PhaseGenerator blessTwo = new PhaseGeneratorModifierPick(3, 1, 2, true, ModifierPickContext.Difficulty_But_Midgame);
      if (!UnUtil.isLocked(Feature.EVENTS_SIMPLE)) {
         result.addAll(
            Arrays.asList(
               new EventGenerator(0.1F, 0.01F, new LevelRequirementHash(2, 18), new PhaseGeneratorPositionSwap()),
               new EventGenerator(0.01F, 2.0F, new LevelRequirementHash(2, 19), blessTwo),
               new EventGenerator(1.0F, 0.2F, new LevelRequirementHash(2, 19), new PhaseGeneratorReroll(HeroChangePhase.HeroRerollType.BASIC)),
               new EventGenerator(0.4F, 0.15F, new LevelRequirementHash(2, 18), new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Easy)),
               new EventGenerator(0.2F, 0.8F, new LevelRequirementHash(2, 19), new PhaseGeneratorTweakPick())
            )
         );
      }

      if (!UnUtil.isLocked(Feature.EVENTS_COMPLEX)) {
         result.addAll(
            Arrays.asList(
               new EventGenerator(1.0F, 0.3F, new LevelRequirementHash(2, 19), new PhaseGeneratorReroll()),
               new EventGenerator(0.4F, 0.3F, new LevelRequirementHash(2, 6), new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Standard)),
               new EventGenerator(0.4F, 0.6F, new LevelRequirementHash(10, 15), new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Standard)),
               new EventGenerator(0.4F, 0.8F, new LevelRequirementHash(8, 19), new PhaseGeneratorHardcoded(new ItemCombinePhase())),
               new EventGenerator(
                  0.1F,
                  0.15F,
                  new LevelRequirementHash(2, 19),
                  new PhaseGeneratorHardcoded(new TradePhase(Arrays.asList(new RandomTieredChoosable(0, 1, ChoosableType.Modifier))))
               ),
               new EventGenerator(
                  0.3F,
                  0.3F,
                  new LevelRequirementHash(2, 3),
                  new PhaseGeneratorHardcoded(
                     new TradePhase(
                        Arrays.asList(new RandomTieredChoosable(-1, 1, ChoosableType.Modifier), new RandomTieredChoosable(1, 4, ChoosableType.Item))
                     )
                  )
               ),
               new EventGenerator(
                  0.2F,
                  0.2F,
                  new LevelRequirementHash(3, 20),
                  new PhaseGeneratorHardcoded(
                     new TradePhase(Arrays.asList(new RandomTieredChoosable(-1, 1, ChoosableType.Item), new RandomTieredChoosable(4, 1, ChoosableType.Item)))
                  )
               )
            )
         );
      }

      if (!UnUtil.isLocked(Feature.EVENTS_WEIRD)) {
         result.addAll(
            Arrays.asList(
               new EventGenerator(
                  0.07F,
                  0.13F,
                  new LevelRequirementHash(3, 10),
                  new PhaseGeneratorHardcoded(
                     new ChoicePhase(
                        new ChoiceType(ChoiceType.ChoiceStyle.Optional, 1),
                        Arrays.asList(
                           new OrChoosable(new RandomTieredChoosable(4, 1, ChoosableType.Item), new RandomTieredChoosable(-1, 1, ChoosableType.Item))
                        )
                     )
                  )
               ),
               new EventGenerator(
                  1.0E-4F,
                  0.8F,
                  new LevelRequirementHash(3, 20),
                  new PhaseGeneratorHardcoded(
                     new TradePhase(
                        Arrays.asList(new RandomTieredChoosable(2, 1, ChoosableType.Hero), new RandomTieredChoosableRange(-6, -8, 1, ChoosableType.Modifier))
                     )
                  )
               ),
               new EventGenerator(0.05F, 0.8F, new LevelRequirementHash(3, 19), new PhaseGeneratorTrade())
            )
         );
      }

      return result;
   }

   public static GlobalAddPhaseBag makeContainedEvents() {
      List<EventGenerator> adds = makeEvents();
      return new GlobalAddPhaseBag(adds, 0.7F);
   }

   public static GlobalContainerAddPhase makeAddPhaseContainer(ContextConfig cc) {
      List<Global> toContain = new ArrayList<>();
      if (cc.offerChanceEvents() && !UnUtil.isLocked(Feature.EVENTS_SIMPLE)) {
         toContain.add(makeContainedEvents());
      }

      if (cc.offerStandardRewards()) {
         toContain.add(
            new GlobalLevelRequirement(
               new LevelRequirementMod(2, 0), new GlobalAddPhase(new PhaseGeneratorHardcoded(new PhaseGeneratorTransformPhase(new PhaseGeneratorLevelup())))
            )
         );
         toContain.add(
            new GlobalLevelRequirement(
               new LevelRequirementAND(new LevelRequirementMod(2, 1), new NotLevelRequirement(new LevelRequirementMod(20, 1))),
               new GlobalAddPhase(new PhaseGeneratorHardcoded(new PhaseGeneratorTransformPhase(new PhaseGeneratorStandardLoot())))
            )
         );
      }

      toContain.addAll(cc.getSpecificModeAddPhases());
      return new GlobalContainerAddPhase(toContain);
   }
}
