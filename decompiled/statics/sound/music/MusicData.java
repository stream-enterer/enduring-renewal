package com.tann.dice.statics.sound.music;

import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Tann;
import java.util.List;

public class MusicData {
   public final String path;
   public final float seconds;
   public final MusicType[] musicTypes;
   public int loop = 1;
   private static final int playlistLookback = 3;
   float extraFade;
   float fadeDurationMultiplier = 1.0F;

   public MusicData(String path, float seconds, MusicType... musicTypes) {
      this.path = path;
      this.seconds = seconds;
      this.musicTypes = musicTypes;
   }

   public float getRarity(List<MusicData> played) {
      float base = this.getRarity();
      if (played.contains(this)) {
         int sub = Math.max(0, played.size() - 3);
         int index = played.lastIndexOf(this);
         if (index < sub) {
            return base;
         } else {
            float ratio = (index - sub + 1.0F) / 3.0F;
            return base * (1.01F - ratio);
         }
      } else {
         return base;
      }
   }

   public float getRarity() {
      float commonNess = this.seconds * this.loop + 20.0F;
      if (OptionLib.MUSIC_SELECTION.c() == 0) {
         int mtl = this.musicTypes.length;
         if (mtl == 0) {
            return 0.0F;
         }

         if (Tann.contains(this.musicTypes, MusicType.Forest)) {
            mtl++;
         }

         commonNess *= mtl;
      }

      return 1.0F / commonNess;
   }

   @Override
   public String toString() {
      return this.path;
   }

   public MusicData loop(int loops, float extraFade) {
      this.loop = loops;
      this.extraFade = extraFade;
      return this;
   }

   public MusicData fadeSpeed(float v) {
      this.fadeDurationMultiplier = v;
      return this;
   }
}
