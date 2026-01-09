package com.tann.dice.gameplay.battleTest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum Difficulty implements Unlockable {
   Heaven(Colours.light, 2, 20),
   Easy(Colours.green, 2, 5),
   Normal(Colours.yellow, 3, 0),
   Hard(Colours.orange, 3, -4),
   Unfair(Colours.red, 4, -10),
   Brutal(Colours.purple, 5, -20),
   Hell(Colours.pink, 6, -40);

   private final Color color;
   private final int maxMonsterTypes;
   private final PhaseGenerator phaseGenerator;
   private final int targetModifierValue;

   private Difficulty(Color color, int maxMonsterTypes, int targetModifierValue) {
      this.color = color;
      this.maxMonsterTypes = maxMonsterTypes;
      this.targetModifierValue = targetModifierValue;
      this.phaseGenerator = new PhaseGeneratorDifficulty(this);
   }

   public static String getOrHarderString(Difficulty difficulty) {
      return difficulty.getColourTaggedName() + " or harder";
   }

   public static boolean equalOrHarderThan(Difficulty test, Difficulty target) {
      return test != null && target != null ? Tann.indexOf(values(), test) >= Tann.indexOf(values(), target) : false;
   }

   public static Difficulty fromString(String diff) {
      Difficulty[] vals = values();

      for (int i = 0; i < vals.length; i++) {
         Difficulty d = vals[i];
         if (d.name().equalsIgnoreCase(diff)) {
            return d;
         }
      }

      return null;
   }

   public String getColourTaggedName() {
      return TextWriter.getTag(this.color) + this.name() + "[cu]";
   }

   public Collection<Global> getGlobals() {
      List<Global> result = new ArrayList<>();
      if (this == Brutal || this == Hell) {
         result.add(
            new GlobalLevelRequirement(
               new LevelRequirementFirst(),
               new GlobalAddPhase(
                  new PhaseGeneratorHardcoded(
                     new MessagePhase(
                        "[red]Beware![n][cu]This is not intended to be possible, I'm just having fun. Maybe it's beatable on some game modes though."
                     )
                  )
               )
            )
         );
      }

      result.add(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(this.phaseGenerator)));
      return result;
   }

   public String getRules() {
      switch (this) {
         case Heaven:
            return "Start with a bunch of [green]blessings[cu], total value " + this.getTargetModifierValue();
         case Easy:
            return "Start with a tier " + this.getTargetModifierValue() + " [green]blessing[cu]";
         case Normal:
            return "Start with a [yellow]tweak";
         case Hard:
            return "Start with a tier " + this.getTargetModifierValue() + " [purple]curse[cu]";
         case Unfair:
         case Brutal:
         case Hell:
            return "Start with a bunch of [purple]curses[cu], total value " + this.getTargetModifierValue();
         default:
            return "unset";
      }
   }

   public Color getColor() {
      return this.color;
   }

   public int getTargetModifierValue() {
      return this.getTargetModifierValue(false);
   }

   public int getTargetModifierValue(boolean base) {
      return base ? this.targetModifierValue : OptionUtils.getRealModifierAddUpTo(this.targetModifierValue);
   }

   public int getMaxMonsterTypes() {
      return this.maxMonsterTypes;
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return new TextWriter(" " + this.getColourTaggedName() + " ");
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return TextWriter.getTag(this.color) + "D";
   }

   public String getTopChoiceText() {
      return TextWriter.getTag(this.getColor()) + "(" + this.name() + ")[cu]";
   }

   public int getBaseAmt() {
      switch (this) {
         case Heaven:
         case Brutal:
         case Hell:
            return 10;
         case Easy:
         case Hard:
            return 4;
         case Normal:
            return 3;
         case Unfair:
            return 7;
         default:
            return 1;
      }
   }

   public boolean singlePick() {
      return Math.abs(this.targetModifierValue) < 8 || this.getBaseAmt() == 0;
   }
}
