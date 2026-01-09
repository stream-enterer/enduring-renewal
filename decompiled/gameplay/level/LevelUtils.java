package com.tann.dice.gameplay.level;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import java.util.ArrayList;
import java.util.List;

public abstract class LevelUtils {
   public static List<Level> generateFor(Difficulty diff) {
      List<Level> results = new ArrayList<>();
      DungeonContext dc = new DungeonContext(new ClassicConfig(diff), Party.generate(0), 1);

      for (int i = 0; i < 20; i++) {
         Level current = dc.getCurrentLevel();
         results.add(current);
         if (i < 19) {
            dc.nextLevel();
         }
      }

      return results;
   }

   public static Level generateFor(Difficulty diff, int lv) {
      int lookbackGen = 2;
      DungeonContext dc = new DungeonContext(new ClassicConfig(diff), Party.generate(0), Math.max(1, lv - lookbackGen));

      for (int i = 0; i < Math.min(lv - 1, lookbackGen); i++) {
         dc.getCurrentLevel();
         dc.nextLevel();
      }

      return dc.getCurrentLevel();
   }

   public static Zone guessTypeFor(int levelIndex) {
      return Zone.guessFromLevel(levelIndex);
   }
}
