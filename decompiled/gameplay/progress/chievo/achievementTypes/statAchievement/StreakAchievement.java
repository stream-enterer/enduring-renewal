package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.metaEnd.streak.BestStreakStat;
import java.util.Arrays;
import java.util.List;

public class StreakAchievement extends StatAchievement {
   final Mode mode;
   final Difficulty difficulty;

   public StreakAchievement(Mode mode, Difficulty difficulty, int streak, Unlockable... unlockable) {
      super(makeName(mode, difficulty), makeDescription(mode, difficulty, streak), findStatName(mode, difficulty), streak, unlockable);
      this.mode = mode;
      this.difficulty = difficulty;
   }

   private static String findStatName(Mode mode, Difficulty difficulty) {
      return BestStreakStat.getName(mode.getConfigs().get(difficulty.ordinal()));
   }

   private static String base(Mode mode, Difficulty difficulty, boolean colour) {
      String result = colour ? mode.getTextButtonName() : mode.getName();
      if (difficulty != null) {
         result = result + " " + (colour ? difficulty.getColourTaggedName() : difficulty);
      }

      return result;
   }

   private static String makeName(Mode mode, Difficulty difficulty) {
      return base(mode, difficulty, false) + " streak";
   }

   private static String makeDescription(Mode mode, Difficulty difficulty, int streak) {
      return "Get to a streak of " + streak + " on " + base(mode, difficulty, true);
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new StreakAchievement(Mode.CLASSIC, Difficulty.Normal, 2),
         new StreakAchievement(Mode.CLASSIC, Difficulty.Hard, 2),
         new StreakAchievement(Mode.CLASSIC, Difficulty.Unfair, 2),
         new StreakAchievement(Mode.CLASSIC, Difficulty.Brutal, 2),
         new StreakAchievement(Mode.CLASSIC, Difficulty.Hell, 2)
      );
   }

   @Override
   public boolean isCompletable() {
      return (this.mode == null || !UnUtil.isLocked(this.mode)) && (this.difficulty == null || !UnUtil.isLocked(this.difficulty));
   }
}
