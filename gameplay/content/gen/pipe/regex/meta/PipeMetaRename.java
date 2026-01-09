package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceModifier;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class PipeMetaRename<T> extends PipeMeta<T> {
   public PipeMetaRename(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, sourceAlgorithm.srcPart, new PRNMid(getMidTag(sourceAlgorithm)), NAME);
   }

   private static String getMidTag(DataSource ds) {
      return ds instanceof DataSourceModifier ? "mn" : "n";
   }

   @Override
   protected T internalMake(String[] groups) {
      String src = groups[0];
      String renam = groups[1];
      return this.make(this.sourceAlgorithm.makeT(src), renam);
   }

   protected T make(T t, String rename) {
      if (t == null) {
         return null;
      } else if (rename == null || rename.isEmpty()) {
         return null;
      } else {
         return ChoosableUtils.isMissingno(t) ? null : this.sourceAlgorithm.rename(t, rename, t + "." + getMidTag(this.sourceAlgorithm) + "." + rename);
      }
   }

   @Override
   public T example() {
      return this.make(this.exampleBase(), Tann.randomString(8));
   }

   public static <T> List<PipeMetaRename<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaRename<>(sa));
   }

   @Override
   public boolean isComplexAPI() {
      return this.sourceAlgorithm instanceof DataSourceModifier;
   }
}
