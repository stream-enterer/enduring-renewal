package com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.textureAlg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNLinked;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.image.ImageFilter;
import com.tann.dice.util.tp.TP;

public class TextureAlgorithmTxEcDraw extends TextureAlgorithm {
   TextureAlgorithmTxEcDraw() {
      super(
         new PRNMid("draw"),
         new PRNLinked(
            PipeRegexNamed.ENTITY_OR_ITEM, PipeRegexNamed.COLON, PipeRegexNamed.UP_TO_TWO_DIGITS, PipeRegexNamed.COLON, PipeRegexNamed.UP_TO_TWO_DIGITS
         ),
         "silver pendant:4:8"
      );
   }

   @Override
   public AtlasRegion fromString(AtlasRegion origin, String[] data, String keytag) {
      String decalString = data[0];
      String xString = data[1];
      String yString = data[2];
      if (Tann.isInt(xString) && Tann.isInt(yString)) {
         TextureRegion decal = loadTextureFromContent(decalString);
         if (decal == null) {
            return null;
         } else {
            int x = Integer.parseInt(xString);
            int y = Integer.parseInt(yString);
            Texture t = ImageFilter.stamp(origin, decal, x, y, keytag);
            return new AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
         }
      } else {
         return null;
      }
   }

   public static TextureRegion loadTextureFromContent(String somethingName) {
      Item i = ItemLib.byName(somethingName);
      if (!i.isMissingno()) {
         return i.getImage();
      } else {
         Ability s = AbilityUtils.byName(somethingName);
         if (s != null) {
            return s.getImage();
         } else {
            EntType e = EntTypeUtils.byName(somethingName);
            if (!e.isMissingno()) {
               return e.portrait;
            } else {
               if (FacadeUtils.matches(somethingName)) {
                  TP<String, TextureRegion> res = FacadeUtils.indexedFullData(somethingName.substring(0, 3), Integer.parseInt(somethingName.substring(3)));
                  if (res != null) {
                     return res.b;
                  }
               }

               return null;
            }
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
