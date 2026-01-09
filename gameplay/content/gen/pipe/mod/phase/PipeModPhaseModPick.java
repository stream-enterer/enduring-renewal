package com.tann.dice.gameplay.content.gen.pipe.mod.phase;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPickAdvanced;
import com.tann.dice.util.Tann;

public class PipeModPhaseModPick extends PipeRegexNamed<Modifier> {
   private static final PRNPart pref = new PRNPref("phmp");

   public PipeModPhaseModPick() {
      super(pref, TWO_DIGIT_DELTA);
   }

   protected Modifier internalMake(String[] groups) {
      String index = groups[0];
      return !Tann.isInt(index) ? null : this.create(Integer.parseInt(index));
   }

   private Modifier create(int value) {
      PhaseGeneratorModifierPickAdvanced pgmp = new PhaseGeneratorModifierPickAdvanced(10, value, ModifierPickContext.Dificulty_Allow_T1);
      return new Modifier(pref.toString() + value, new GlobalAddPhase(pgmp));
   }

   public Modifier example() {
      return this.create(Tann.randomInt(-30, 30));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
