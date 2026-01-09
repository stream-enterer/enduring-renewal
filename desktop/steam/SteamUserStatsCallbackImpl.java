package com.tann.dice.desktop.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserStatsCallback;

public class SteamUserStatsCallbackImpl implements SteamUserStatsCallback {
   private final SteamControl steamControl;

   SteamUserStatsCallbackImpl(SteamControl steamControl) {
      this.steamControl = steamControl;
   }

   public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
      if (result == SteamResult.OK) {
         this.steamControl.syncAchievements();
      }
   }

   public void onUserStatsStored(long gameId, SteamResult result) {
   }

   public void onUserStatsUnloaded(SteamID steamIDUser) {
   }

   public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName, int curProgress, int maxProgress) {
   }

   public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
   }

   public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries, int numEntries) {
   }

   public void onLeaderboardScoreUploaded(
      boolean success, SteamLeaderboardHandle leaderboard, int score, boolean scoreChanged, int globalRankNew, int globalRankPrevious
   ) {
   }

   public void onNumberOfCurrentPlayersReceived(boolean success, int players) {
   }

   public void onGlobalStatsReceived(long gameId, SteamResult result) {
   }
}
