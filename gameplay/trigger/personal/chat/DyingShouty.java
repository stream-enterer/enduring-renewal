package com.tann.dice.gameplay.trigger.personal.chat;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;

public class DyingShouty extends ChatOnly {
   @Override
   public String transformChat(String chatText, EntState vis) {
      return vis != null && StateConditionType.Dying.isValid(vis) ? chatText.toUpperCase() : chatText;
   }
}
