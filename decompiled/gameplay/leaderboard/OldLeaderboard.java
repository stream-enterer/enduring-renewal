package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.List;

public class OldLeaderboard extends Leaderboard {
   private String url;
   private String game;

   public OldLeaderboard(String game, String url) {
      super(transformUrlToName(url), "[notranslate]" + getDesc(game, url), Colours.grey, url, getScInit(url), -1, true);
      this.url = url;
      this.game = game;
   }

   private static String getDesc(String game, String url) {
      return describeVersion(game) + " archived leaderboard";
   }

   private static String describeVersion(String game) {
      switch (game) {
         case "slice_and_dice":
            return "S&D 1.0";
         case "slice_and_dice2":
            return "S&D 2.0";
         case "slice_and_dice3":
            return "S&D 3.0";
         default:
            return "old version";
      }
   }

   private static String transformUrlToName(String url) {
      url = url.toLowerCase();
      List<Mode> allModes = Mode.getAllModes();

      for (int i = allModes.size() - 1; i >= 0; i--) {
         Mode allMode = allModes.get(i);
         url = url.replace(TextWriter.stripTags(allMode.getName().toLowerCase()), allMode.getTextButtonName());
      }

      return url.replaceAll("_", " ")
         .replace("bones", "[bones]")
         .replaceAll("alpha", "[alpha]")
         .replaceAll("standard", "[green]standard[cu]")
         .replaceAll("speedrun", "[yellow]speedrun[cu]")
         .replaceAll(" streak", "")
         .replaceAll("streak ", "")
         .replaceAll("highest ", "")
         .replaceAll(" highest", "")
         .replaceAll("hard", Difficulty.Hard.getColourTaggedName())
         .replaceAll("unfair", Difficulty.Unfair.getColourTaggedName())
         .replaceAll("brutal", Difficulty.Brutal.getColourTaggedName())
         .replaceAll("hell", Difficulty.Hell.getColourTaggedName());
   }

   @Override
   public String getColouredName() {
      return "[notranslate]" + TextWriter.getTag(this.col) + this.name + "[cu]";
   }

   private static String getScInit(String url) {
      if (!url.contains("ills") && !url.contains("efeated") && !url.contains("losses")) {
         Leaderboard lb = getSimilar(url);
         return lb instanceof StreakLeaderboard ? "streak" : lb.scoreName;
      } else {
         return "#";
      }
   }

   @Override
   protected String getFullURL(LeaderboardDisplaySettings settings) {
      return "https://tann.fun/leaderboards/" + this.game + "/" + this.url + settings.getUrl();
   }

   @Override
   public String getSuperName() {
      return describeVersion(this.game);
   }

   private Leaderboard getSimilar() {
      return getSimilar(this.url);
   }

   private static Leaderboard getSimilar(String url) {
      String lcu = url.toLowerCase();
      if (lcu.contains("streak")) {
         return new StreakLeaderboard(Mode.CLASSIC, Difficulty.Normal, "blah");
      } else if (lcu.contains("urse")) {
         return new CursedLeaderboard(Mode.CURSE);
      } else {
         return (Leaderboard)(!lcu.contains("fastest") && !lcu.contains("slowest") && !lcu.contains("eedrun")
            ? new CursedLeaderboard(Mode.CURSE)
            : new SpeedrunLeaderboard(Mode.CLASSIC));
      }
   }

   @Override
   public boolean canSubmitBetterScore() {
      return false;
   }

   @Override
   public boolean validForModeInfo(ContextConfig contextConfig) {
      return false;
   }

   @Override
   public StandardButton getSubmitHighscoreButton() {
      return null;
   }

   @Override
   public boolean isScoreHighEnough(int score) {
      return false;
   }

   @Override
   public boolean disableSubmit() {
      return true;
   }

   @Override
   public String getScoreString(int value) {
      return this.getSimilar().getScoreString(value);
   }

   @Override
   public int getScore() {
      return 0;
   }
}
