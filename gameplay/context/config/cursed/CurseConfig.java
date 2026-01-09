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
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAND;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.NotLevelRequirement;
import java.util.Arrays;
import java.util.Collection;

public class CurseConfig extends BaseCurseConfig {
   public static final LevelRequirement RESET_REQ = new LevelRequirementAND(
      new LevelRequirementMod(20, 1), new NotLevelRequirement(new LevelRequirementFirst())
   );
   public static final LevelRequirement AFTER_NONFINAL_BOSS = new LevelRequirementAND(
      new LevelRequirementMod(4, 1), new NotLevelRequirement(new LevelRequirementMod(20, 1))
   );
   public static final LevelRequirement AFTER_BOSS = new LevelRequirementAND(
      new LevelRequirementMod(4, 1), new NotLevelRequirement(new LevelRequirementFirst())
   );
   public static final LevelRequirement NOT_AFTER_BOSS = new NotLevelRequirement(AFTER_BOSS);

   public CurseConfig() {
      super(Mode.CURSE);
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(
         new GlobalLevelRequirement(
            new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorModifierPick(5, 1, -1, false, ModifierPickContext.Cursed))
         ),
         new GlobalLevelRequirement(AFTER_NONFINAL_BOSS, new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1, true, ModifierPickContext.Cursed))),
         new GlobalLevelRequirement(RESET_REQ, new GlobalAddPhase(CurseMode.makeBlessingPick())),
         new GlobalLevelRequirement(RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
      );
   }

   @Override
   public String serialise() {
      return "";
   }

   @Override
   public String getAnticheeseKey() {
      return "curse";
   }
}
