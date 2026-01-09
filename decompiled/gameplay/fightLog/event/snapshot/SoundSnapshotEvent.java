package com.tann.dice.gameplay.fightLog.event.snapshot;

import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.statics.sound.Sounds;

public class SoundSnapshotEvent extends SnapshotEvent {
   public static SoundSnapshotEvent flap = new SoundSnapshotEvent(Sounds.flap);
   public static SoundSnapshotEvent stealth = new SoundSnapshotEvent(Sounds.stealth);
   public static SoundSnapshotEvent resurrectSound = new SoundSnapshotEvent(Sounds.summonBones);
   public static SoundSnapshotEvent clink = new SoundSnapshotEvent(Sounds.clink);
   public static SoundSnapshotEvent slime = new SoundSnapshotEvent(Sounds.slime);
   public static SoundSnapshotEvent witchHex = new SoundSnapshotEvent(Sounds.fire);
   public static SoundSnapshotEvent plague = new SoundSnapshotEvent(Sounds.deboost);
   public static SoundSnapshotEvent gong = new SoundSnapshotEvent(Sounds.gong);
   public static SoundSnapshotEvent clang = new SoundSnapshotEvent(Sounds.clangs);
   public static SoundSnapshotEvent smith = new SoundSnapshotEvent(Sounds.smith);
   public static SoundSnapshotEvent bansheeWail = new SoundSnapshotEvent(Sounds.wail);
   public static SoundSnapshotEvent chip = new SoundSnapshotEvent(Sounds.chip);
   private final String[] sound;

   public SoundSnapshotEvent(String[] sound) {
      this.sound = sound;
   }

   @Override
   public void act(AbilityHolder holder) {
      Sounds.playSound(this.sound);
   }
}
