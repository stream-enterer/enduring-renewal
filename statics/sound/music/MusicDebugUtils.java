package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.tann.dice.test.TestFiles;
import java.util.ArrayList;
import java.util.List;

public class MusicDebugUtils {
   public static void printMusic() {
      String rootPath = "C:/code/games/Dicegeon/android/assets/music/";
      FileHandle root = Gdx.files.absolute(rootPath);
      List<FileHandle> children = TestFiles.getAll(root);
      System.out.println("private final static List<TP<String, Float>> musicData = Arrays.asList(");

      for (int i = 0; i < children.size(); i++) {
         FileHandle child = children.get(i);
         if (!child.path().contains("debug")) {
            String fileName = child.path().split(rootPath)[1];
            Music m = Gdx.audio.newMusic(child);
            float length = analyseLength(m);
            boolean lastSong = i == children.size() - 1;
            System.out.println("new TP<>(\"" + fileName + "\", " + length + "f)" + (lastSong ? "" : ","));
         }
      }

      System.out.println(");");
   }

   public static List<String> getMusicPaths() {
      String rootPath = "C:/code/games/Dicegeon/android/assets/music/";
      FileHandle root = Gdx.files.absolute(rootPath);
      List<FileHandle> children = TestFiles.getAll(root);
      List<String> result = new ArrayList<>();
      System.out.println("private final static List<TP<String, Float>> musicData = Arrays.asList(");

      for (int i = 0; i < children.size(); i++) {
         FileHandle child = children.get(i);
         if (!child.path().contains("debug")) {
            String fileName = child.path().split(rootPath)[1];
            result.add(fileName);
         }
      }

      return result;
   }

   private static float analyseLength(Music music) {
      float MAX = 300.0F;
      int iterations = 22;
      float tooHigh = 300.0F;
      float tooLow = 0.0F;

      for (int i = 0; i < 22; i++) {
         float check = tooLow + (tooHigh - tooLow) / 2.0F;
         music.setPosition(check);
         boolean after = music.getPosition() == 0.0F;
         if (after) {
            tooHigh = check;
            music.play();
         } else {
            tooLow = check;
         }
      }

      music.stop();
      return tooLow + (tooHigh - tooLow) / 2.0F;
   }
}
