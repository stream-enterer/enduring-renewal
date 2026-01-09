package com.tann.dice.gameplay.trigger.personal.chat;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;

public class KillSpecificMonster extends ChatOnly {
   final String type;
   final StateEvent event;

   public KillSpecificMonster(String type, StateEvent event) {
      this.type = type;
      this.event = event;
   }

   @Override
   public void onKill(EntState entState, Ent killed) {
      if (killed.getEntType().getName(false).equalsIgnoreCase(this.type)) {
         entState.addEvent(this.event, true);
      }
   }
}
