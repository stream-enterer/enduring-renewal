package com.tann.dice.gameplay.mode.debuggy;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.misc.DebugConfig;
import com.tann.dice.gameplay.mode.Mode;
import java.util.Arrays;
import java.util.List;

public class DebugMode extends Mode {
   public DebugMode() {
      super("Debug");
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new DebugConfig());
   }

   @Override
   public String getSaveKey() {
      return "debug";
   }

   @Override
   public boolean isPlayable() {
      return false;
   }
}
