package com.tann.dice.gameplay.trigger.global.speech;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.trigger.global.speech.between.GlobalSpeechDoomsay;
import com.tann.dice.gameplay.trigger.global.speech.between.GlobalSpeechWantItems;
import com.tann.dice.gameplay.trigger.global.speech.statSnap.GlobalSpeechEnvy;
import com.tann.dice.gameplay.trigger.global.speech.statSnap.GlobalSpeechOtherDeath;
import com.tann.dice.gameplay.trigger.global.speech.statSnap.GlobalSpeechSnap;
import com.tann.dice.test.util.TestRunner;
import java.util.ArrayList;
import java.util.List;

public class GlobalContainerSpeech extends GlobalSpeech {
   final List<GlobalSpeech> speeches = makeGlobalSpeech();

   @Override
   public void statSnapshotCheck(StatSnapshot ss) {
      if (!TestRunner.isTesting()) {
         for (int i = 0; i < this.speeches.size(); i++) {
            this.speeches.get(i).statSnapshotCheck(ss);
         }
      }
   }

   @Override
   public void levelEndAfterShortWait(DungeonContext context) {
      if (!TestRunner.isTesting()) {
         for (int i = 0; i < this.speeches.size(); i++) {
            this.speeches.get(i).levelEndAfterShortWait(context);
         }
      }
   }

   private static List<GlobalSpeech> makeGlobalSpeech() {
      List<GlobalSpeech> result = new ArrayList<>();
      result.addAll(GlobalSpeechEnvy.makeEnvies());
      result.add(new GlobalSpeechSnap());
      result.add(new GlobalSpeechOtherDeath());
      result.add(new GlobalSpeechDoomsay());
      result.add(new GlobalSpeechWantItems());
      return result;
   }
}
