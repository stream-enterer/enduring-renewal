package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmPalette extends TextureAlgorithm {
   TextureAlgorithmPalette() {
      super(
         new PRNMid("p"),
         new PRNLinked(PipeRegexNamed.COLOUR, PipeRegexNamed.prnS(":"), PipeRegexNamed.COLOUR, PipeRegexNamed.prnS(":"), PipeRegexNamed.UP_TO_TWO_DIGITS),
         "aaa:a0a:60",
         "000:fff:30"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String colFrom = data[0];
      String colTo = data[1];
      String thresh = data[2];
      if (!Tann.isInt(thresh)) {
         return null;
      } else {
         Color from = Colours.fromHex(colFrom);
         Color to = Colours.fromHex(colTo);
         if (from != null && to != null) {
            Texture t = ImageFilter.paletteSwap(origin, from, to, Integer.parseInt(thresh) / 100.0F, keytag);
            return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
         } else {
            return null;
         }
      }
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0] + ":" + extra[1] + ":" + extra[2];
   }

   @Override
   public boolean skipAPI() {
      return true;
   }
}
