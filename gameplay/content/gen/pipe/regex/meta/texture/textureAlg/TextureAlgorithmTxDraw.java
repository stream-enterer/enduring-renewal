package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;
import com.tann.dice.util.image.ImageFilter;
import com.tann.dice.util.image.Img64;

public class TextureAlgorithmTxDraw extends TextureAlgorithm {
   static final int numParts = 1;

   TextureAlgorithmTxDraw() {
      super(
         new PRNMid("draw"),
         new PRNLinked(PipeRegexNamed.TEX, PipeRegexNamed.COLON, PipeRegexNamed.UP_TO_TWO_DIGITS, PipeRegexNamed.COLON, PipeRegexNamed.UP_TO_TWO_DIGITS),
         "322c1g2jgc2l4000lpt=XK8tCbkeg3gdg2w1iag8g8hw4i9gz2gahwNxcagQwc9gPAg8PBh8gMhBg8h0hAh8jAh8g2gzh9g2hwiaj0ibg0hcc9j3j7j5j6g0gf0g0gf1"
               .replaceAll(":", "~")
            + ":5:5"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String string64 = getColonned(data, 1, 0);
      AtlasRegion ar = TextureAlgorithmTx.getFrom(Img64.fromStringCached(string64));
      if (ar == null) {
         return null;
      } else {
         String xString = data[1];
         String yString = data[2];
         if (Tann.isInt(xString) && Tann.isInt(yString)) {
            int x = Integer.parseInt(xString);
            int y = Integer.parseInt(yString);
            Texture t = ImageFilter.stamp(origin, ar, x, y, keytag);
            return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
         } else {
            return null;
         }
      }
   }

   public static String getColonned(String[] data, int numParts, int offset) {
      if (numParts + offset > data.length) {
         return "err";
      } else {
         String string64 = "";

         for (int i = 0; i < numParts; i++) {
            string64 = string64 + data[i + offset];
            if (i < numParts - 1) {
               string64 = string64 + ":";
            }
         }

         return string64;
      }
   }

   @Override
   public String getSuffix(String[] extra) {
      return getColonned(extra, extra.length, 0);
   }

   @Override
   public boolean skipAPI() {
      return true;
   }
}
