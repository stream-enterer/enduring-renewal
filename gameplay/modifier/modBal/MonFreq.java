package com.tann.dice.gameplay.modifier.modBal;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.level.LevelUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MonFreq {
   private static Map<MonsterType, Float> cache;
   static final int iter = 1000;

   public static float getRelativeFrequency(MonsterType type) {
      return getCachedFrequencies().get(type);
   }

   private static Map<MonsterType, Float> getCachedFrequencies() {
      if (cache == null) {
         Map<MonsterType, Integer> monsterMap = new HashMap<>();

         for (MonsterType monsterType : MonsterTypeLib.getMasterCopy()) {
            if (UnUtil.isLocked(monsterType)) {
               TannLog.error("Cannot calculate monfreq with locked enemies");
            }

            monsterMap.put(monsterType, 0);
         }

         List<Level> levels = new ArrayList<>();

         for (int i = 0; i < 1000; i++) {
            levels.addAll(LevelUtils.generateFor(Difficulty.Unfair));
         }

         int totalMonsters = 0;

         for (Level level : levels) {
            for (MonsterType mt : level.getMonsterList()) {
               if (monsterMap.get(mt) != null) {
                  totalMonsters++;
                  monsterMap.put(mt, monsterMap.get(mt) + 1);
               }
            }
         }

         cache = new HashMap<>();

         for (Entry<MonsterType, Integer> e : monsterMap.entrySet()) {
            cache.put(e.getKey(), (float)e.getValue().intValue() / totalMonsters);
         }
      }

      return cache;
   }
}
