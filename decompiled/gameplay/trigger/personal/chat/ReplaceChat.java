package com.tann.dice.gameplay.trigger.personal.chat;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.util.Tann;

public class ReplaceChat extends ChatOnly {
   private final String[] options;

   public ReplaceChat(String... options) {
      this.options = options;
   }

   @Override
   public String transformChat(String chatText, EntState vis) {
      return Tann.pick(this.options);
   }
}
