package com.tann.dice.gameplay.mode.cursey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.BaseCurseConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase.ResetPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPickAdvanced;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAllButFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlyptraMode extends Mode {
   public BlyptraMode() {
      super("[green]Bl[cu][pink]yp[cu][red]tra[cu]");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{
         "a mix of " + Mode.BLURTRA.getTextButtonName() + " and " + Mode.CURSE_HYPER.getTextButtonName(),
         "quickly gain many powerful modifiers",
         "you will probably lose by crashing"
      };
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new BlyptraMode.BlyptraConfig());
   }

   @Override
   public Color getColour() {
      return Colours.pink;
   }

   @Override
   public String getSaveKey() {
      return "blyptra";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      BlyptraMode.BlyptraConfig cc = (BlyptraMode.BlyptraConfig)this.getConfigs().get(0);
      int furthestReached = cc.getFurthestReached();
      return (Actor)(furthestReached <= 0 ? new Actor() : new TextWriter("[yellow]Highscore: " + furthestReached, 5000, Colours.purple, 3));
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cursed;
   }

   @Override
   public long getBannedCollisionBits() {
      return CurseMode.getCollisionBitStatic();
   }

   public static class BlyptraConfig extends BaseCurseConfig {
      public BlyptraConfig() {
         super(Mode.BLYPTRA);
      }

      @Override
      public String getAnticheeseKey() {
         return "blyptra";
      }

      @Override
      public Collection<Global> getSpecificModeAddPhases() {
         return Arrays.asList(
            new GlobalLevelRequirement(
               new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorModifierPickAdvanced(8, 10, ModifierPickContext.Difficulty))
            ),
            new GlobalLevelRequirement(
               new LevelRequirementAllButFirst(), new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -3, true, ModifierPickContext.Cursed, 1))
            ),
            new GlobalLevelRequirement(
               CurseConfig.AFTER_BOSS, new GlobalAddPhase(new PhaseGeneratorModifierPickAdvanced(8, 10, ModifierPickContext.Difficulty))
            ),
            new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
         );
      }
   }
}
