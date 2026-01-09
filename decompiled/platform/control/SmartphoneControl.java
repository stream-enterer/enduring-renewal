package com.tann.dice.platform.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpSnippet;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialQuest;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.TannLog;
import java.util.Arrays;
import java.util.List;

public abstract class SmartphoneControl extends Control {
   @Override
   public void affectAdvertLines(List<String> lines) {
   }

   @Override
   public String getSelectTapString() {
      return "Tap";
   }

   @Override
   public String getInfoTapString() {
      return "Tap and hold";
   }

   @Override
   public boolean allowLongPress() {
      return true;
   }

   @Override
   public int getConfirmButtonThumbpadRadius() {
      return com.tann.dice.Main.isPortrait() ? -1 : 18;
   }

   @Override
   public List<TutorialQuest> getExtraTargetingPhaseQuests() {
      return Arrays.asList(makeRotate(true), makeRotate(false));
   }

   private static TutorialQuest makeRotate(final boolean portrait) {
      String n = portrait ? "portrait" : "landscape";
      String rem = "[n][grey](may require device auto-rotate)";
      if (portrait) {
         rem = "[n][grey](may require [cog]->landscape lock)";
      }

      return new TutorialQuest(0, "Try rotating your device to " + n + rem) {
         @Override
         public boolean isValid(FightLog fightLog) {
            return com.tann.dice.Main.isPortrait() != portrait && fightLog.getContext().getCurrentLevelNumber() > 2;
         }

         @Override
         public void loadIn() {
            if (com.tann.dice.Main.isPortrait() == portrait) {
               this.markCompleted();
            }
         }
      };
   }

   @Override
   public boolean saveContentEncryption() {
      return false;
   }

   @Override
   public String getMainFileString() {
      return "slice-and-dice-3";
   }

   @Override
   public List<Actor> getTipsSnippets(int contentWidth) {
      return Arrays.asList(
         new HelpSnippet(
            "It's maybe possible to play with one hand in portrait orientation, use the ui option 'longtap end' to help with corner buttons. And remember you can also tap heroes to lock/unlock their dice."
         )
      );
   }

   protected static void showDialogOnMainThread(final String msg) {
      Gdx.app.postRunnable(new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().showDialog(msg);
         }
      });
   }

   protected static void refreshModesPanel() {
      Gdx.app.postRunnable(new Runnable() {
         @Override
         public void run() {
            if (com.tann.dice.Main.getCurrentScreen() instanceof TitleScreen) {
               TitleScreen screen = (TitleScreen)com.tann.dice.Main.getCurrentScreen();
               screen.getModesPanel().layout();
            }
         }
      });
   }

   public static void showPurchaseErrorToUser(Throwable e) {
      String msg = "[red]Error purchasing IAP: [grey]" + getStringFromThrowable(e);
      TannLog.log(msg, TannLog.Severity.error);
      showDialogOnMainThread(msg);
   }

   public static void showRestoreError(Throwable e, boolean restorePressed) {
      String msg = "[red]Error restoring purchase: [grey]" + getStringFromThrowable(e);
      TannLog.log(msg, TannLog.Severity.error);
      if (restorePressed) {
         showDialogOnMainThread(msg);
      }
   }

   private static String getStringFromThrowable(Throwable e) {
      if (e == null) {
         return "null-throwable";
      } else {
         return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
      }
   }
}
