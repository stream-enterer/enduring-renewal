package com.tann.dice.gameplay.trigger.global.speech.statSnap;

import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.trigger.global.speech.GlobalSpeech;

public abstract class GlobalSpeechStatSnapshot extends GlobalSpeech {
   @Override
   public final void statSnapshotCheck(StatSnapshot ss) {
      this.snapshot(ss);
   }

   protected abstract void snapshot(StatSnapshot var1);
}
