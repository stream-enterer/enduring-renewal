package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import java.util.Arrays;
import java.util.List;

public class PipeMetaBracketed<T> extends PipeMeta<T> {
   public PipeMetaBracketed(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, BRACKET_LEFT, sourceAlgorithm.srcPart, BRACKET_RIGHT);
   }

   @Override
   protected T internalMake(String[] groups) {
      return this.make(this.sourceAlgorithm.makeT(groups[0]));
   }

   protected T make(T t) {
      String rename = "(" + t + ")";
      return ChoosableUtils.isMissingno(t) ? null : this.sourceAlgorithm.renameUnderlying(t, rename);
   }

   @Override
   public T example() {
      return this.make(this.exampleBase());
   }

   public static <T> List<PipeMetaBracketed<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaBracketed<>(sa));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
