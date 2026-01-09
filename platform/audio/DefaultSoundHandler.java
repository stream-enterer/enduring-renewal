package com.tann.dice.platform.audio;

import com.badlogic.gdx.audio.Sound;

public class DefaultSoundHandler implements SoundHandler {
   @Override
   public void play(Sound sound, float volume, float pitch) {
      sound.play(volume, pitch, 0.0F);
   }
}
