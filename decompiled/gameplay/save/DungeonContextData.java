package com.tann.dice.gameplay.save;

import com.tann.dice.gameplay.context.DungeonValue;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.ArrayList;
import java.util.List;

public class DungeonContextData {
   public String cc;
   public String c;
   public int n;
   public PartyData p;
   public List<String> m = new ArrayList<>();
   public LevelData l;
   public List<LevelData> pl = new ArrayList<>();
   public List<DungeonValue> dv = new ArrayList<>();
   public int sl;
   public int seed;
   public long extraTime;
   public List<Stat> stats = new ArrayList<>();
   public boolean checkedItems;

   public DungeonContextData(
      String configClass,
      String config,
      PartyData party,
      int currentLevelNumber,
      int startLevel,
      List<String> modifiers,
      List<Stat> stats,
      long extraTime,
      LevelData currentLevelData,
      List<LevelData> previousLevels,
      int seed,
      boolean checkedItems,
      List<DungeonValue> values
   ) {
      this.cc = configClass;
      this.c = config;
      this.p = party;
      this.n = currentLevelNumber;
      this.sl = startLevel;
      this.m = modifiers;
      this.stats = stats;
      this.extraTime = extraTime;
      this.l = currentLevelData;
      this.pl = previousLevels;
      this.seed = seed;
      this.checkedItems = checkedItems;
      this.dv = values;
   }

   public DungeonContextData() {
   }

   public void clearForReport() {
      if (this.pl != null) {
         this.pl.clear();
      }

      if (this.stats != null) {
         this.stats.clear();
      }

      if (this.p != null) {
         this.p.plt = null;
      }

      this.extraTime = 0L;
      this.checkedItems = false;
      this.seed = 0;
      this.cc = null;
      this.c = null;
   }
}
