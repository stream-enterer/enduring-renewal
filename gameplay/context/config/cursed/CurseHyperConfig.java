package com.tann.dice.gameplay.context.config.cursed;

import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.cursey.CurseMode;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase.ResetPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import java.util.Arrays;
import java.util.Collection;

public class CurseHyperConfig extends BaseCurseConfig {
   public CurseHyperConfig() {
      super(Mode.CURSE_HYPER);
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(
         new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1, true, ModifierPickContext.Cursed)),
         new GlobalLevelRequirement(CurseConfig.AFTER_BOSS, new GlobalAddPhase(CurseMode.makeBlessingPick())),
         new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
      );
   }

   @Override
   public String getAnticheeseKey() {
      return "cursed-hyper";
   }
}
