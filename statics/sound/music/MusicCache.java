package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Map;

public class MusicCache {
   static Map<MusicData, Music> pathToMusic = new HashMap<>();

   static void resetStatics() {
      disposeIfNotNull(pathToMusic);
      pathToMusic = new HashMap<>();
   }

   public static Music get(MusicData path) {
      return pathToMusic.get(path);
   }

   public static Music getOrLoad(MusicData data) {
      if (get(data) != null) {
         return get(data);
      } else {
         FileHandle fh = Gdx.files.internal("music/" + data.path);
         if (!fh.exists()) {
            return null;
         } else {
            Music m = Gdx.audio.newMusic(fh);
            set(data, m);
            return m;
         }
      }
   }

   private static void set(MusicData path, Music music) {
      pathToMusic.put(path, music);
   }

   private static void disposeIfNotNull(Map<MusicData, Music> pathToMusic) {
      if (pathToMusic != null) {
         for (Music value : pathToMusic.values()) {
            value.dispose();
         }
      }
   }
}
