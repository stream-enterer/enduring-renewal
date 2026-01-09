package com.tann.dice.gameplay.context.config.misc;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.debuggy.PickMode;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import java.util.Arrays;
import java.util.Collection;

public class PickConfig extends ContextConfig {
   public PickConfig() {
      super(Mode.PICK);
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
   }

   @Override
   public Zone getTypeForLevel(int levelNumber, DungeonContext context) {
      return super.getTypeForLevel(levelNumber, context);
   }

   @Override
   public DungeonContext makeContext(AntiCheeseRerollInfo original) {
      return PickMode.makeRestartContext();
   }

   @Override
   public int getTotalLength() {
      return 1;
   }
}
