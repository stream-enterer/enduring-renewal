package com.tann.dice.gameplay.context.config.misc;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DebugConfig extends ContextConfig {
   Zone type = Zone.Forest;

   public DebugConfig() {
      super(Mode.DEBUG);
   }

   public DebugConfig(Zone type) {
      this();
      this.type = type;
   }

   @Override
   public void quitAction() {
      com.tann.dice.Main.self().setScreen(new TitleScreen(Mode.CLASSIC));
   }

   @Override
   public String getSpecificKey() {
      return "debug-specific";
   }

   @Override
   public String serialise() {
      return "";
   }

   @Override
   public DungeonContext makeContext(AntiCheeseRerollInfo acri) {
      return null;
   }

   @Override
   public StandardButton makeStartButton(boolean big) {
      return null;
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
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
   }

   @Override
   public List<TP<Zone, Integer>> getOverrideLevelTypes(DungeonContext context) {
      return Arrays.asList(new TP<>(this.type, 1));
   }

   @Override
   public boolean canRestart() {
      return false;
   }
}
