package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public abstract class TextureAlgorithm {
   private final String[] EXAMPLE_DATA;
   public final PRNPart prnPart;
   public final PRNPart sep;

   TextureAlgorithm(PRNPart sep, PRNPart imageData, String... example_data) {
      this.EXAMPLE_DATA = example_data;
      this.prnPart = imageData;
      this.sep = sep;
   }

   public abstract AtlasRegion fromString(AtlasRegion var1, String[] var2, String var3);

   public String[] getExampleTex() {
      return this.autoSplit()
         ? Tann.random(this.EXAMPLE_DATA).replaceAll(":", "#").replaceAll("~", ":").split("#")
         : new String[]{Tann.random(this.EXAMPLE_DATA)};
   }

   public abstract String getSuffix(String[] var1);

   public static List<TextureAlgorithm> makeAll() {
      return Arrays.asList(
         new TextureAlgorithmTx(),
         new TextureAlgorithmTxDraw(),
         new TextureAlgorithmTxEc(),
         new TextureAlgorithmTxEcDraw(),
         new TextureAlgorithmHue(),
         new TextureAlgorithmHSL(),
         new TextureAlgorithmBorder(),
         new TextureAlgorithmRect(),
         new TextureAlgorithmTargetedHue(),
         new TextureAlgorithmPalette()
      );
   }

   public boolean autoSplit() {
      return true;
   }

   public boolean skipAPI() {
      return false;
   }
}
