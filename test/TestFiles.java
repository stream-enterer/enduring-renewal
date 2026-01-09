package com.tann.dice.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.platform.control.desktop.DesktopControl;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestPlat;
import com.tann.dice.util.Tann;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFiles {
   @Test
   public static void testSoundFiles() {
      List<String> bads = new ArrayList<>();

      for (Field f : Sounds.class.getFields()) {
         if (f.getType().isArray()) {
            try {
               String[] val = (String[])f.get(null);
               if (val.length == 0) {
                  bads.add(f.getName());
               }
            } catch (IllegalAccessException var6) {
               bads.add(f.getName());
               var6.printStackTrace();
            }
         }
      }

      Tann.assertTrue("no bad sounds: " + bads, bads.size() == 0);
   }

   @Test
   @TestPlat(platformClass = DesktopControl.class)
   @SkipNonTann
   public static void testImageFiles() {
      List<String> imagesPaths = new ArrayList<>();

      for (Field f : Images.class.getFields()) {
         if (f.getType() == TextureRegion.class) {
            try {
               TextureRegion tr = (TextureRegion)f.get(null);
               if (tr instanceof AtlasRegion) {
                  AtlasRegion ar = (AtlasRegion)tr;
                  String path = ar.name;
                  imagesPaths.add(path);
               }
            } catch (IllegalAccessException var8) {
               var8.printStackTrace();
            }
         }
      }

      List<String> filePaths = new ArrayList<>();

      for (FileHandle fh : getAll(Gdx.files.internal("C:/code/workspace/Dicegeon/images/2d"))) {
         if (fh.extension().equals("png")) {
            String path = fh.path().substring(37);
            path = path.substring(0, path.length() - 4);
            if (!path.startsWith("3dlink")
               && !path.startsWith("combatEffects")
               && !path.startsWith("portrait")
               && !path.startsWith("patch")
               && !path.startsWith("achievement")
               && !path.startsWith("font")
               && !path.startsWith("item")
               && !path.startsWith("misc")
               && !path.startsWith("lapel2d")
               && !path.startsWith("spell")
               && !path.startsWith("style")
               && !path.startsWith("trigger")
               && !path.startsWith("ui/minimap")) {
               filePaths.add(path);
            }
         }
      }

      filePaths.removeAll(imagesPaths);
      Tann.assertTrue("no unused files: " + filePaths, filePaths.isEmpty());
   }

   @Test
   @TestPlat(platformClass = DesktopControl.class)
   public static void checkUnusedSoundFiles() {
      List<String> javaStrings = new ArrayList<>();

      for (Field f : Sounds.class.getFields()) {
         if (f.getType().isArray()) {
            try {
               String[] val = (String[])f.get(null);
               javaStrings.addAll(Arrays.asList(val));
            } catch (IllegalAccessException var6) {
               var6.printStackTrace();
            }
         }
      }

      List<String> fileStrings = new ArrayList<>();

      for (FileHandle fh : getAll(Gdx.files.internal("sfx"))) {
         fileStrings.add(fh.path());
      }

      fileStrings.removeAll(javaStrings);
      Tann.assertTrue("no unused files: " + fileStrings, fileStrings.isEmpty());
   }

   public static List<FileHandle> getAll(FileHandle parent) {
      List<FileHandle> result = new ArrayList<>();
      getAll(parent, result);
      return result;
   }

   private static void getAll(FileHandle parent, List<FileHandle> partial) {
      if (parent.isDirectory()) {
         for (FileHandle f : parent.list()) {
            getAll(f, partial);
         }
      } else {
         partial.add(parent);
      }
   }
}
