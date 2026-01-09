package com.tann.dice.gameplay.context.config.cursed;

import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.cursey.CurseMode;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase.ResetPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPickAdvanced;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import java.util.Arrays;
import java.util.Collection;

public class BlursedConfig extends BaseCurseConfig {
   public BlursedConfig() {
      super(Mode.BLURSED);
   }

   public static PhaseGeneratorModifierPickAdvanced firstPickPhase() {
      return new PhaseGeneratorModifierPickAdvanced(10, 15, ModifierPickContext.Difficulty);
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(
         new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(firstPickPhase())),
         new GlobalLevelRequirement(
            CurseConfig.AFTER_NONFINAL_BOSS, new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1, true, ModifierPickContext.Cursed))
         ),
         new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(CurseMode.makeBlessingPick())),
         new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
      );
   }

   @Override
   public String serialise() {
      return "";
   }

   @Override
   public String getAnticheeseKey() {
      return "curse-easy";
   }
}
