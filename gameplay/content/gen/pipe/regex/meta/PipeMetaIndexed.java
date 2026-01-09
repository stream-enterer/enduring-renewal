package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceHero;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceItem;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceModifier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceMonster;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import java.util.Arrays;
import java.util.List;

public class PipeMetaIndexed<T> extends PipeMeta<T> {
   final DataSource sa;
   static final String HR = "z";
   static final String MN = "q";
   static final String IT = "u";
   static final String MOD = "j";

   protected PipeMetaIndexed(DataSource<T> sourceAlgorithm) {
      super(sourceAlgorithm, getStart(sourceAlgorithm), UP_TO_THREE_B64);
      this.sa = sourceAlgorithm;
   }

   private static PRNPart getStart(DataSource ds) {
      if (ds instanceof DataSourceItem) {
         return prnS("u");
      } else if (ds instanceof DataSourceModifier) {
         return prnS("j");
      } else if (ds instanceof DataSourceHero) {
         return prnS("z");
      } else if (ds instanceof DataSourceMonster) {
         return prnS("q");
      } else {
         throw new RuntimeException("PRNPart error: " + ds.getClass().getSimpleName());
      }
   }

   public static <T> List<PipeMetaIndexed<T>> makeAll(DataSource<T> sa) {
      return Arrays.asList(new PipeMetaIndexed<>(sa));
   }

   public static String tinyName(EntType et) {
      if (et instanceof HeroType) {
         return tinyName((HeroType)et);
      } else {
         return et instanceof MonsterType ? tinyName((MonsterType)et) : "??DFS?SD??";
      }
   }

   public static String tinyName(HeroType ht) {
      int index = DataSourceHero.reverseIndex(ht);
      return makeTiny("z", index);
   }

   public static String tinyName(MonsterType mt) {
      int index = DataSourceMonster.reverseIndex(mt);
      return makeTiny("q", index);
   }

   private static String makeTiny(String start, int index) {
      return index == -1 ? null : start + GenUtils.b64(index);
   }

   public static String tinyName(Item it) {
      return makeTiny("u", ItemLib.getMasterCopy().indexOf(it));
   }

   public static String tinyName(Modifier m) {
      return makeTiny("j", ModifierLib.getAll().indexOf(m));
   }

   @Override
   public T example() {
      return (T)this.sa.exampleBase();
   }

   @Override
   protected T internalMake(String[] groups) {
      String index = groups[0];
      long val = GenUtils.b64(index);
      return this.sourceAlgorithm.makeIndexed(val);
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
