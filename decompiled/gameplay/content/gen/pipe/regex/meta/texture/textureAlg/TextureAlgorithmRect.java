package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Colours;
import com.tann.dice.util.image.ImageFilter;

public class TextureAlgorithmRect extends TextureAlgorithm {
   TextureAlgorithmRect() {
      super(
         new PRNMid("rect"),
         new PRNLinked(PipeRegexNamed.RECT, PipeRegexNamed.COLON, PipeRegexNamed.COLOUR),
         "01:01:10:10:fff",
         "01:01:10:10:0ff",
         "01:01:10:10:4b7"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String xStr = data[0];
      String yStr = data[1];
      String wStr = data[2];
      String hStr = data[3];
      String colStr = data[4];
      if (PipeRegexNamed.badInt(xStr, yStr, wStr, hStr)) {
         return null;
      } else {
         Color col = Colours.fromHex(colStr);
         if (col == null) {
            return null;
         } else {
            Texture t = ImageFilter.rect(origin, Integer.parseInt(xStr), Integer.parseInt(yStr), Integer.parseInt(wStr), Integer.parseInt(hStr), col, keytag);
            return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
         }
      }
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0] + extra[1] + extra[2] + extra[3] + ":" + extra[4];
   }

   @Override
   public boolean skipAPI() {
      return true;
   }
}
