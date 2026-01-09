package com.tann.dice.gameplay.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.leaderboardStat.LeaderboardStat;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public abstract class Leaderboard implements Unlockable {
   public static final String LEADERBOARDS_URL_3 = "https://tann.fun/leaderboards/slice_and_dice31/";
   public static final int LIMIT = 10;
   public static final int LIMIT_FOR_COMPARE = 5;
   private final String description;
   final String name;
   final String url;
   final String scoreName;
   final Color col;
   final boolean keepHighest;
   final int requiredScore;
   Array<LeaderboardEntry> entries = null;
   public boolean failed;
   Runnable onSuccess;
   Runnable onFail;
   private int dataFetchedAmt = 0;

   public Leaderboard(String name, Color col, String url, String scoreName, int requiredScore, boolean keepHighest) {
      this(name, null, col, url, scoreName, requiredScore, keepHighest);
   }

   public Leaderboard(String name, String description, Color col, String url, String scoreName, int requiredScore, boolean keepHighest) {
      this.name = name;
      this.description = "[text]" + description;
      this.url = url;
      this.scoreName = scoreName;
      this.col = col;
      this.requiredScore = requiredScore;
      this.keepHighest = keepHighest;
   }

   public boolean validForModeInfo(ContextConfig contextConfig) {
      return this.validForEndCard(contextConfig);
   }

   public final boolean validForEndCard(ContextConfig contextConfig) {
      if (UnUtil.isLocked(this)) {
         return false;
      } else {
         Mode m = contextConfig.mode;
         Difficulty d = null;
         if (contextConfig instanceof DifficultyConfig) {
            d = ((DifficultyConfig)contextConfig).getDifficulty();
         }

         return this.internalValid(m, d);
      }
   }

   protected boolean internalValid(Mode m, Difficulty d) {
      return false;
   }

   protected String getFullURL(LeaderboardDisplaySettings settings) {
      return "https://tann.fun/leaderboards/slice_and_dice31/" + this.url + settings.getUrl();
   }

   private void makeGetRequest(LeaderboardDisplaySettings settings) {
      this.failed = false;
      this.entries = null;
      HttpRequest request = new HttpRequestBuilder().newRequest().method("GET").timeout(8000).url(this.getFullURL(settings)).build();
      Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
         public void handleHttpResponse(HttpResponse httpResponse) {
            String response = httpResponse.getResultAsString();

            try {
               Leaderboard.this.entries = (Array<LeaderboardEntry>)com.tann.dice.Main.getJson().fromJson(Array.class, LeaderboardEntry.class, response);
            } catch (Exception var4) {
               var4.printStackTrace();
               if (Leaderboard.this.onFail != null) {
                  Leaderboard.this.onFail.run();
               }

               return;
            }

            if (Leaderboard.this.entries == null) {
               if (Leaderboard.this.onFail != null) {
                  Leaderboard.this.onFail.run();
               }
            } else if (Leaderboard.this.onSuccess != null) {
               Leaderboard.this.internalOnSuccess();
               Gdx.app.postRunnable(Leaderboard.this.onSuccess);
            }
         }

         public void failed(Throwable t) {
            if (Leaderboard.this.onFail != null) {
               Gdx.app.postRunnable(Leaderboard.this.onFail);
            }

            Leaderboard.this.failed = true;
            TannLog.log("leaderboard GET request failed; " + t.getMessage(), TannLog.Severity.error);
         }

         public void cancelled() {
            Leaderboard.this.failed = true;
            TannLog.log("leaderboard GET request cancelled");
         }
      });
   }

   private void internalOnSuccess() {
      this.dataFetchedAmt++;
   }

   public int getDataFetchedAmt() {
      return this.dataFetchedAmt;
   }

   public String getScoreString(int value) {
      return value + "";
   }

   public void postScore(String name, final int score, final Runnable onSuccess, final Runnable onFail) {
      LeaderboardPostRequest lpr = new LeaderboardPostRequest(
         name, com.tann.dice.Main.getSettings().getHighscoreIdentifier(), score, this.url, com.tann.dice.Main.self().control.getHighscorePlatformString()
      );
      HttpRequest request = new HttpRequestBuilder()
         .newRequest()
         .method("POST")
         .timeout(5000)
         .url("https://tann.fun/leaderboards/slice_and_dice31/" + this.url)
         .header("Content-Type", "application/json")
         .content(com.tann.dice.Main.getJson().toJson(lpr))
         .build();
      Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
         public void handleHttpResponse(HttpResponse httpResponse) {
            String response = httpResponse.getResultAsString().trim();
            if (!response.startsWith("success")) {
               TannLog.log("leaderboard post request failed: " + response);
               onFail.run();
            } else {
               TannLog.log("leaderboard post request succeeded: " + response);
               onSuccess.run();
               com.tann.dice.Main.self().masterStats.getStat(LeaderboardStat.getName(Leaderboard.this)).setValue(score);
               com.tann.dice.Main.self().masterStats.saveAll();
            }
         }

         public void failed(Throwable t) {
            TannLog.log("request failed: " + t.getMessage());
            onFail.run();
         }

         public void cancelled() {
            TannLog.log("request cancelled");
            onFail.run();
         }
      });
   }

   public String getName() {
      return this.name;
   }

   public String getColouredName() {
      return "[notranslate]" + TextWriter.getTag(this.col) + com.tann.dice.Main.t(this.name) + "[cu]";
   }

   public Color getCol() {
      return this.col;
   }

   public void makeRequest(Runnable onSuccess, Runnable onFail) {
      this.makeRequest(new LeaderboardDisplaySettings(), onSuccess, onFail);
   }

   public void makeRequest(LeaderboardDisplaySettings settings, Runnable onSuccess, Runnable onFail) {
      this.onSuccess = onSuccess;
      this.onFail = onFail;
      this.makeGetRequest(settings);
   }

   public Array<LeaderboardEntry> getEntries() {
      return this.entries;
   }

   public void clearCache() {
      this.entries = null;
   }

   public String getScoreName() {
      return this.scoreName;
   }

   public StandardButton getSubmitHighscoreButton() {
      StandardButton tb = new StandardButton(TextWriter.getTag(this.col) + "Submit Highscore");
      tb.setRunnable(new Runnable() {
         @Override
         public void run() {
            Actor a = Leaderboard.this.getSubmitHighscorePanel();
            com.tann.dice.Main.getCurrentScreen().push(a, true, true, false, 0.7F);
            Tann.center(a);
            Tann.slideIn(a, Tann.TannPosition.Top, (int)((com.tann.dice.Main.height - a.getHeight()) / 2.0F));
         }
      });
      return tb;
   }

   public abstract int getScore();

   public Group getSubmitHighscorePanel() {
      return new SubmitHighscorePanel(this, this.getScore());
   }

   public boolean isKeepHighest() {
      return this.keepHighest;
   }

   public boolean isScoreBetter(int potentialScore) {
      int previousSubmitted = com.tann.dice.Main.self().masterStats.getStat(LeaderboardStat.getName(this)).getValue();
      if (!this.isScoreHighEnough(potentialScore)) {
         return false;
      } else if (potentialScore < 1) {
         return false;
      } else if (previousSubmitted == -1) {
         return true;
      } else {
         return this.isKeepHighest() ? potentialScore > previousSubmitted : potentialScore < previousSubmitted;
      }
   }

   public boolean isScoreHighEnough(int score) {
      if (this.isKeepHighest()) {
         if (score < this.requiredScore) {
            return false;
         }
      } else if (score > this.requiredScore) {
         return false;
      }

      return true;
   }

   public boolean canSubmitBetterScore() {
      return this.isScoreBetter(this.getScore());
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return new TextWriter(this.getColouredName());
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return TextWriter.getTag(this.col) + "L";
   }

   public String getDescription() {
      return this.description;
   }

   public StandardButton makeTextButton() {
      return new StandardButton(this.getColouredName());
   }

   public boolean isUnavailable() {
      return false;
   }

   public String getRequiredScoreString() {
      return this.getScoreString(this.requiredScore);
   }

   public Actor makeInfoActor() {
      return this.makeInfoActor(this.getColouredName());
   }

   public Actor makeInfoActor(String title) {
      StandardButton sb = new StandardButton(title);
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            Actor a = new LeaderboardDisplay(Leaderboard.this);
            com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.8F, false);
         }
      });
      return sb;
   }

   public String getSuperName() {
      return this.name;
   }

   public String getStatName() {
      return this.getSuperName() + ":" + this.getName();
   }

   public boolean disableSubmit() {
      return false;
   }
}
