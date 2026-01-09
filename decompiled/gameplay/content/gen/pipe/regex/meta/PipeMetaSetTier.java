package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceModifier;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class PipeMetaSetTier<T> extends PipeMeta<T> {
   public static PRNPart getTierFromData(DataSource source) {
      return UP_TO_THREE_DIGITS_TIER;
   }

   private static String getMidTag(DataSource ds) {
      return ds instanceof DataSourceModifier ? "modtier" : "tier";
   }

   public PipeMetaSetTier(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, sourceAlgorithm.srcPart, new PRNMid(getMidTag(sourceAlgorithm)), getTierFromData(sourceAlgorithm));
   }

   @Override
   protected T internalMake(String[] groups) {
      String src = groups[0];
      String tier = groups[1];
      return this.make(this.sourceAlgorithm.makeT(src), Integer.parseInt(tier));
   }

   protected T make(T t, int newTier) {
      if (t == null) {
         return null;
      } else {
         return ChoosableUtils.isMissingno(t) ? null : this.sourceAlgorithm.retier(t, newTier, t + "." + getMidTag(this.sourceAlgorithm) + "." + newTier);
      }
   }

   @Override
   public T example() {
      return this.make(this.exampleBase(), Tann.randomInt(9));
   }

   public static <T extends Choosable> List<PipeMetaSetTier<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaSetTier<>(sa));
   }
}
