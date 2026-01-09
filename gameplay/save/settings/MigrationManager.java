package com.tann.dice.gameplay.save.settings;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.TannLog;

public class MigrationManager {
   public static void manageMigration(String newVersion, String prevVersion, Settings settings) {
      if (prevVersion != null) {
         genericMigration();
         if (com.tann.dice.Main.versionALower(prevVersion, "2.0.2")) {
            TannLog.log("Previous version was below 2.0.2 so migrating");
            settings.save();
         }

         if (prevVersion.equalsIgnoreCase("3.1.13")) {
            try {
               int preChievs = com.tann.dice.Main.unlockManager().getCompletedAchievements().size();
               TannLog.log("Migrating achivements from 3.0");
               migrateAchievementsIssue();
               if (com.tann.dice.Main.unlockManager().getCompletedAchievements().size() != preChievs) {
                  settings.save();
                  com.tann.dice.Main.getCurrentScreen().showDialog("Migrated some achievements");
               }
            } catch (Exception var4) {
               TannLog.error(var4, "chievccc");
               com.tann.dice.Main.getCurrentScreen().showDialog("Error code x551c");
            }
         }
      }
   }

   private static void migrateAchievementsIssue() {
      handleAchievementMigration("winner", null, null);
      handleAchievementMigration("[orange]Hard[cu] victory", null, Difficulty.Hard);
      handleAchievementMigration("[orange]Raid[cu] victory", Mode.RAID, null);
      handleAchievementMigration("[red]Unfair[cu] victory", null, Difficulty.Unfair);
      handleAchievementMigration("[purple]Brutal[cu] victory", null, Difficulty.Brutal);
      handleAchievementMigration("[red]Generate[cu] victory", Mode.GENERATE_HEROES, null);
      handleAchievementMigration("[red]Generate[cu] [orange]Hard[cu] victory", Mode.GENERATE_HEROES, Difficulty.Hard);
      handleAchievementMigration("[red]Generate[cu] [red]Unfair[cu] victory", Mode.GENERATE_HEROES, Difficulty.Unfair);
      handleAchievementMigration("[blue]Dream[cu] [orange]Hard[cu] victory", Mode.DREAM, Difficulty.Hard);
      handleAchievementMigration("[pink]Hell[cu] victory", null, Difficulty.Hell);
   }

   private static void handleAchievementMigration(String achName, Mode m, Difficulty d) {
      if (checkWinAchievement(m, d)) {
         com.tann.dice.Main.unlockManager().achieveFromMigration(achName);
      }
   }

   private static boolean checkWinAchievement(Mode m, Difficulty d) {
      for (Mode mode : Mode.getPlayableModes()) {
         if (m == null || m == mode) {
            for (ContextConfig config : mode.getConfigs()) {
               if (config instanceof DifficultyConfig) {
                  DifficultyConfig dc = (DifficultyConfig)config;
                  if ((d == null || d == dc.getDifficulty()) && config.getWins() > 0) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static void genericMigration() {
      com.tann.dice.Main.self().settings.clearAllAnticheese();
      com.tann.dice.Main.self().masterStats.getUnlockManager().updateAfterAchieve();
   }
}
