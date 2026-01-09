package com.tann.dice.gameplay.trigger.global.speech.between;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.trigger.global.speech.GlobalSpeech;
import com.tann.dice.util.Tann;
import java.util.List;

public class GlobalSpeechDoomsay extends GlobalSpeech {
   public static final ChatStateEvent cse = new ChatStateEvent(0.08F, "We're all gonna die...", "I don't think we got this", "Maybe we should [grey]flee");

   @Override
   public void levelEndAfterShortWait(DungeonContext context) {
      List<Hero> heroes = context.getParty().getHeroes();
      int wasDeads = 0;

      for (int i = 0; i < heroes.size(); i++) {
         Hero h = heroes.get(i);
         if (h.isDiedLastRound()) {
            wasDeads++;
         }
      }

      float ratio = (float)wasDeads / heroes.size();
      if (ratio > 0.7F) {
         if (!cse.chance()) {
            return;
         }

         cse.act(Tann.random(heroes).getEntPanel());
      }

      super.levelEndAfterShortWait(context);
   }
}
