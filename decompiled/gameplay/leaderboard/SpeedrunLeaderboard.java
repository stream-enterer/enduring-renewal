package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.MasterStats;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfRun.SpeedrunStat;
import com.tann.dice.util.Tann;

public class SpeedrunLeaderboard extends Leaderboard {
   final Mode mode;

   public SpeedrunLeaderboard(Mode mode) {
      super(
         "[yellow]Speedrun",
         "[notranslate]"
            + com.tann.dice.Main.t(mode.getTextButtonName())
            + " "
            + com.tann.dice.Main.t(Difficulty.Normal.getColourTaggedName())
            + " "
            + com.tann.dice.Main.t(reminder(needsBasic(mode)))
            + "[b]",
         mode.getColour(),
         makeUrl(mode),
         "time",
         3600,
         false
      );
      this.mode = mode;
   }

   private static String reminder(boolean needsBasic) {
      return needsBasic ? "(basic+skip+no tweaks only)" : "(skip+no tweaks only)";
   }

   @Override
   public boolean isKeepHighest() {
      return false;
   }

   public static boolean needsBasic(Mode m) {
      return m != Mode.CHOOSE_PARTY && m != Mode.RAID;
   }

   private static String makeUrl(Mode mode) {
      return "speedrun_" + mode.getName().toLowerCase();
   }

   @Override
   public boolean internalValid(Mode m, Difficulty d) {
      return m == this.mode && d == Difficulty.Normal;
   }

   @Override
   public String getScoreString(int value) {
      return Tann.parseSeconds(value);
   }

   @Override
   public int getScore() {
      ContextConfig cc = this.mode.getConfigs().get(Difficulty.Normal.ordinal());
      MasterStats ms = com.tann.dice.Main.self().masterStats;
      Stat ss = ms.getStat(SpeedrunStat.getName(cc));
      return ss == null ? 69 : ss.getValue();
   }

   @Override
   public boolean isUnavailable() {
      return UnUtil.isLocked(this.mode);
   }

   @Override
   public String getSuperName() {
      return this.mode.getTextButtonName();
   }
}
