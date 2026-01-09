package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosableRange;
import com.tann.dice.gameplay.trigger.global.choosable.GlobalGainChoosable;
import com.tann.dice.util.Tann;

public class PipeModGainChoosable extends PipeRegexNamed<Modifier> {
   private static final String PREF = "ch";

   public PipeModGainChoosable() {
      super(new PRNPref("ch"), CHOOSABLE);
   }

   public Modifier example() {
      return this.make(new RandomTieredChoosableRange(2, 3 + Tann.randomInt(5), 1, ChoosableType.Hero));
   }

   protected Modifier internalMake(String[] groups) {
      Choosable ch = ChoosableUtils.deserialise(groups[0], null);
      return ch != null && !ChoosableUtils.isMissingno(ch) ? this.make(ch) : null;
   }

   private Modifier make(Choosable ch) {
      return new Modifier("ch." + ChoosableUtils.fullSerialise(ch), new GlobalGainChoosable(ch));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
