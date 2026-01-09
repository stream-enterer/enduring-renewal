package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.GlobalGenerateHeroes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateHeroesConfig extends DifficultyConfig {
   public GenerateHeroesConfig(Difficulty difficulty) {
      super(Mode.GENERATE_HEROES, difficulty);
   }

   public GenerateHeroesConfig(String serial) {
      this(Difficulty.valueOf(serial));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new GenerateHeroesConfig(d));
      }

      return configs;
   }

   @Override
   protected List<Global> getSpecificDifficultyModeGlobals() {
      return Arrays.asList(new GlobalGenerateHeroes());
   }
}
