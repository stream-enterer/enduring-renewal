package com.tann.dice.statics.bullet;

import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.sound.Sounds;

public class RollFx {
   public static void addRollSFX(int numDice, boolean firstRoll, boolean jiggle, boolean player) {
      float speedMult = OptionUtils.getRollSpeedMultiplier(player);
      numDice = Math.round(numDice * Interpolation.linear.apply(0.3F, 1.0F, speedMult));
      if (jiggle) {
         Sounds.playSound(Sounds.clacks, 1.0F, getPitch(speedMult));
         Sounds.playSoundDelayed(Sounds.clocks, 1.0F, getPitch(speedMult), 0.4F * speedMult);
      }

      float maximumDelay = 5.0F * speedMult;
      float clackStart = (firstRoll ? 0.1F : 0.18F) * speedMult;
      float clackRand = 0.1F * speedMult;

      for (int i = 1; i < numDice; i++) {
         Sounds.playSoundDelayed(Sounds.clacks, 1.0F, getPitch(speedMult), (float)(clackStart + Math.min(maximumDelay, (float)i) * clackRand * Math.random()));
      }

      int extraClacks = (int)(Math.pow(numDice, 1.9F) * Math.random() / 7.0);
      float extraClackStart = 0.85F * speedMult;

      for (int i = 0; i < extraClacks; i++) {
         Sounds.playSoundDelayed(
            Sounds.clacks, 1.0F, getPitch(speedMult), (float)(extraClackStart + Math.min(maximumDelay, (float)i) * clackRand * Math.random())
         );
      }

      clackStart = 0.65F * speedMult;
      clackRand = 0.22F * speedMult;

      for (int i = 0; i < numDice; i++) {
         Sounds.playSoundDelayed(Sounds.clocks, 1.0F, getPitch(speedMult), (float)(clackStart + Math.min(maximumDelay, (float)i) * Math.random() * clackRand));
      }
   }

   public static float simplerp(float x, float str) {
      return Interpolation.linear.apply(x, 1.0F, str);
   }

   private static float getPitch(float speedMult) {
      return (float)(0.8F + Math.random() * 0.2F) / simplerp(speedMult, 0.7F);
   }
}
