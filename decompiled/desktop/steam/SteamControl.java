package com.tann.dice.desktop.steam;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamUserStats;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.AchievementCompletionListener;
import com.tann.dice.platform.control.desktop.DesktopControl;
import com.tann.dice.util.Colours;
import java.util.List;

public class SteamControl extends DesktopControl {
   private SteamUserStats userStats = null;
   private boolean hasRunSetup = false;

   @Override
   public Color getCol() {
      return Colours.grey;
   }

   @Override
   public Preferences makePrefs(String name) {
      return new CopyOfLwjgl3Prefs(name);
   }

   @Override
   public boolean stupidAboutLinks() {
      return true;
   }

   @Override
   public String getFullVersionURL() {
      return "https://store.steampowered.com/app/1775490/Slice__Dice/";
   }

   private void setup() {
      try {
         SteamAPI.loadLibraries();
         if (!SteamAPI.init()) {
            System.err.println("Could not initialise Steam");
            return;
         }

         this.userStats = new SteamUserStats(new SteamUserStatsCallbackImpl(this));
         this.userStats.requestCurrentStats();
      } catch (SteamException var2) {
         var2.printStackTrace();
      }

      com.tann.dice.Main.unlockManager().registerAchievementListener(new AchievementCompletionListener() {
         @Override
         public void onUnlock(Achievement a) {
            if (SteamControl.this.userStats != null) {
               try {
                  SteamControl.this.userStats.setAchievement(SteamControl.this.getAchievementId(a));
                  SteamControl.this.userStats.storeStats();
               } catch (Exception var3) {
                  System.err.println("Unable to set achievement " + a.getName());
               }
            }
         }
      });
   }

   private String getAchievementId(Achievement a) {
      return a.getName().replaceAll("\\[[^\\]]+\\]", "").toLowerCase().replaceAll(" ", "-").replaceAll("!", "");
   }

   void syncAchievements() {
      if (this.userStats != null) {
         List<Achievement> cheevos = com.tann.dice.Main.unlockManager().getCompletedAchievements();
         boolean atLeastOneUnlocked = false;

         for (int i = 0; i < cheevos.size(); i++) {
            Achievement achievement = cheevos.get(i);
            String id = this.getAchievementId(achievement);
            boolean isAchieved = this.userStats.isAchieved(id, false);
            if (!isAchieved) {
               System.out.println("Unlocking " + achievement.getName() + ": " + id);
               this.userStats.setAchievement(id);
               atLeastOneUnlocked = true;
            }
         }

         if (atLeastOneUnlocked) {
            this.userStats.storeStats();
         }
      }
   }

   @Override
   public void onStop() {
      if (this.userStats != null) {
         this.userStats.dispose();
      }

      SteamAPI.shutdown();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.hasRunSetup) {
         this.setup();
         this.hasRunSetup = true;
      }

      if (SteamAPI.isSteamRunning()) {
         SteamAPI.runCallbacks();
      }
   }
}
