package com.tann.dice.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.tann.dice.platform.audio.DefaultSoundHandler;
import com.tann.dice.platform.control.desktop.DesktopMp3Control;
import com.tann.dice.util.Tann;

public class MactopLauncher {
   public static void main(String[] arg) {
      Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
      config.setForegroundFPS(60);
      config.setIdleFPS(60);
      config.setWindowedMode(Tann.DG_WIDTH, Tann.DG_HEIGHT);
      config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 16);
      config.setTitle("Slice & Dice");
      config.setWindowIcon(FileType.Internal, new String[]{"misc/icon.png"});
      config.setHdpiMode(HdpiMode.Pixels);
      new Lwjgl3Application(new com.tann.dice.Main(new DefaultSoundHandler(), new DesktopMp3Control(), false, false), config);
   }
}
