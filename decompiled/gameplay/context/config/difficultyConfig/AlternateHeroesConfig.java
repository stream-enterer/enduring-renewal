package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.GlobalAlternateHeroes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlternateHeroesConfig extends DifficultyConfig {
   public AlternateHeroesConfig(Difficulty difficulty) {
      super(Mode.ALTERNATE_HEROES, difficulty);
   }

   public AlternateHeroesConfig(String serial) {
      this(Difficulty.valueOf(serial));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new AlternateHeroesConfig(d));
      }

      return configs;
   }

   @Override
   protected List<Global> getSpecificDifficultyModeGlobals() {
      return Arrays.asList(new GlobalAlternateHeroes());
   }
}
