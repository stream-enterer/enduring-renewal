package com.tann.dice.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.tann.dice.desktop.steam.SteamControl;
import com.tann.dice.platform.audio.DefaultSoundHandler;

public class DicetopLauncher {
   public static void main(String[] arg) {
      Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
      config.setForegroundFPS(60);
      config.setIdleFPS(60);
      config.setWindowedMode(1280, 760);
      config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 16);
      config.setTitle("Slice & Dice");
      config.setWindowIcon(FileType.Internal, new String[]{"misc/icon.png"});
      config.setHdpiMode(HdpiMode.Pixels);
      new Lwjgl3Application(new com.tann.dice.Main(new DefaultSoundHandler(), new SteamControl(), false), config);
   }
}
