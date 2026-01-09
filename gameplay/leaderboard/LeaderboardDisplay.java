package com.tann.dice.gameplay.leaderboard;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.List;

public class LeaderboardDisplay extends Group {
   LeaderboardDisplaySettings displaySettings = new LeaderboardDisplaySettings(LeaderboardDisplaySettings.LeaderboardDisplaySettingsType.Page);
   final Leaderboard leaderboard;

   public LeaderboardDisplay(Leaderboard leaderboard) {
      this.leaderboard = leaderboard;
      this.setSize(this.getWidth(), baseHeight());
      this.setTransform(false);
      this.layout();
   }

   public static int baseWidth() {
      return isCondensed() ? 120 : 212;
   }

   public static int baseHeight() {
      return isCondensed() ? 144 : 134;
   }

   public void layout() {
      this.layout(false);
   }

   private static boolean isCondensed() {
      return com.tann.dice.Main.isPortrait() || com.tann.dice.Main.self().translator.shouldTranslate();
   }

   public void layout(boolean fail) {
      this.clearChildren();
      this.setSize(baseWidth(), baseHeight());
      Pixl p = new Pixl(2);
      p.row(2).text(this.leaderboard.getColouredName()).row();
      if (this.leaderboard.getDescription() != null) {
         p.text(this.leaderboard.getDescription(), (int)(baseWidth() * 0.98F)).row();
      }

      if (!this.leaderboard.disableSubmit() && !this.leaderboard.isScoreHighEnough(this.leaderboard.getScore())) {
         String txt = com.tann.dice.Main.t("Qualifying score");
         p.text("[notranslate][grey]" + txt + ": " + this.leaderboard.getRequiredScoreString());
         if (this.leaderboard.getScore() != 0) {
            if (isCondensed()) {
               p.row();
            }

            String txt2 = com.tann.dice.Main.t("your score");
            p.text("[notranslate][grey] (" + txt2 + ": " + this.leaderboard.getScoreString(this.leaderboard.getScore()) + ")");
         }

         p.row();
      }

      if (fail) {
         p.actor(
            new Pixl(5, 5).border(Colours.red).text("[text]failed :(").row().text("[text]bug or server down").row().text("[text]or maybe your internet").row()
         );
      } else if (this.leaderboard.getEntries() == null) {
         Actor a = new Pixl(20).text("Loading...").pix();
         p.actor(a);
         this.leaderboard.makeRequest(this.displaySettings, new Runnable() {
            @Override
            public void run() {
               LeaderboardDisplay.this.layout();
            }
         }, new Runnable() {
            @Override
            public void run() {
               LeaderboardDisplay.this.failLayout();
            }
         });
      } else {
         int gap = 2;
         Pixl nameP = new Pixl(gap);
         Pixl scoreP = new Pixl(gap);
         Pixl submittedP = new Pixl(gap);
         Pixl positionP = new Pixl(gap);
         Pixl platformP = new Pixl(gap);
         long myId = com.tann.dice.Main.getSettings().getHighscoreIdentifier();
         String topColTag = "[text]";
         positionP.text("[text]#").row();
         nameP.text("[text]name").row();
         scoreP.text("[text]" + this.leaderboard.getScoreName()).row();
         submittedP.text("[text]submitted").row();
         platformP.text("[text]platform").row();
         Array<LeaderboardEntry> entriesArray = this.leaderboard.getEntries();
         List<LeaderboardEntry> entries = Tann.arrayToList(entriesArray);

         for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            boolean myRow = entry.author_identifier == myId;
            String rowPref = "[text]";
            if (myRow) {
               rowPref = "[pink]";
            }

            String authorString = entry.author;
            if (authorString == null || authorString.length() == 0) {
               authorString = "BLANK_NAME";
            }

            authorString = TextWriter.stripTags(authorString);
            int position = entry.position;
            String posPref;
            switch (position) {
               case 1:
                  posPref = "[yellow]";
                  break;
               case 2:
                  posPref = "[light]";
                  break;
               case 3:
                  posPref = "[orange]";
                  break;
               default:
                  posPref = myRow ? rowPref : "[text]";
            }

            positionP.text(posPref + position).row(2);
            nameP.text("[notranslate]" + posPref + Tann.makeEllipses(TextWriter.rebracketTags(authorString), 10)).row(2);
            scoreP.text("[notranslate]" + posPref + this.leaderboard.getScoreString(entry.score)).row(2);
            if (!isCondensed()) {
               submittedP.text(this.getTaggedTime(Tann.getTimeDescription(entry.submitted_time))).row(2);
               platformP.text(this.getPlatStringTagged(entry.platform)).row(2);
            }
         }

         Actor ppp = positionP.pix(8);
         int hhh = (int)ppp.getHeight();
         p.actor(ppp).actor(makeSeparator(hhh)).actor(nameP.pix(8)).actor(makeSeparator(hhh)).actor(scoreP.pix(8));
         if (!isCondensed()) {
            p.actor(makeSeparator(hhh)).actor(submittedP.pix(8)).actor(makeSeparator(hhh)).actor(platformP.pix(8));
         }

         Actor bottom = this.displaySettings.makeActor(this);
         if (bottom != null) {
            p.row().actor(bottom);
         }
      }

      Actor a = p.pix();
      this.addActor(a);
      Tann.center(a);
      a.setY(this.getHeight() - a.getHeight() - 2.0F);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }

   private String getTaggedTime(String d) {
      if (d.contains("days")) {
         return "[text]" + d;
      } else {
         return d.contains("day") ? "[light]" + d : "[grey]" + d;
      }
   }

   private String getPlatStringTagged(String platform) {
      if (platform == null) {
         return "[yellow]?";
      } else if (platform.equalsIgnoreCase("android")) {
         return "[green]" + platform;
      } else if (platform.equalsIgnoreCase("desktop")) {
         return "[text]" + platform;
      } else {
         return platform.equalsIgnoreCase("iOS") ? "[orange]" + platform : "[notranslate][pink]" + platform;
      }
   }

   private static Actor makeSeparator(int height) {
      return new Rectactor(1, height, Colours.grey);
   }

   private void failLayout() {
      this.layout(true);
   }

   public void setNewDisplaySettings(LeaderboardDisplaySettings leaderboardDisplaySettings) {
      this.displaySettings = leaderboardDisplaySettings;
      this.leaderboard.entries = null;
      this.layout();
   }
}
