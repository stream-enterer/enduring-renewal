package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.MaxLevel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoConfig extends DifficultyConfig {
   public DemoConfig(Difficulty d) {
      super(Mode.DEMO, d);
   }

   public DemoConfig(String save) {
      this(Difficulty.valueOf(save));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new DemoConfig(d));
      }

      return configs;
   }

   @Override
   protected List<Global> getSpecificDifficultyModeGlobals() {
      return Arrays.asList(new MaxLevel(12));
   }
}
