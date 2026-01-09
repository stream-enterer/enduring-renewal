package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMeta;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg.TextureAlgorithm;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeMetaTexture<T> extends PipeMeta<T> {
   private final TextureAlgorithm textureAlgorithm;

   public PipeMetaTexture(DataSource sourceAlgorithm, TextureAlgorithm textureAlgorithm) {
      super(sourceAlgorithm, sourceAlgorithm.srcPart, textureAlgorithm.sep, textureAlgorithm.prnPart);
      this.textureAlgorithm = textureAlgorithm;
   }

   @Override
   protected T internalMake(String[] groups) {
      String obj = groups[0];
      String[] extra = new String[groups.length - 1];
      System.arraycopy(groups, 1, extra, 0, groups.length - 1);
      T t = this.makeT(obj);
      if (ChoosableUtils.isMissingno(t)) {
         return null;
      } else {
         AtlasRegion ar = this.getImage(t);
         String keytag = this.textureAlgorithm.getClass().getSimpleName() + ":" + Tann.arrayToString(groups, "|");
         ar = this.textureAlgorithm.fromString(this.getImage(t), extra, keytag);
         return ar == null ? null : this.combine(t, ar, this.getName(t) + this.textureAlgorithm.sep + this.textureAlgorithm.getSuffix(extra));
      }
   }

   private AtlasRegion getImage(T t) {
      return this.sourceAlgorithm.getImage(t);
   }

   @Override
   public T example() {
      T t = this.exampleBase();
      String[] extra = this.textureAlgorithm.getExampleTex();
      String[] all = new String[extra.length + 1];
      System.arraycopy(extra, 0, all, 1, extra.length);
      all[0] = t.toString();
      return this.internalMake(all);
   }

   public static <T> List<PipeMetaTexture<T>> makeAll(DataSource<T> sa) {
      List<PipeMetaTexture<T>> result = new ArrayList<>();

      for (TextureAlgorithm value : TextureAlgorithm.makeAll()) {
         result.add(new PipeMetaTexture<>(sa, value));
      }

      return result;
   }

   @Override
   public boolean isSlow() {
      return true;
   }

   @Override
   public boolean isTexturey() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return this.textureAlgorithm.skipAPI();
   }
}
