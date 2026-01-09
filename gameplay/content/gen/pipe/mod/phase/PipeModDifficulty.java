package com.tann.dice.gameplay.content.gen.pipe.mod.phase;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.util.Tann;

public class PipeModDifficulty extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("diff");

   public PipeModDifficulty() {
      super(PREF, DIFFICULTY);
   }

   protected Modifier internalMake(String[] groups) {
      return this.create(Difficulty.fromString(groups[0]));
   }

   private Modifier create(Difficulty d) {
      return d != null && d != Difficulty.Normal
         ? new Modifier(PREF.toString() + d, new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorDifficulty(d))))
         : null;
   }

   public Modifier example() {
      return this.create(Tann.randomExcept(Difficulty.values(), Difficulty.Normal));
   }

   @Override
   public boolean isComplexAPI() {
      return false;
   }
}
