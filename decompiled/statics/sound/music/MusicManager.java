package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MusicManager {
   private static final float SONG_GAP = 1.4F;
   private static final boolean MUSIC_PRINT = false;
   private static List<MusicData> dataList;
   private static List<Musician> musicianList;
   private static final List<MusicData> playedList = new ArrayList<>();
   private static MusicFader fading;
   private static MusicFader playing;
   private static int failsInARow = 0;
   private static boolean waitingForThread = false;
   private static boolean waitingBetweenSong = false;
   private static MusicType currentMusicType;
   static float lastPos;
   static int samePos;

   public static void initMusic() {
      dataList = MusicBlob.getData();
      musicianList = MusicBlob.loadMusicians(dataList);
      preloadMusicIfNecessary();
   }

   public static List<Musician> getMusicianList() {
      return musicianList;
   }

   public static List<MusicData> getDataList() {
      return dataList;
   }

   public static boolean requestSong(MusicData music, boolean bypassOverrides) {
      if (!bypassOverrides && com.tann.dice.Main.getSettings().isDisabledSong(music)) {
         return false;
      } else if (alreadyPlaying(MusicCache.get(music))) {
         return false;
      } else {
         fadeIn(music);
         return true;
      }
   }

   public static boolean isMusicDisabled() {
      return com.tann.dice.Main.self().control.disableMusic() || dataList.isEmpty() || OptionLib.music.isOff();
   }

   public static boolean isPlaying(boolean innerMusic) {
      if (playing == null) {
         return false;
      } else {
         return innerMusic ? playing.m.isPlaying() : !playing.stopped;
      }
   }

   private static boolean alreadyPlaying(Music m) {
      return fading != null && fading.m == m || playing != null && playing.m == m;
   }

   private static void fadeIn(final MusicData md) {
      boolean useThread = OptionLib.MUSIC_LOAD_TYPE.c() == 0;
      if (useThread && MusicCache.get(md) == null) {
         if (waitingForThread) {
            return;
         }

         waitingForThread = true;
         (new Thread() {
            @Override
            public void run() {
               final Music m = MusicCache.getOrLoad(md);
               if (m == null) {
                  TannLog.error("Failed to load " + md);
                  MusicManager.failsInARow++;
                  MusicManager.waitingForThread = false;
               } else {
                  Gdx.app.postRunnable(new Runnable() {
                     @Override
                     public void run() {
                        MusicManager.loadAndPlay(md, m);
                        MusicManager.waitingForThread = false;
                     }
                  });
                  super.run();
               }
            }
         }).start();
      } else {
         Music m = MusicCache.getOrLoad(md);
         if (m == null) {
            TannLog.error("Failed to load " + md);
            failsInARow++;
         } else {
            loadAndPlay(md, m);
         }
      }
   }

   private static boolean loadAndPlay(MusicData md, Music m) {
      if (fading != null) {
         fading.stop();
      }

      fading = playing;
      if (fading != null) {
         fading.fadeOut();
         playedList.add(fading.md);
      }

      try {
         playing = new MusicFader(md, m);
         playing.play();
         if (OptionLib.SHOW_PLAYING_POPUP.c()) {
            com.tann.dice.Main.getCurrentScreen().showMusicPopup(JukeboxUtils.getPopupActor(md));
         }

         failsInARow = 0;
         return true;
      } catch (GdxRuntimeException var3) {
         TannLog.error("Failed to load " + md);
         failsInARow++;
         return false;
      }
   }

   public static void checkSongIsValid(boolean manualSelect) {
      if (OptionLib.MUSIC_SELECTION.c() == 0) {
         MusicType mt = getCurrentMusicType();
         if ((manualSelect || mt.forceSwitch && currentMusicType != mt || currentMusicType != null && currentMusicType.forceSwitch && currentMusicType != mt)
            && (playing == null || !Tann.contains(playing.md.musicTypes, mt))) {
            newZoneNewSong();
         }

         currentMusicType = mt;
      }
   }

   public static void tick(float delta) {
      checkSamePosBug();
      if (com.tann.dice.Main.frames % 100 == 0) {
         checkForThatOneBug();
      }

      checkSongIsValid(false);
      if (com.tann.dice.Main.getSettings().isVolumeMuted()) {
         if (playing != null && !playing.isFinished()) {
            playing.stop();
         }

         if (fading != null && !fading.isFinished()) {
            fading.stop();
         }

         playing = null;
         fading = null;
      } else {
         if (playing != null && playing.act(delta)) {
            playedList.add(playing.md);
            playing = null;
            waitingBetweenSong = true;
            Tann.delay(1.4F, new Runnable() {
               @Override
               public void run() {
                  if (MusicManager.waitingBetweenSong) {
                     MusicManager.manageNewSongs();
                  }
               }
            });
         }

         if (fading != null && fading.act(delta)) {
            fading = null;
         }

         if (!waitingBetweenSong) {
            manageNewSongs();
         }
      }
   }

   private static void checkSamePosBug() {
      if (!isMusicDisabled()) {
         float newPos = playing == null ? -1.0F : playing.m.getPosition();
         if (newPos == lastPos) {
            samePos++;
         } else {
            lastPos = newPos;
            samePos = 0;
         }

         if (samePos > 360) {
            TannLog.error("music stuck maybe, attempting to jiggle it");
            samePos = 0;
            standardNextSong();
         }
      }
   }

   private static void checkForThatOneBug() {
      for (MusicData musicDatum : dataList) {
         if ((playing == null || musicDatum != playing.md) && (fading == null || musicDatum != fading.md)) {
            Music m = MusicCache.get(musicDatum);
            if (m != null && m.isPlaying()) {
               m.stop();
               String msg = "Old music still playing: " + musicDatum + " (stopped)";
               TannLog.error(msg);
            }
         }
      }
   }

   private static void manageNewSongs() {
      waitingBetweenSong = false;
      if (!waitingForThread) {
         if (!hasFailed()) {
            if (isPlaying(false) && !isPlaying(true)) {
               standardNextSong();
            } else {
               if (playing == null || playing.isFinished()) {
                  standardNextSong();
               }
            }
         }
      }
   }

   public static boolean hasFailed() {
      return failsInARow > 5;
   }

   private static void standardNextSong() {
      if (OptionLib.MUSIC_SELECTION.c() == 0) {
         boolean djSuccess = nextTrackDJ();
         if (!djSuccess) {
            TannLog.error("Error finding playable dj song, playing something random");
            randomNewSong();
         }
      } else {
         randomNewSong();
      }
   }

   private static List<MusicData> getValidDjTracks() {
      List<MusicData> result = new ArrayList<>();
      MusicType mt = getCurrentMusicType();

      for (MusicData data : shufCop()) {
         if (!com.tann.dice.Main.getSettings().isDisabledSong(data) && Tann.contains(data.musicTypes, mt)) {
            result.add(data);
         }
      }

      return result;
   }

   private static List<MusicData> getValidRandomTracks() {
      List<MusicData> result = new ArrayList<>();

      for (MusicData data : shufCop()) {
         if (!com.tann.dice.Main.getSettings().isDisabledSong(data)) {
            result.add(data);
         }
      }

      return result;
   }

   private static List<MusicData> shufCop() {
      List<MusicData> cpy = new ArrayList<>(dataList);
      Collections.shuffle(cpy);
      return cpy;
   }

   private static void newZoneNewSong() {
      boolean success = nextTrackDJ();
   }

   private static boolean nextTrackDJ() {
      return isMusicDisabled() ? false : playRandomRarity(getValidDjTracks());
   }

   private static boolean playRandomRarity(List<MusicData> tracks) {
      if (playing != null) {
         tracks.remove(playing.md);
      }

      if (fading != null) {
         tracks.remove(fading.md);
      }

      if (!playedList.isEmpty()) {
         tracks.remove(playedList.get(playedList.size() - 1));
      }

      float total = 0.0F;

      for (MusicData track : tracks) {
         total += track.getRarity(playedList);
      }

      int attempts = 5;
      if (!Float.isNaN(total) && !Float.isInfinite(total) && total != 0.0F) {
         for (int i = 0; i < attempts; i++) {
            float roll = Tann.random(total);

            for (MusicData track : tracks) {
               roll -= track.getRarity(playedList);
               if (roll <= 0.0F) {
                  boolean success = requestSong(track, false);
                  if (success) {
                     return true;
                  }

                  TannLog.error("Failed to play song: " + track);
               }
            }
         }

         return false;
      } else {
         TannLog.error("bad random rarity roll: " + total);
         return false;
      }
   }

   private static MusicType getCurrentMusicType() {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s instanceof TitleScreen) {
         return MusicType.Title;
      } else {
         if (s instanceof DungeonScreen) {
            Zone z = Zone.guessFromLevel(((DungeonScreen)s).getDungeonContext().getCurrentMod20LevelNumber());
            if (z != null) {
               return z.getMusicType();
            }
         }

         return MusicType.Mishap;
      }
   }

   public static void userRequestNextSong() {
      standardNextSong();
   }

   public static void userRequestSkipBack() {
      if (fading != null) {
         fading.stop();
         fading = null;
      }

      if (playedList.size() != 0) {
         MusicData toPlay = playedList.remove(playedList.size() - 1);
         requestSong(toPlay, true);
         if (playedList.size() != 0) {
            playedList.remove(playedList.size() - 1);
         }
      }
   }

   public static void userRequestSkipForwards() {
      if (playing != null) {
         playing.skipForwards();
      }
   }

   private static void randomNewSong() {
      if (!isMusicDisabled()) {
         boolean success = playRandomRarity(getValidRandomTracks());
         if (!success) {
            TannLog.error("Failed to play random song, playing something random ignoring overrides");
            requestSong(Tann.random(dataList), true);
         }
      }
   }

   public static MusicData getCurrentSongData(boolean current) {
      MusicFader mf = current ? playing : fading;
      return mf == null ? null : mf.md;
   }

   public static String getCurrentSongTitle(boolean current) {
      MusicData p = getCurrentSongData(current);
      if (p == null) {
         return JukeboxUtils.getTextNone();
      } else {
         String[] spl = p.path.split("/");
         if (spl.length > 1) {
            String songName = spl[spl.length - 1];
            return Tann.makeEllipses(MusicFormat.getNiceName(songName), 99);
         } else {
            return "fail - p";
         }
      }
   }

   public static void clearStatics() {
      if (fading != null) {
         fading.stop();
      }

      if (playing != null) {
         playing.stop();
      }

      fading = null;
      playing = null;
      MusicCache.resetStatics();
   }

   public static void preloadMusicIfNecessary() {
      if (OptionLib.MUSIC_LOAD_TYPE.c() == 1 && !isMusicDisabled()) {
         for (MusicData musicDatum : dataList) {
            MusicCache.getOrLoad(musicDatum);
         }
      }
   }

   public static float getCurrentSongPlayedAmt() {
      if (playing == null) {
         return 0.0F;
      } else if (playing.isFinished()) {
         return 0.0F;
      } else {
         return !playing.m.isPlaying() ? 0.0F : playing.m.getPosition();
      }
   }

   private static MusicData fromFader(MusicFader mf) {
      return mf.md;
   }

   public static MusicData getCurrentMusicData() {
      return playing == null ? null : fromFader(playing);
   }

   public static Musician fetchInefficiently(MusicData md) {
      for (Musician loadMusician : MusicBlob.loadMusicians(dataList)) {
         if (loadMusician.songs.contains(md)) {
            return loadMusician;
         }
      }

      return null;
   }
}
