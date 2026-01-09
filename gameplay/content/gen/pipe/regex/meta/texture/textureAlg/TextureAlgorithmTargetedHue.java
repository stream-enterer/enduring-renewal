package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Colours;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmTargetedHue extends TextureAlgorithm {
   TextureAlgorithmTargetedHue() {
      super(
         new PRNMid("thue"),
         new PRNLinked(PipeRegexNamed.COLOUR, PipeRegexNamed.COLON, PipeRegexNamed.EXACTLY_TWO_DIGITS, PipeRegexNamed.COLON, PipeRegexNamed.TWO_DIGIT_DELTA),
         "aaa:30:30"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String colStr = data[0];
      String threshStr = data[1];
      String hStr = data[2];
      Color col = Colours.fromHex(colStr);
      return makeAr(origin, col, Float.parseFloat(threshStr) / 100.0F, Integer.parseInt(hStr), 0, 0, keytag);
   }

   public static AtlasRegion makeAr(AtlasRegion ht, Color pickedCol, float threshold, int hue, int sat, int lightness, String keytag) {
      Texture t = ImageFilter.hslDeltaPicked(ht, pickedCol, threshold, hue, sat, lightness, keytag);
      return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
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
