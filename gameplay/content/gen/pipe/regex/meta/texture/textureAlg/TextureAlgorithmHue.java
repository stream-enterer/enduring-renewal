package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;

public class TextureAlgorithmHue extends TextureAlgorithm {
   TextureAlgorithmHue() {
      super(new PRNMid("hue"), PipeRegexNamed.TWO_DIGIT_DELTA, "40", "-20", "90");
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String h = data[0];
      return !Tann.isInt(h) ? null : TextureAlgorithmHSL.makeAr(origin, Integer.parseInt(h), 0, 0, keytag);
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0];
   }
}
