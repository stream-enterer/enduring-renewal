package com.tann.dice.gameplay.content.gen.pipe.regex.meta;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import javax.annotation.Nonnull;

public abstract class PipeMeta<T> extends PipeRegexNamed<T> {
   protected final DataSource<T> sourceAlgorithm;

   protected PipeMeta(DataSource<T> sourceAlgorithm, PRNPart... parts) {
      super(parts);
      this.sourceAlgorithm = sourceAlgorithm;
   }

   protected T combine(T t, AtlasRegion ar, String realName) {
      return this.sourceAlgorithm.combine(t, ar, realName);
   }

   @Nonnull
   protected T makeT(String name) {
      return this.sourceAlgorithm.makeT(name);
   }

   protected T exampleBase() {
      return this.sourceAlgorithm.exampleBase();
   }

   protected String getName(T t) {
      return t == null ? "ach null" : t.toString();
   }
}
