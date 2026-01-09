package com.tann.dice.gameplay.context.config.misc;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BalanceConfig extends ContextConfig {
   public final int level;

   public BalanceConfig(int level) {
      super(Mode.BALANCE);
      this.level = level;
   }

   public BalanceConfig(String serial) {
      this(Integer.parseInt(serial));
   }

   public static List<ContextConfig> make() {
      ArrayList<ContextConfig> result = new ArrayList<>();

      for (int i = 1; i <= 20; i++) {
         result.add(new BalanceConfig(i));
      }

      return result;
   }

   @Override
   public int getLevelOffset() {
      return this.level - 1;
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      Collection<Global> result = super.getSpecificModeAddPhases();
      if (this.level != 0) {
         result.add(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
      }

      return result;
   }

   @Override
   public StandardButton makeStartButton(boolean big) {
      return new StandardButton(TextWriter.getTag(this.level % 4 == 0 ? Colours.orange : this.mode.getColour()) + this.level).makeTiny();
   }

   @Override
   public int getTotalLength() {
      return 1;
   }

   @Override
   public List<TP<Zone, Integer>> getOverrideLevelTypes(DungeonContext context) {
      int start = context.getCurrentLevelNumber() - 1;

      for (TP<Zone, Integer> tannp : Mode.getStandardLevelTypes()) {
         if (start < tannp.b) {
            return Arrays.asList(new TP<>(tannp.a, 1));
         }

         start -= tannp.b;
      }

      return null;
   }

   @Override
   public String serialise() {
      return this.level + "";
   }
}
