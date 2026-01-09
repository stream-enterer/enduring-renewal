package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import java.util.ArrayList;
import java.util.List;

public class ShortcutConfig extends DifficultyConfig {
   public ShortcutConfig(Difficulty difficulty) {
      super(Mode.SHORTCUT, difficulty);
   }

   public ShortcutConfig(String serial) {
      this(Difficulty.valueOf(serial));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new ShortcutConfig(d));
      }

      return configs;
   }

   @Override
   public int getLevelOffset() {
      return 8;
   }
}
