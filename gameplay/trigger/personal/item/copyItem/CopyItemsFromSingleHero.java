package com.tann.dice.gameplay.trigger.personal.item.copyItem;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import java.util.ArrayList;
import java.util.List;

public class CopyItemsFromSingleHero extends Personal {
   final String heroType;

   public CopyItemsFromSingleHero(String heroType) {
      this.heroType = heroType;
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      List<Personal> linkedTriggers = new ArrayList<>();

      for (EntState es : snapshot.getStates(true, null)) {
         if (es.getEnt().entType.getName(false).equalsIgnoreCase(this.heroType)) {
            for (Item e : es.getEnt().getItems()) {
               linkedTriggers.addAll(e.getPersonals());
            }
         }
      }

      return linkedTriggers;
   }
}
