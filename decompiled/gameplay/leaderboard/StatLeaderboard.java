package com.tann.dice.gameplay.leaderboard;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.progress.stats.stat.Stat;

public class StatLeaderboard extends Leaderboard {
   final String statName;

   public StatLeaderboard(String statName, String title, Color col, String description, String url, String scoreName, int requiredScore, boolean keepHighest) {
      super(title, description, col, url, scoreName, requiredScore, keepHighest);
      this.statName = statName;
   }

   @Override
   public int getScore() {
      Stat s = com.tann.dice.Main.self().masterStats.getStat(this.statName);
      return s == null ? 0 : s.getValue();
   }
}
