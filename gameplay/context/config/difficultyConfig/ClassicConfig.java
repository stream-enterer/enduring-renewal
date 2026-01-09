package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import java.util.ArrayList;
import java.util.List;

public class ClassicConfig extends DifficultyConfig {
   public ClassicConfig(Difficulty difficulty) {
      super(Mode.CLASSIC, difficulty);
   }

   public ClassicConfig(String save) {
      this(Difficulty.valueOf(save));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new ClassicConfig(d));
      }

      return configs;
   }
}
