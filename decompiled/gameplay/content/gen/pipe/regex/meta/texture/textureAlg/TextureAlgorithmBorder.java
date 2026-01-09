package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Colours;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmBorder extends TextureAlgorithm {
   TextureAlgorithmBorder() {
      super(new PRNMid("b"), PipeRegexNamed.COLOUR, "0f0", "f0f");
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String col = data[0];
      Color c = Colours.fromHex(col);
      return c == null ? null : DataSource.wrap(new TextureRegion(ImageFilter.border(origin, c, keytag)));
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0];
   }
}
