package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.MasterStats;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.FurthestReachedStat;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak.BestStreakStat;

public class StreakLeaderboard extends Leaderboard {
   final Mode mode;
   public final Difficulty difficulty;

   public StreakLeaderboard(Mode mode, Difficulty difficulty, String url) {
      super(
         difficulty.name(),
         "[notranslate]"
            + com.tann.dice.Main.t(mode.getTextButtonName())
            + " "
            + com.tann.dice.Main.t(difficulty.getColourTaggedName())
            + " "
            + com.tann.dice.Main.t("streak"),
         difficulty.getColor(),
         url,
         "score",
         5,
         true
      );
      this.mode = mode;
      this.difficulty = difficulty;
   }

   @Override
   public boolean internalValid(Mode m, Difficulty d) {
      return m == this.mode && d == this.difficulty;
   }

   @Override
   public int getScore() {
      ContextConfig cc;
      if (this.difficulty == null) {
         cc = this.mode.getConfigs().get(0);
      } else {
         cc = this.mode.getConfigs().get(this.difficulty.ordinal());
      }

      int streak = 0;
      int highest = 0;
      MasterStats ms = com.tann.dice.Main.self().masterStats;
      Stat ss = ms.getStat(FurthestReachedStat.getName(cc));
      if (ss != null) {
         highest = ss.getValue();
      }

      ss = ms.getStat(BestStreakStat.getName(cc));
      if (ss != null) {
         streak = ss.getValue();
      }

      return this.encode(streak, highest);
   }

   public static String streakScoreName(int value) {
      if (value <= 20) {
         return "fight " + value;
      } else {
         return value < 101 ? "???" : value - 100 + "-streak";
      }
   }

   @Override
   public String getScoreString(int value) {
      return streakScoreName(value);
   }

   private int encode(int streak, int highest) {
      return streak > 0 ? 100 + streak : highest;
   }

   @Override
   public boolean isUnavailable() {
      return UnUtil.isLocked(this.difficulty) || UnUtil.isLocked(this.mode);
   }

   @Override
   public String getSuperName() {
      return this.mode.getTextButtonName();
   }
}
