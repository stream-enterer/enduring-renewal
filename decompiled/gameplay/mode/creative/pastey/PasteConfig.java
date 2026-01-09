package com.tann.dice.gameplay.mode.creative.pastey;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;

public class PasteConfig extends ContextConfig {
   public PasteConfig() {
      super(Mode.PASTE);
   }

   @Override
   public int getTotalLength() {
      return 1;
   }

   @Override
   public boolean canRestart() {
      return false;
   }
}
