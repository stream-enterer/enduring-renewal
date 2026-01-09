package com.tann.dice.gameplay.leaderboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;

public class LeaderboardDisplaySettings {
   public final LeaderboardDisplaySettings.LeaderboardDisplaySettingsType type;
   public long arg;

   public LeaderboardDisplaySettings() {
      this(LeaderboardDisplaySettings.LeaderboardDisplaySettingsType.Page);
   }

   public LeaderboardDisplaySettings(LeaderboardDisplaySettings.LeaderboardDisplaySettingsType type) {
      this.type = type;
   }

   public LeaderboardDisplaySettings(long playerId) {
      this.type = LeaderboardDisplaySettings.LeaderboardDisplaySettingsType.Me;
      this.arg = playerId;
   }

   public Actor makeActor(final LeaderboardDisplay display) {
      if (this.type != LeaderboardDisplaySettings.LeaderboardDisplaySettingsType.Page) {
         return null;
      } else {
         Array<LeaderboardEntry> entries = display.leaderboard.getEntries();
         boolean full = entries != null && entries.size == 10;
         if (this.arg == 0L && !full) {
            return null;
         } else {
            Pixl p = new Pixl();
            int gap = 3;

            for (int i = -1; i <= 1; i++) {
               if (i == 0) {
                  p.text("page " + (this.arg + 1L));
               } else {
                  boolean disabled = false;
                  if (i == -1 && this.arg == 0L) {
                     disabled = true;
                  }

                  if (i == 1 && !full) {
                     disabled = true;
                  }

                  Color col = disabled ? Colours.grey : Colours.light;
                  Actor pageButton = new Pixl(2, 2).border(col).text(TextWriter.getTag(col) + (i == -1 ? "<" : ">")).pix();
                  if (!disabled) {
                     final int finalI = i;
                     pageButton.addListener(new TannListener() {
                        @Override
                        public boolean action(int button, int pointer, float x, float y) {
                           LeaderboardDisplaySettings.this.arg = LeaderboardDisplaySettings.this.arg + finalI;
                           display.leaderboard.makeRequest(LeaderboardDisplaySettings.this, new Runnable() {
                              @Override
                              public void run() {
                                 display.layout();
                              }
                           }, new Runnable() {
                              @Override
                              public void run() {
                                 display.layout();
                              }
                           });
                           return true;
                        }
                     });
                  }

                  p.actor(pageButton);
               }

               if (i != 1) {
                  p.gap(3);
               }
            }

            return p.pix();
         }
      }
   }

   public String getUrl() {
      switch (this.type) {
         case Page:
            return "?page=" + this.arg + "&limit=" + 10;
         case Me:
            return "?playerId=" + this.arg + "&limit=" + 5;
         default:
            throw new RuntimeException(this.type + "??");
      }
   }

   public static enum LeaderboardDisplaySettingsType {
      Page,
      Me;
   }
}
