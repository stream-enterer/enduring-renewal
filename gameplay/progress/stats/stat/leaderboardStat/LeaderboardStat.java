package com.tann.dice.gameplay.progress.stats.stat.leaderboardStat;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.leaderboard.Leaderboard;
import com.tann.dice.gameplay.leaderboard.LeaderboardBlob;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatMergeType;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardStat extends Stat {
   public LeaderboardStat(Leaderboard leaderboard) {
      super(getName(leaderboard));
   }

   @Override
   protected StatMergeType getMergeType() {
      return StatMergeType.Newest;
   }

   public static String getName(Leaderboard leaderboard) {
      return leaderboard.getStatName() + "-best-submitted319";
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return false;
   }

   public static List<Stat> make() {
      List<Stat> result = new ArrayList<>();

      for (Leaderboard l : LeaderboardBlob.all) {
         result.add(new LeaderboardStat(l));
      }

      return result;
   }

   @Override
   public boolean isBoring() {
      return true;
   }
}
