package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.util.Tann;

public class PipeMetaRandomTier<T extends Choosable> extends PipeMeta<T> {
   public PipeMetaRandomTier(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, sourceAlgorithm.getMRTPart(), PipeMetaSetTier.getTierFromData(sourceAlgorithm));
   }

   protected T internalMake(String[] groups) {
      String tStr = groups[0];
      return !Tann.isInt(tStr) ? null : this.make(Integer.parseInt(tStr));
   }

   protected T make(int tier) {
      for (int i = 0; i < 5000; i++) {
         T t = this.example();
         if (t.getTier() == tier) {
            return t;
         }
      }

      return null;
   }

   public T example() {
      return this.exampleBase();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   @Override
   public boolean isTransformative() {
      return true;
   }
}
