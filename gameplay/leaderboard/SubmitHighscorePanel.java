package com.tann.dice.gameplay.leaderboard;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextEfficientGroup;
import com.tann.dice.util.ui.TextInputField;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class SubmitHighscorePanel extends TextEfficientGroup {
   public static final int MAX_HIGHSCORE_NAME = 10;
   final Leaderboard leaderboard;
   TextInputField textInputField;
   LeaderboardDisplay leaderboardDisplay = null;
   final int score;
   Boolean postedSuccessfully;
   Boolean gettenSuccessfully;
   public static final String DEFAULT_NAME = "enter name";

   public SubmitHighscorePanel(final Leaderboard leaderboard, int score) {
      this.leaderboard = leaderboard;
      this.score = score;
      this.textInputField = new TextInputField(com.tann.dice.Main.getSettings().getHighscoreName(), 10);
      this.textInputField.setOnTextUpdate(new Runnable() {
         @Override
         public void run() {
            if (SubmitHighscorePanel.this.textInputField.text != null) {
               String rb = TextWriter.rebracketTags(SubmitHighscorePanel.this.textInputField.text);
               SubmitHighscorePanel.this.textInputField.setText(rb);
               com.tann.dice.Main.getSettings().setHighscoreName(rb);
            }
         }
      });
      this.layout();
      leaderboard.makeRequest(new Runnable() {
         @Override
         public void run() {
            SubmitHighscorePanel.this.gettenSuccessfully = true;
            SubmitHighscorePanel.this.leaderboardDisplay = new LeaderboardDisplay(leaderboard);
            SubmitHighscorePanel.this.layout();
         }
      }, new Runnable() {
         @Override
         public void run() {
            SubmitHighscorePanel.this.gettenSuccessfully = false;
            SubmitHighscorePanel.this.layout();
         }
      });
   }

   private void layout() {
      this.clearChildren();
      int gap = 3;
      Pixl p = new Pixl(gap, gap).forceWidth(LeaderboardDisplay.baseWidth() + gap * 2 + 6);
      final Runnable success = new Runnable() {
         @Override
         public void run() {
            SubmitHighscorePanel.this.leaderboard
               .makeRequest(
                  new Runnable() {
                     @Override
                     public void run() {
                        SubmitHighscorePanel.this.leaderboardDisplay
                           .setNewDisplaySettings(new LeaderboardDisplaySettings(com.tann.dice.Main.getSettings().getHighscoreIdentifier()));
                        SubmitHighscorePanel.this.postedSuccessfully = true;
                        SubmitHighscorePanel.this.layout();
                     }
                  },
                  null
               );
         }
      };
      final Runnable fail = new Runnable() {
         @Override
         public void run() {
            SubmitHighscorePanel.this.postedSuccessfully = false;
            SubmitHighscorePanel.this.layout();
         }
      };
      StandardButton tb = new StandardButton("submit");
      tb.setRunnable(new Runnable() {
         @Override
         public void run() {
            if (SubmitHighscorePanel.this.score == 0) {
               com.tann.dice.Main.getCurrentScreen().showDialog("[red]Cannot submit a score of 0");
            } else if ("enter name".equals(SubmitHighscorePanel.this.textInputField.text)) {
               com.tann.dice.Main.getCurrentScreen().showDialog("[red]Please tap 'enter name' to enter a name");
            } else {
               SubmitHighscorePanel.this.leaderboard.postScore(SubmitHighscorePanel.this.textInputField.text, SubmitHighscorePanel.this.score, success, fail);
            }
         }
      });
      p.text("name:")
         .actor(this.textInputField)
         .row()
         .text(this.leaderboard.getScoreName() + ": " + this.leaderboard.getScoreString(this.score))
         .row()
         .actor(tb)
         .row();
      if (this.postedSuccessfully != null) {
         p.text(this.postedSuccessfully ? "[green]success" : "[red]failed").row();
      }

      Actor content;
      if (this.leaderboardDisplay != null) {
         content = this.leaderboardDisplay;
      } else {
         String waiting = this.gettenSuccessfully == null ? "Fetching scores..." : "[red]Failed to get scores :(";
         content = new TextWriter(waiting);
      }

      ScrollPane sp = Tann.makeScrollpane(content);
      sp.setWidth(content.getWidth() + 6.0F);
      sp.setHeight(80.0F);
      p.actor(sp);
      Tann.become(this, p.pix());
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }
}
