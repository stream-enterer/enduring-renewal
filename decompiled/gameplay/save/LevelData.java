package com.tann.dice.gameplay.save;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.level.Level;
import java.util.ArrayList;
import java.util.List;

public class LevelData {
   public List<String> m;

   public LevelData(List<String> monsters) {
      this.m = monsters;
   }

   public LevelData() {
   }

   public LevelData(Level thisLevel) {
      this(getMonsterString(thisLevel));
   }

   private static List<String> getMonsterString(Level thisLevel) {
      List<String> monsterString = new ArrayList<>();

      for (MonsterType mt : thisLevel.getMonsterList()) {
         monsterString.add(mt.getSaveString());
      }

      return monsterString;
   }

   public Level toLevel() {
      List<MonsterType> types = new ArrayList<>();

      for (String s : this.m) {
         types.add(MonsterTypeLib.byName(s));
      }

      return new Level(types.toArray(new MonsterType[0]));
   }
}
