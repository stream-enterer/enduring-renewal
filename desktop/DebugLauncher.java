package com.tann.dice.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.tann.dice.desktop.steam.SteamControl;
import com.tann.dice.platform.audio.DefaultSoundHandler;
import com.tann.dice.util.Tann;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DebugLauncher {
   private static final boolean debug = true;
   private static final boolean forcePack = false;

   public static void main(String[] arg) {
      try {
         checkPack("../../images/2d", "misc/imagehash-2d.txt", "2d", false);
         checkPack("../../images/2d_big", "misc/imagehash-2d-big.txt", "2dBig", false);
         checkPack("../../images/3d", "misc/imagehash-3d.txt", "3d", true);
      } catch (Exception var2) {
         var2.printStackTrace();
         System.err.println("Probably running as standalone desktop");
      }

      Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
      config.setForegroundFPS(60);
      config.setIdleFPS(60);
      config.setWindowedMode(Tann.DG_WIDTH, Tann.DG_HEIGHT);
      config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 16);
      config.setTitle("Slice & Dice");
      config.setWindowIcon(FileType.Internal, new String[]{"misc/icon.png"});
      config.setHdpiMode(HdpiMode.Pixels);
      new Lwjgl3Application(new com.tann.dice.Main(new DefaultSoundHandler(), new SteamControl(), false, true), config);
   }

   private static void checkPack(String dir, String file, String outputDir, boolean threeD) {
      int total = hash(new File(dir));
      int c = 0;

      try {
         File f = new File(file);
         if (!f.exists()) {
            PrintWriter pw = new PrintWriter(f);
            pw.print(-1);
            pw.close();
         }

         Scanner s = new Scanner(f);
         c = s.nextInt();
         if (total != c) {
            PrintWriter pw = new PrintWriter(f);
            pw.print(total);
            pw.close();
         }
      } catch (FileNotFoundException var9) {
         var9.printStackTrace();
      }

      if (total != c || new File(outputDir).listFiles().length == 0) {
         packImages(dir, outputDir, threeD);
      }
   }

   private static void ensureSymlinkExists() {
      if (!new File("../../images/2d/3dlink").exists()) {
         throw new RuntimeException("slow packing 3d link should exist");
      }
   }

   private static int hash(File f) {
      int total = 0;
      if (f.isDirectory()) {
         for (File content : f.listFiles()) {
            total += hash(content);
         }
      }

      return (int)(total + f.getName().hashCode() + f.lastModified());
   }

   private static void packImages(String dir, String outputDir, boolean threeD) {
      long start = System.currentTimeMillis();
      System.out.println("packing " + outputDir + "...");
      Settings settings = new Settings();
      settings.silent = true;
      settings.fast = true;
      if (threeD) {
         int size = 1024;
         settings.minWidth = size;
         settings.minHeight = size;
         settings.maxWidth = size;
         settings.maxHeight = size;
         settings.paddingX = 1;
         settings.paddingY = 1;
         settings.combineSubdirectories = true;
         settings.filterMag = TextureFilter.MipMapLinearLinear;
         settings.filterMin = TextureFilter.MipMapLinearLinear;
         TexturePacker.process(settings, dir, outputDir, "atlas_image");
      } else {
         settings.combineSubdirectories = true;
         int max = 1024;
         if (dir.contains("big")) {
            max = 2048;
         }

         settings.maxWidth = max;
         settings.maxHeight = max;
         settings.filterMag = TextureFilter.Nearest;
         settings.filterMin = TextureFilter.Nearest;
         TexturePacker.process(settings, dir, outputDir, "atlas_image");
      }

      System.out.println("done, took " + (System.currentTimeMillis() - start) + "ms");
   }
}
