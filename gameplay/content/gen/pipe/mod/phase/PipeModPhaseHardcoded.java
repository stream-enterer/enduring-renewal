package com.tann.dice.gameplay.content.gen.pipe.mod.phase;

import com.badlogic.gdx.utils.SerializationException;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MissingnoPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;

public class PipeModPhaseHardcoded extends PipeRegexNamed<Modifier> {
   private static final PRNPart pref = new PRNPref("ph");

   public PipeModPhaseHardcoded() {
      super(pref, PHASE_STRING);
   }

   protected Modifier internalMake(String[] groups) {
      String phs = groups[0];
      return this.create(phs);
   }

   private Modifier create(String phs) {
      if (phs.contains(",")) {
         return null;
      } else {
         try {
            Phase p = Phase.deserialise(phs, true);
            return !(p instanceof MissingnoPhase) && !p.isInvalid() ? new Modifier(pref + phs, new GlobalAddPhase(new PhaseGeneratorHardcoded(p))) : null;
         } catch (SerializationException var3) {
            TannLog.error("SerEx creating phase from " + phs);
            var3.printStackTrace();
            return null;
         }
      }
   }

   public Modifier example() {
      return this.create("4" + Tann.randomString(20));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
