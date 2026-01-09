package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ScreenshotGenerator {
   private static Map<Runnable, Float> pendingRunnables = new HashMap<>();
   private static int screenshotNumber = 0;

   public static void tick(float delta) {
      Map<Runnable, Float> clones = new HashMap<>();
      clones.putAll(pendingRunnables);

      for (Runnable r : new HashSet<>(clones.keySet())) {
         float newValue = clones.get(r) - delta;
         if (newValue <= 0.0F) {
            r.run();
            clones.remove(r);
         } else {
            clones.put(r, newValue);
         }
      }

      pendingRunnables = clones;
   }

   public void generate() {
      FileHandle handle = Gdx.files.local("../../randombits/screenshots/specs.json");
      String contents = handle.readString();
      JsonValue fromJson = new JsonReader().parse(contents);
      JsonValue sizes = fromJson.get("sizes");

      for (int i = 0; i < sizes.size; i++) {
         final String size = sizes.getString(i);
         System.out.println(size);
         String[] parts = size.split("x");
         final int width = Integer.valueOf(parts[0]);
         final int height = Integer.valueOf(parts[1]);
         final DisplayMode dm = Gdx.graphics.getDisplayMode();
         System.out.println("resolution: " + dm.width + "x" + dm.height);
         pendingRunnables.put(new Runnable() {
            @Override
            public void run() {
               float targetWidth = width;

               float targetHeight;
               for (targetHeight = height; targetWidth > dm.width || targetHeight > dm.height; targetHeight = (float)(targetHeight * 0.8)) {
                  targetWidth = (float)(targetWidth * 0.8);
               }

               Gdx.graphics.setWindowedMode((int)targetWidth, (int)targetHeight);
            }
         }, 2.0F * i);
         final String suffix = "_" + screenshotNumber;
         pendingRunnables.put(new Runnable() {
            @Override
            public void run() {
               ScreenshotGenerator.snap(size, suffix);
            }
         }, 2.0F * i + 1.0F);
      }

      screenshotNumber++;
   }

   private static void snap(String name, String suffix) {
      Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
      ByteBuffer pixels = pixmap.getPixels();
      int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;

      for (int i = 3; i < size; i += 4) {
         pixels.put(i, (byte)-1);
      }

      String path = "../../randombits/screenshots/output/" + name + suffix + ".png";
      FileHandle file = Gdx.files.local(path);
      PixmapIO.writePNG(file, pixmap, -1, true);
      String command = "convert " + file.path() + " -resize " + name + "! " + path;
      System.out.println(command);

      try {
         Process p = Runtime.getRuntime().exec(command);
         p.waitFor();
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      } catch (InterruptedException var10) {
         throw new RuntimeException(var10);
      }

      pixmap.dispose();
   }
}
