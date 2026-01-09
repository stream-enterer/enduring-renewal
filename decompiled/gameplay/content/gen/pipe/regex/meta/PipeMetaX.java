package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceMonster;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class PipeMetaX<T> extends PipeMeta<T> {
   public PipeMetaX(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, prnS("x"), DIGIT_2_9, prnS("\\."), sourceAlgorithm.srcPart);
   }

   public static <T> List<PipeMetaX<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaX<>(sa));
   }

   @Override
   protected T internalMake(String[] groups) {
      String level = groups[0];
      String name = groups[1];
      return !Tann.isInt(level) ? null : this.make(Integer.parseInt(level), this.sourceAlgorithm.makeT(name));
   }

   protected T make(int multiplier, T t) {
      if (t == null) {
         return null;
      } else {
         return ChoosableUtils.isMissingno(t) ? null : this.sourceAlgorithm.upscale(t, multiplier);
      }
   }

   @Override
   public T example() {
      return this.make(Tann.randomInt(2, 9), this.exampleBase());
   }

   @Override
   public boolean isTransformative() {
      return true;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return this.sourceAlgorithm instanceof DataSourceMonster;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.5F;
   }

   @Override
   protected T generateInternal(boolean wild) {
      return this.make(Tann.randomInt(2, 5), this.exampleBase());
   }
}
