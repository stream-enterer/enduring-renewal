package com.tann.dice.gameplay.context.config.misc;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.StartConfigButton;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InstantConfig extends ContextConfig {
   public final int handicap;

   public InstantConfig(int handicap) {
      super(Mode.INSTANT);
      this.handicap = handicap;
   }

   public InstantConfig(String serial) {
      this(Integer.valueOf(serial));
   }

   @Override
   public String getSpecificKey() {
      return "instant-" + this.handicap;
   }

   @Override
   public String serialise() {
      return "" + this.handicap;
   }

   @Override
   public DungeonContext makeContext(AntiCheeseRerollInfo acri) {
      int lowBound = Math.max(1, this.handicap);
      int upBound = 20;
      int level = (int)(lowBound + Math.random() * (upBound - lowBound));
      int partyLevel = level - this.handicap;
      return new DungeonContext(this, Party.generate(partyLevel), level);
   }

   @Override
   public StandardButton makeStartButton(boolean big) {
      return StartConfigButton.make(
         "[notranslate]" + TextWriter.getTag(this.mode.getColour()) + com.tann.dice.Main.t("Start"),
         TextWriter.getTag(this.getColour()) + Tann.delta(this.handicap)
      );
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
   }

   @Override
   protected boolean offerStandardRewards() {
      return false;
   }

   @Override
   protected boolean offerChanceEvents() {
      return false;
   }

   public Color getColour() {
      switch (this.handicap) {
         case 0:
            return Colours.green;
         case 1:
         case 2:
         case 4:
         case 5:
         case 7:
         case 8:
         default:
            return Colours.pink;
         case 3:
            return Colours.yellow;
         case 6:
            return Colours.red;
         case 9:
            return Colours.purple;
      }
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
   public String describeConfig() {
      return TextWriter.getTag(this.getColour()) + Tann.delta(this.handicap);
   }
}
