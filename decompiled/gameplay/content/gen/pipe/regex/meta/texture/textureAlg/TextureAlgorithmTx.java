package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmTx extends TextureAlgorithm {
   TextureAlgorithmTx() {
      super(
         new PRNMid("img"),
         PipeRegexNamed.TEX,
         "322c1g2jgc2l4000lpt=XK8tCbkeg3gdg2w1iag8g8hw4i9gz2gahwNxcagQwc9gPAg8PBh8gMhBg8h0hAh8jAh8g2gzh9g2hwiaj0ibg0hcc9j3j7j5j6g0gf0g0gf1"
      );
   }

   public static AtlasRegion getFrom(Texture t) {
      return t == null ? null : new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String s = data[0];
      if (s == null) {
         return null;
      } else {
         Texture t = ImageFilter.makeFrom64RLE(s);
         return t == null ? null : new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
      }
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0];
   }

   @Override
   public boolean autoSplit() {
      return false;
   }
}
