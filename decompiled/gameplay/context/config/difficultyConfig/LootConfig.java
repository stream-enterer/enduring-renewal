package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.ItemCombinePhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorChallenge;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAllButFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementHash;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LootConfig extends DifficultyConfig {
   public LootConfig(Difficulty difficulty) {
      super(Mode.LOOT, difficulty);
   }

   public LootConfig(String serial) {
      this(Difficulty.valueOf(serial));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new LootConfig(d));
      }

      return configs;
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      Collection<Global> result = super.getSpecificModeAddPhases();
      result.add(new GlobalLevelRequirement(new LevelRequirementAllButFirst(), new GlobalAddPhase(new PhaseGeneratorStandardLoot())));
      result.add(
         new GlobalLevelRequirement(
            new LevelRequirementHash(6, 16), new GlobalAddPhase(new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Standard))
         )
      );
      result.add(new GlobalLevelRequirement(new LevelRequirementHash(10, 18), new GlobalAddPhase(new PhaseGeneratorHardcoded(new ItemCombinePhase()))));
      return result;
   }

   @Override
   protected boolean offerStandardRewards() {
      return false;
   }

   @Override
   protected boolean offerChanceEvents() {
      return false;
   }

   @Override
   public List<Modifier> getStartingModifiers() {
      return Arrays.asList(ModifierLib.byName("Deep Pockets"));
   }
}
