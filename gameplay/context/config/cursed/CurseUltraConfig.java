package com.tann.dice.gameplay.context.config.cursed;

import com.tann.dice.gameplay.mode.Mode;
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

public class CurseUltraConfig extends BaseCurseConfig {
   public CurseUltraConfig() {
      super(Mode.CURSED_ULTRA);
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(
         new GlobalLevelRequirement(
            new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorModifierPick(2, 1, -3, false, ModifierPickContext.Cursed, 1))
         ),
         new GlobalLevelRequirement(
            CurseConfig.AFTER_NONFINAL_BOSS, new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -3, true, ModifierPickContext.Cursed, 1))
         ),
         new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorModifierPickAdvanced(8, 10, ModifierPickContext.Difficulty))),
         new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
      );
   }

   @Override
   public String serialise() {
      return "";
   }

   @Override
   public String getAnticheeseKey() {
      return "curse-ultra";
   }
}
