package com.tann.dice.gameplay.level;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {
   private final List<MonsterType> monsterList;
   private final Float difficultyDelta;

   public Level(MonsterType... monsterTypes) {
      this(null, monsterTypes);
   }

   public Level(Float diffD, MonsterType... monsterTypes) {
      this(diffD, new ArrayList<>(Arrays.asList(monsterTypes)));
   }

   public Level(Float diffD, List<MonsterType> monsterTypes) {
      if (monsterTypes == null) {
         monsterTypes = new ArrayList<>(Arrays.asList(PipeMonster.getMissingno()));
      }

      this.monsterList = monsterTypes;
      this.difficultyDelta = diffD;
   }

   public static Level errorLevel(int levelNumber) {
      if (levelNumber < 6) {
         return new Level(PipeMonster.getMissingno());
      } else if (levelNumber < 12) {
         return new Level(PipeMonster.getMissingno(), PipeMonster.getMissingno(), PipeMonster.getMissingno());
      } else if (levelNumber < 16) {
         return new Level(
            PipeMonster.getMissingno(), PipeMonster.getMissingno(), PipeMonster.getMissingno(), PipeMonster.getMissingno(), PipeMonster.getMissingno()
         );
      } else {
         return levelNumber <= 20
            ? new Level(
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno()
            )
            : new Level(
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno(),
               PipeMonster.getMissingno()
            );
      }
   }

   public List<MonsterType> getMonsterList() {
      return this.monsterList;
   }

   public static List<Level> generateRandomLevels() {
      List<Level> results = new ArrayList<>();
      results.add(new Level(MonsterTypeLib.randomWithRarity(), MonsterTypeLib.randomWithRarity(), MonsterTypeLib.randomWithRarity()));
      List<MonsterType> copy = MonsterTypeLib.getMasterCopy();

      for (int i = 0; i < 10; i++) {
         results.add(new Level(Tann.pickNRandomElements(copy, (int)(Math.random() * 3.0 + 1.0)).toArray(new MonsterType[0])));
      }

      return results;
   }

   @Override
   public String toString() {
      return "Level: " + this.monsterList;
   }

   public Float getDiffD() {
      return this.difficultyDelta;
   }

   public String diffDeltaString() {
      return diffDeltaString(this.getDiffD());
   }

   public static String diffDeltaString(Float difficultyDelta) {
      return difficultyDelta == null
         ? "unknown"
         : TextWriter.getTag(difficultyDelta > 0.0F ? Colours.red : Colours.green) + Tann.floatFormat(difficultyDelta) + "[cu]";
   }

   public boolean hasMissingno() {
      return EntTypeUtils.anyMissingno(this.monsterList);
   }
}
