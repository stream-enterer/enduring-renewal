package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.util.Tann;

public class LeaderboardPostRequest {
   final String author;
   final long author_identifier;
   final int score;
   final int check;
   final long time;
   final String platform;

   public LeaderboardPostRequest(String author, long author_identifier, int score, String leaderboardName, String platform) {
      this.author = author;
      this.author_identifier = author_identifier;
      this.score = score;
      this.check = Tann.hash(score, leaderboardName);
      this.time = System.currentTimeMillis();
      this.platform = platform;
   }
}
