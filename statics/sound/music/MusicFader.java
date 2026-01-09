package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.audio.Music;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.util.TannLog;

public class MusicFader {
   private final float FADE_DURATION;
   private final float FADE_SAFETY_BUFFER = 0.05F;
   private final float MIN_VOLUME = 0.0F;
   public final Music m;
   boolean trackNeedsFade;
   float timeElapsed;
   float fadeTimer;
   float startVolume;
   float targetVolume;
   float stopLoopingAt = -1.0F;
   float startFadingOutAt = -1.0F;
   final int actualLoops;
   public final MusicData md;
   boolean stopped;

   public MusicFader(MusicData md, Music m) {
      this.md = md;
      this.trackNeedsFade = md.loop > 1;
      this.actualLoops = OptionUtils.affectLoops(trackSpecificLoop(md) ? md.loop : 1);
      if (this.trackNeedsFade) {
         this.FADE_DURATION = 1.2F * md.fadeDurationMultiplier;
      } else {
         this.FADE_DURATION = 0.08F;
      }

      this.stopLoopingAt = md.seconds * (this.actualLoops - 0.5F);
      this.startFadingOutAt = md.seconds * this.actualLoops - this.FADE_DURATION - 0.05F - md.extraFade;
      this.startVolume = 0.0F;
      this.targetVolume = 1.0F;
      m.setVolume(0.0F);
      this.m = m;
      m.setLooping(this.actualLoops > 1);
   }

   private static boolean forceLoop() {
      return false;
   }

   private static boolean trackSpecificLoop(MusicData md) {
      return md.loop > 1;
   }

   public void play() {
      if (this.stopped) {
         TannLog.error("trying to play stopped file");
      } else {
         this.m.play();
      }
   }

   public boolean act(float delta) {
      if (!this.stopped && !this.m.isPlaying() && this.startFadingOutAt != -1.0F) {
         this.stop();
         TannLog.error("Song stopped early, potential issue with song metadata: " + this.md);
      }

      if (this.stopped) {
         return true;
      } else {
         this.timeElapsed += delta;
         this.fadeTimer += delta;
         if (this.getValue() == 0.0F && this.targetVolume == 0.0F) {
            this.m.setVolume(0.0F);
            this.stop();
            return true;
         } else {
            if (this.fadeTimer < this.FADE_DURATION) {
               com.tann.dice.Main.requestRendering();
            }

            this.m.setLooping(forceLoop() || this.actualLoops > 1 && this.timeElapsed < this.stopLoopingAt);
            if (this.probablySwitchedAwayFromIninite() && this.targetVolume != 0.0F
               || this.startFadingOutAt != -1.0F && this.timeElapsed > this.startFadingOutAt) {
               this.startFadingOutAt = -1.0F;
               this.fadeOut();
            }

            this.m.setVolume(this.getVolume());
            return false;
         }
      }
   }

   private boolean probablySwitchedAwayFromIninite() {
      return OptionUtils.affectLoops(1) < 50 && this.timeElapsed > this.md.seconds * 2.0F;
   }

   private float getVolume() {
      return Math.max(0.0F, this.getValue() * com.tann.dice.Main.getSettings().getVolumeMusic());
   }

   private float getValue() {
      float ratio = this.fadeTimer / this.FADE_DURATION;
      ratio = Math.max(0.0F, Math.min(1.0F, ratio));
      return this.startVolume + (this.targetVolume - this.startVolume) * ratio;
   }

   public void stop() {
      if (this.stopped) {
         TannLog.error("Trying to stop again");
      } else {
         this.m.setVolume(0.0F);
         this.m.setLooping(false);
         this.m.stop();
         this.stopped = true;
      }
   }

   public void fadeOut() {
      System.out.println("fading out");
      if (!this.stopped) {
         this.fadeTo(0.0F);
      }
   }

   public void fadeIn() {
      this.fadeTo(1.0F);
   }

   private void fadeTo(float inTarget) {
      if (this.stopped) {
         TannLog.error("Trying to fade to " + inTarget + " when stopped");
      } else {
         this.startVolume = this.getValue();
         this.targetVolume = inTarget;
         this.fadeTimer = 0.0F;
      }
   }

   public boolean isFinished() {
      if (this.stopped || !this.m.isPlaying()) {
         return true;
      } else {
         return forceLoop() ? false : false;
      }
   }

   public void skipForwards() {
      float amt = this.md.seconds / 8.0F;
      this.m.setPosition(this.m.getPosition() + amt);
      this.timeElapsed += amt;
      this.fadeTimer += amt;
   }
}
