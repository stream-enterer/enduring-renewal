package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Separators;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class PipeMetaDocument<T> extends PipeMeta<T> {
   public PipeMetaDocument(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, sourceAlgorithm.srcPart, new PRNMid(getMidTag(sourceAlgorithm)), RICH_TEXT);
   }

   private static String getMidTag(DataSource ds) {
      return "doc";
   }

   @Override
   protected T internalMake(String[] groups) {
      String src = groups[0];
      String renam = groups[1];
      return this.make(this.sourceAlgorithm.makeT(src), renam);
   }

   protected T make(T t, String doc) {
      if (t == null || doc == null || doc.isEmpty()) {
         return null;
      } else if (Separators.bannedFromDocument(doc)) {
         return null;
      } else {
         return ChoosableUtils.isMissingno(t) ? null : this.sourceAlgorithm.document(t, doc, t + "." + getMidTag(this.sourceAlgorithm) + "." + doc);
      }
   }

   @Override
   public T example() {
      return this.make(this.exampleBase(), Tann.randomString(8));
   }

   public static <T> List<PipeMetaDocument<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaDocument<>(sa));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
