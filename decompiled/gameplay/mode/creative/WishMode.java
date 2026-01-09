package com.tann.dice.gameplay.mode.creative;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalWishEnabled;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WishMode extends Mode {
   public WishMode() {
      super("Wish");
   }

   public static void makeWishSound() {
   }

   public static Modifier makeGenie() {
      return ModifierLib.byName("add.Banshee.hsv.65:20:20.n.GENIE.mn.Angry Genie");
   }

   @Override
   public Color getColour() {
      return Colours.light;
   }

   @Override
   public String[] getDescriptionLines() {
      String t = com.tann.dice.Main.self().control.getSelectTapString();
      return new String[]{
         "You can wish between fights:",
         "[grey]items[cu]: " + t.toLowerCase() + " inventory, then [grey]'items'",
         "[yellow]levelups[cu]: " + t.toLowerCase() + " inventory, then a hero's portrait",
         "[purple]modifiers[cu]: " + t.toLowerCase() + " the modifier list",
         com.tann.dice.Main.self().control.getInfoTapString().toLowerCase() + " 'end turn' or 'continue' to skip a fight"
      };
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new WishMode.WishConfig());
   }

   @Override
   public String getSaveKey() {
      return "wish";
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.creative;
   }

   public String wishFor(String text) {
      return this.wishFor(text, Colours.text);
   }

   public String wishFor(String text, Color col) {
      return "wish for " + Words.aOrAn(text);
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   public static class WishConfig extends ContextConfig {
      public WishConfig() {
         super(Mode.WISH);
      }

      @Override
      public List<Global> getSpecificModeGlobals() {
         List<Global> result = new ArrayList<>();
         result.add(new GlobalWishEnabled());
         return result;
      }

      @Override
      public Collection<Global> getSpecificModeAddPhases() {
         Collection<Global> result = super.getSpecificModeAddPhases();
         result.add(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
         return result;
      }
   }
}
