package com.tann.dice.gameplay.content.gen.pipe.mod.phase;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.util.Tann;

public class PipeModPhaseIndexed extends PipeRegexNamed<Modifier> {
   private static final PRNPart pref = new PRNPref("phi");

   public PipeModPhaseIndexed() {
      super(pref, DIGIT);
   }

   protected Modifier internalMake(String[] groups) {
      String index = groups[0];
      return !Tann.isInt(index) ? null : this.create(Integer.parseInt(index));
   }

   private Modifier create(int index) {
      PhaseGenerator pg = PhaseGenerator.indexed(index);
      return pg == null ? null : new Modifier(pref.toString() + index, new GlobalAddPhase(pg));
   }

   public Modifier example() {
      return this.create(Tann.randomInt(9));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
