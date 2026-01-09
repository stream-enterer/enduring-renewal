package com.tann.dice.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestModding {
   @Test
   @SkipNonTann
   @Slow
   public static void checkMods() {
      checkMods("3point0");
   }

   @Test
   @SkipNonTann
   @Slow
   public static void checkMods2() {
      checkMods("3point1");
   }

   private static void checkMods(String folder) {
      List<String> bad = new ArrayList<>();

      for (FileHandle fileHandle : Gdx.files.absolute("C:\\code\\games\\Dicegeon\\randombits\\modtest\\mod\\" + folder).list()) {
         if (!fileHandle.isDirectory()) {
            String rs = fileHandle.readString();
            rs = rs.replaceAll("`", "").replaceAll("\n", "").replaceAll("\r", "").trim();
            if (rs.startsWith("=")) {
               rs = rs.substring(1);
            }

            String[] mods = rs.split(",");
            List<String> newBads = checkModifiers(mods);
            if (newBads.size() > 0) {
               bad.add(fileHandle.name());
            }

            bad.addAll(newBads);
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   @SkipNonTann
   @Slow
   public static void checkNonworkingMods() {
      List<String> bad = new ArrayList<>();

      for (FileHandle fileHandle : Gdx.files.absolute("C:\\code\\games\\Dicegeon\\randombits\\modtest\\mod\\broken").list()) {
         if (!fileHandle.isDirectory()) {
            String rs = fileHandle.readString();
            rs = rs.replaceAll("`", "").replaceAll("\n", "").replaceAll("\r", "").trim();
            if (rs.startsWith("=")) {
               rs = rs.substring(1);
            }

            String[] mods = rs.split(",");
            List<String> newBads = checkModifiers(mods);
            if (newBads.size() == 0) {
               bad.add(fileHandle.name());
            }
         }
      }

      Tann.assertBads(bad);
   }

   @Test
   @Slow
   public static void checkModifiers() {
      Tann.assertBads(checkModifiers(TestModdingData.MODIFIERS));
   }

   private static List<String> checkModifiers(String[] mods) {
      List<String> bad = new ArrayList<>();

      for (String entireMod : mods) {
         for (String modString : entireMod.split(",")) {
            String err = PasteMode.getPasteErrorGeneric(modString);
            if (err != null) {
               bad.add(err + " -- " + modString);
            }

            Pipe.setupChecks();
            Modifier m = ModifierLib.byName(modString);
            Pipe.disableChecks();
            if (m == null || m.isMissingno()) {
               bad.add(modString);
            }
         }
      }

      return bad;
   }

   @Test
   public static void testDidgyAndGamer() {
      Tann.assertTrue(com.tann.dice.Main.atlas_3d.findRegion("extra/theGreenDigi/AllAlliesPips") != null);
      Tann.assertTrue(com.tann.dice.Main.atlas_3d.findRegion("extra/TheGreenDigi/AllAlliesPips") == null);
      Tann.assertTrue(com.tann.dice.Main.atlas_3d.findRegion("extra/TheBGamer12/fireball") != null);
      Tann.assertTrue(com.tann.dice.Main.atlas_3d.findRegion("extra/theBGamer12/fireball") == null);
   }
}
