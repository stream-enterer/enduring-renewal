package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.util.Tann;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmHSL extends TextureAlgorithm {
   TextureAlgorithmHSL() {
      super(
         PipeRegexNamed.HSL,
         new PRNLinked(
            PipeRegexNamed.TWO_DIGIT_DELTA, PipeRegexNamed.COLON, PipeRegexNamed.TWO_DIGIT_DELTA, PipeRegexNamed.COLON, PipeRegexNamed.TWO_DIGIT_DELTA
         ),
         "40:0:0",
         "0:-40:0",
         "0:30:0"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String h = data[0];
      String s = data[1];
      String l = data[2];
      return Tann.isInt(h) && Tann.isInt(s) && Tann.isInt(l) ? makeAr(origin, Integer.parseInt(h), Integer.parseInt(s), Integer.parseInt(l), keytag) : null;
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0] + ":" + extra[1] + ":" + extra[2];
   }

   public static AtlasRegion makeAr(AtlasRegion ht, int hue, int sat, int lightness, String keytag) {
      Texture t = ImageFilter.hslDelta(ht, hue, sat, lightness, keytag);
      return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
   }

   @Override
   public boolean skipAPI() {
      return true;
   }
}
