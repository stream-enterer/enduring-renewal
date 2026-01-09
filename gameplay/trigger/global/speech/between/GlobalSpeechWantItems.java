package com.tann.dice.gameplay.trigger.global.speech.between;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.trigger.global.speech.GlobalSpeech;
import java.util.List;

public class GlobalSpeechWantItems extends GlobalSpeech {
   public static final ChatStateEvent cse = new ChatStateEvent(0.01F, "Can I get an item?", "I want an item too!", "Everyone else gets an item...");

   @Override
   public void levelEndAfterShortWait(DungeonContext context) {
      List<Hero> heroes = context.getParty().getHeroes();
      int numItems = 0;
      int hasItems = 0;
      Hero noItem = null;

      for (int i = 0; i < heroes.size(); i++) {
         Hero h = heroes.get(i);
         numItems += h.getItems().size();
         if (h.getItems().size() == 0) {
            noItem = h;
         } else {
            hasItems++;
         }
      }

      if (numItems > heroes.size() && heroes.size() > 3 && hasItems == heroes.size() - 1 && cse.chance() && noItem != null) {
         cse.act(noItem.getEntPanel());
      }

      super.levelEndAfterShortWait(context);
   }
}
