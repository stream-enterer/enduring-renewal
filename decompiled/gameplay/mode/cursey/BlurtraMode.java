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
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlurtraMode extends Mode {
   public BlurtraMode() {
      super("[green]Bl[cu][purple]ur[cu][red]tra[cu]");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"same as " + Mode.CURSED_ULTRA.getTextButtonName() + " mode", "but start with a blessing choice", "[grey]Warning: long"};
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new BlurtraMode.BlurtraConfig());
   }

   @Override
   public Color getColour() {
      return Colours.green;
   }

   @Override
   public String getSaveKey() {
      return "blurtra3";
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      BlurtraMode.BlurtraConfig cc = (BlurtraMode.BlurtraConfig)this.getConfigs().get(0);
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

   public static class BlurtraConfig extends BaseCurseConfig {
      public BlurtraConfig() {
         super(Mode.BLURTRA);
      }

      @Override
      public String getAnticheeseKey() {
         return "blurtra";
      }

      @Override
      public Collection<Global> getSpecificModeAddPhases() {
         return Arrays.asList(
            new GlobalLevelRequirement(
               new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorModifierPickAdvanced(8, 10, ModifierPickContext.Difficulty))
            ),
            new GlobalLevelRequirement(
               CurseConfig.AFTER_NONFINAL_BOSS, new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -3, true, ModifierPickContext.Cursed, 1))
            ),
            new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorModifierPickAdvanced(8, 10, ModifierPickContext.Difficulty))),
            new GlobalLevelRequirement(CurseConfig.RESET_REQ, new GlobalAddPhase(new PhaseGeneratorHardcoded(new ResetPhase())))
         );
      }
   }
}
