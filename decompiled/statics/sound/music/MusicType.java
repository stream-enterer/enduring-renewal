package com.tann.dice.statics.sound.music;

public enum MusicType {
   Title(true),
   Forest(false),
   Catacombs(false),
   Dungeon(false),
   Lair(false),
   Pit(false),
   Mishap(true);

   public final boolean forceSwitch;

   private MusicType(boolean forceSwitch) {
      this.forceSwitch = forceSwitch;
   }
}
