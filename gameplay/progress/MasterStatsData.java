package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import java.util.ArrayList;
import java.util.List;

public class MasterStatsData {
   List<Stat> stats;
   List<String> completedAchievementStrings;

   public MasterStatsData() {
   }

   public MasterStatsData(List<Stat> stats, List<Achievement> completedAchievements) {
      this.stats = stats;
      this.completedAchievementStrings = new ArrayList<>();

      for (Achievement a : completedAchievements) {
         this.completedAchievementStrings.add(a.getName());
      }
   }
}
