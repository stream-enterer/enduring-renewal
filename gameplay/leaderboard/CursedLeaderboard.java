package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.FurthestReachedStat;

public class CursedLeaderboard extends Leaderboard {
   final Mode mode;

   public CursedLeaderboard(Mode mode) {
      super(mode.getName(), "Highest fight reached in " + mode.getTextButtonName(), mode.getColour(), makeUrl(mode), "level", 9, true);
      this.mode = mode;
   }

   private static String makeUrl(Mode mode) {
      String pref;
      if (mode == Mode.BLURSED) {
         pref = "blursed";
      } else if (mode == Mode.BLURTRA) {
         pref = "blurtra";
      } else if (mode == Mode.BLYPTRA) {
         pref = "blyptra";
      } else {
         pref = mode.getName().replaceAll(" ", "_");
      }

      return pref + "_highest";
   }

   @Override
   public String getScoreString(int value) {
      return "" + value;
   }

   @Override
   public int getScore() {
      Stat s = com.tann.dice.Main.self().masterStats.getStat(FurthestReachedStat.getName(this.mode.getConfigs().get(0)));
      return s == null ? 0 : s.getValue();
   }

   @Override
   public boolean internalValid(Mode m, Difficulty d) {
      return m == this.mode;
   }

   @Override
   public boolean isUnavailable() {
      return UnUtil.isLocked(this.mode);
   }

   @Override
   public String getSuperName() {
      return "[purple]Cursed";
   }
}
