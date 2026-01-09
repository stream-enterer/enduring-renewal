package com.tann.dice.statics;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtils {
   public static final List<AtlasRegion> genImages = Tann.getRegionsStartingWith("portrait/hero/special/generated/");
   public static Map<TextureRegion, TextureRegion> cachedMap = new HashMap<>();

   public static void clearCaches() {
      cachedMap = new HashMap<>();
   }

   public static TextureRegion get2DIfPossible(TextureRegion tex) {
      if (OptionLib.DISABLE_2D_3D_IMAGE.c()) {
         return tex;
      } else {
         TextureRegion result = cachedMap.get(tex);
         if (result != null) {
            return result;
         } else {
            if (tex instanceof AtlasRegion) {
               AtlasRegion ar = (AtlasRegion)tex;
               if (ar.name.contains("extra")) {
                  return tex;
               }

               TextureRegion tmp = loadExtNull("3dlink/" + ar.name);
               if (tmp != null) {
                  cachedMap.put(tex, tmp);
                  return tmp;
               }

               TannLog.error("missing linked for " + ar.name);
            }

            TannLog.error("failed to get 2d for " + tex);
            cachedMap.put(tex, tex);
            return tex;
         }
      }
   }

   public static AtlasRegion loadArExt(String s) {
      TextureRegion tr = loadExt(s);
      return tr instanceof AtlasRegion ? (AtlasRegion)tr : null;
   }

   public static TextureRegion loadExtNull(String regionName) {
      return com.tann.dice.Main.atlas.findRegion(regionName);
   }

   public static TextureRegion loadExt(String name) {
      return loadExt(com.tann.dice.Main.atlas, name);
   }

   public static TextureRegion loadExtBig(String name) {
      return loadExt(com.tann.dice.Main.atlas_big, name);
   }

   public static TextureRegion loadExt3d(String name) {
      return loadExt(com.tann.dice.Main.atlas_3d, name);
   }

   private static TextureRegion loadExt(TextureAtlas atlas, String name) {
      return Images.load(atlas, name);
   }

   public static TextureRegion loadExt3dNull(String s) {
      return com.tann.dice.Main.atlas_3d.findRegion(s);
   }
}
