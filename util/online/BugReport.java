package com.tann.dice.util.online;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.VersionUtils;
import com.tann.dice.util.bsRandom.Supplier;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class BugReport {
   public static final String URL = "https://tann.fun/bug/slice_and_dice";
   String version;
   String author;
   String description;
   String content;

   public BugReport(String version, String author, String description, String content) {
      this.version = version;
      this.author = author;
      this.description = description;
      this.content = content;
   }

   public static Group makeBugReportPanel(String title, String info, final String contentInput, final boolean describe) {
      if (contentInput.length() > 5100) {
         contentInput = contentInput.substring(0, 5000) + "(truncated, actual length: " + contentInput.length();
      }

      if (com.tann.dice.Main.getCurrentScreen() instanceof TitleScreen) {
         TitleScreen ts = (TitleScreen)com.tann.dice.Main.getCurrentScreen();
         Mode m = ts.currentMode;
         String rawSave = m.getRawSave();
         if (rawSave != null) {
            contentInput = contentInput + " TSMODE `" + rawSave + "`";
         }
      }

      Sounds.playSound(Sounds.pip);
      StandardButton automated = new StandardButton("[green]Send automated report");
      automated.setRunnable(new Runnable() {
         @Override
         public void run() {
            Runnable success = new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  com.tann.dice.Main.getCurrentScreen().showPopupDialog("[green]Bug submitted successfully", Colours.green);
               }
            };
            Runnable fail = new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.getCurrentScreen().showPopupDialog("[red]Bug submit failed, maybe try copying to clipboard", Colours.red);
               }
            };
            BugReport.initiateAutomatedReport(contentInput, success, fail, describe);
         }
      });
      StandardButton copy = new StandardButton("[yellow]Copy report to clipboard");
      copy.setRunnable(new Runnable() {
         @Override
         public void run() {
            ClipboardUtils.copyWithSoundAndToast(contentInput);
            com.tann.dice.Main.getCurrentScreen().popSingleMedium();
         }
      });
      return new Pixl(3, 3).border(Colours.grey).text(title).row().text(info, 170).row().actor(automated).row().actor(copy).pix();
   }

   public static StandardButton makeBugReportButton(final String title, final String info, final Supplier<String> content, final boolean describe) {
      StandardButton report = new StandardButton("Bug");
      report.setRunnable(new Runnable() {
         @Override
         public void run() {
            Group bugReportGroup = BugReport.makeBugReportPanel(title, info, content.supply(), describe);
            com.tann.dice.Main.getCurrentScreen().push(bugReportGroup, true, true, false, 0.7F);
            Tann.center(bugReportGroup);
         }
      });
      return report;
   }

   public static void initiateAutomatedReport(String content, final Runnable success, final Runnable fail, boolean describe) {
      String separator = "  |  ";
      final String contentToSubmit = VersionUtils.versionName + separator + TannLog.newlinedLogs() + separator + content;
      if (!describe) {
         submitBugReport(
            new BugReport(VersionUtils.versionName, com.tann.dice.Main.getSettings().getHighscoreName(), "no description", contentToSubmit), success, fail
         );
      } else {
         com.tann.dice.Main.self().control.textInput(new TextInputListener() {
            public void input(String description) {
               BugReport bugReport = new BugReport(VersionUtils.versionName, com.tann.dice.Main.getSettings().getHighscoreName(), description, contentToSubmit);
               BugReport.submitBugReport(bugReport, success, fail);
            }

            public void canceled() {
            }
         }, "Description", "", "optional description");
      }
   }

   private static void submitBugReport(BugReport bugReport, final Runnable success, final Runnable fail) {
      HttpRequest request = new HttpRequestBuilder()
         .newRequest()
         .method("POST")
         .timeout(5000)
         .url("https://tann.fun/bug/slice_and_dice")
         .header("Content-Type", "application/json")
         .content(com.tann.dice.Main.getJson().toJson(bugReport))
         .build();
      Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
         public void handleHttpResponse(HttpResponse httpResponse) {
            String response = httpResponse.getResultAsString().trim();
            TannLog.log("bug submitted: " + response);
            if (response.startsWith("success")) {
               if (success != null) {
                  success.run();
               }
            } else if (fail != null) {
               fail.run();
            }
         }

         public void failed(Throwable t) {
            TannLog.log("bug request failed");
            if (fail != null) {
               fail.run();
            }
         }

         public void cancelled() {
            TannLog.log("bug request cancelled");
            if (fail != null) {
               fail.run();
            }
         }
      });
   }
}
