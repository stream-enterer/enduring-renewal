package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;

public class TextureAlgorithmTxEc extends TextureAlgorithm {
   TextureAlgorithmTxEc() {
      super(new PRNMid("img"), new PRNLinked(PipeRegexNamed.ENTITY_OR_ITEM), "thief");
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String cString = data[0];
      TextureRegion decal = TextureAlgorithmTxEcDraw.loadTextureFromContent(cString);
      return decal == null ? null : new AtlasRegion(decal);
   }

   @Override
   public String getSuffix(String[] extra) {
      return extra[0];
   }
}
