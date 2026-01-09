package com.tann.dice.gameplay.save.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.custom.CustomPreset;
import com.tann.dice.gameplay.mode.creative.pastey.Scenario;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.gameplay.save.settings.option.BOption;
import com.tann.dice.gameplay.save.settings.option.ChOption;
import com.tann.dice.gameplay.save.settings.option.FlOption;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpPage;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.LedgerPage;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.StuffPage;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.statics.sound.music.MusicData;
import com.tann.dice.util.Tann;
import com.tann.dice.util.saves.Prefs;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Settings {
   private String lastVersion;
   private String lastMode;
   private String lastDBSV;
   private String lastAlmanacPage;
   private String lastHelpPage;
   private String lastLedgerPage;
   private String lastStuffPage;
   private String lastTextmodPage;
   private String lastSearchIfFailed;
   private boolean tutorialEnabled = true;
   private boolean hasRolled = false;
   private boolean hasEquipped = false;
   private boolean hasSworded = false;
   private boolean purchased = false;
   private boolean hasAttemptedLevel = false;
   private int scaleAdjust = 0;
   private String highscoreName;
   private long highscoreIdentifier = -1L;
   private List<Integer> tutCom = new ArrayList<>();
   private List<String> modeUnlockNotified = new ArrayList<>();
   private List<String> choosePartyHeroes = new ArrayList<>();
   private List<String> customModifiers = new ArrayList<>();
   private List<String> disabledSongPaths = new ArrayList<>();
   private List<String> pins = new ArrayList<>();
   private ObjectMap<String, AnticheeseData> anticheeseMap = new ObjectMap();
   private List<Scenario> savedScenarios = new ArrayList<>();
   private List<CustomPreset> customPresets = new ArrayList<>();
   private List<String> enabledBooleans = new ArrayList<>();
   private ObjectMap<String, Integer> chopValues = new ObjectMap();
   private ObjectMap<String, Float> flopValues = new ObjectMap();
   private Vector2 savedResolution;
   private String controlStore;
   private static int setScaleAdjust;

   public void saveOptions() {
      this.enabledBooleans.clear();
      this.chopValues.clear();
      this.flopValues.clear();

      for (Option bo : OptionUtils.getAll()) {
         if (bo instanceof BOption && ((BOption)bo).c()) {
            this.enabledBooleans.add(bo.getName());
         } else if (bo instanceof ChOption) {
            int val = ((ChOption)bo).c();
            if (val != 0) {
               this.chopValues.put(bo.getName(), val);
            }
         } else if (bo instanceof FlOption) {
            this.flopValues.put(bo.getName(), ((FlOption)bo).getVal());
         }
      }

      this.save();
   }

   public void loadBooleanOptions() {
      OptionUtils.loadValuesFrom(this.enabledBooleans, this.chopValues, this.flopValues);
   }

   public boolean isTutorialEnabled() {
      return this.tutorialEnabled;
   }

   public void setTutorialEnabled(boolean tutorialEnabled) {
      this.tutorialEnabled = tutorialEnabled;
      this.save();
   }

   public float getVolumeSFX() {
      return OptionLib.sfx.getValForUse();
   }

   public float getVolumeMusic() {
      return OptionLib.music.getValForUse();
   }

   public List<Integer> getTutorialCompletion() {
      return this.tutCom;
   }

   public float getTutorialProgress() {
      return (float)this.tutCom.size() / TutorialManager.getNumTutorialElements();
   }

   public void setTutorialCompletion(List<Integer> tutorialCompletion) {
      this.tutCom = tutorialCompletion;
      this.save();
   }

   public boolean isHasRolled() {
      return this.hasRolled || !this.tutorialEnabled;
   }

   public void setHasRolled(boolean hasRolled) {
      this.hasRolled = hasRolled;
      this.save();
   }

   public boolean isHasEquipped() {
      return this.hasEquipped || !this.tutorialEnabled;
   }

   public void setHasEquipped(boolean hasEquipped) {
      this.hasEquipped = hasEquipped;
      this.save();
   }

   public boolean isHasSworded() {
      return this.hasSworded || !this.tutorialEnabled;
   }

   public void setHasSworded(boolean hasSworded) {
      this.hasSworded = hasSworded;
      this.save();
   }

   public void resetTutorial() {
      this.setTutorialEnabled(true);
      this.setHasRolled(false);
      this.setHasEquipped(false);
      this.setHasSworded(false);
      this.setTutorialCompletion(new ArrayList<>());
      this.save();
   }

   public void notifyModeUnlock(Mode mode) {
      this.notifyUnlock(mode.getClass().getSimpleName());
   }

   public void notifyUnlock(String s) {
      this.modeUnlockNotified.add(s);
      this.save();
   }

   public boolean isModeUnlockNotified(Mode mode) {
      return this.isUnlockNotified(mode.getClass().getSimpleName());
   }

   public boolean isUnlockNotified(String s) {
      return this.modeUnlockNotified.contains(s);
   }

   public int getScaleAdjust() {
      return setScaleAdjust;
   }

   public void setScaleAdjust(int scaleAdjust) {
      this.setScaleAdjustFS(scaleAdjust);
   }

   public void saveForHs() {
      this.save();
   }

   public void save() {
      Prefs.setString("settings", com.tann.dice.Main.getJson().toJson(this));
   }

   public String getHighscoreName() {
      return this.highscoreName;
   }

   public void setHighscoreName(String highscoreName) {
      this.highscoreName = highscoreName;
      this.save();
   }

   public long getHighscoreIdentifier() {
      return this.highscoreIdentifier;
   }

   public void setHighscoreIdentifier(long highscoreIdentifier) {
      this.highscoreIdentifier = highscoreIdentifier;
      this.save();
   }

   public void setChoosePartyHeroes(List<String> chooseParty) {
      this.choosePartyHeroes = chooseParty;
   }

   public List<String> getChoosePartyHeroes() {
      return this.choosePartyHeroes;
   }

   public void setCustomModifiers(List<String> customModifiers) {
      this.customModifiers = customModifiers;
   }

   public List<String> getCustomModifiers() {
      return this.customModifiers;
   }

   public void setPurchased(boolean purchased) {
      this.purchased = purchased;
   }

   public boolean isPurchased() {
      return this.purchased;
   }

   public Mode getLastMode() {
      return Mode.getModeFromString(this.lastMode);
   }

   public void setLastMode(Mode mode) {
      if (mode != null) {
         this.lastMode = mode.getName();
      }
   }

   public AnticheeseData getSavedAnticheese(String key) {
      return key == null ? null : (AnticheeseData)this.anticheeseMap.get(key);
   }

   public List<AnticheeseData> getAllSavedAnticheese() {
      List<AnticheeseData> result = new ArrayList<>();
      Values var2 = this.anticheeseMap.values().iterator();

      while (var2.hasNext()) {
         AnticheeseData acd = (AnticheeseData)var2.next();
         result.add(acd);
      }

      return result;
   }

   public void saveAntiCheese(String antiCheeseKey, AnticheeseData data) {
      this.anticheeseMap.put(antiCheeseKey, data);
      this.save();
   }

   public void clearAnticheese(String anticheeseKey) {
      this.anticheeseMap.remove(anticheeseKey);
      this.save();
   }

   public void loadUp() {
      this.loadBooleanOptions();
      this.setScaleAdjustFS(this.scaleAdjust);
      if (this.controlStore != null) {
         com.tann.dice.Main.self().control.setStore(this.controlStore);
      }
   }

   public void setScaleAdjustFS(Integer scaleAdjust) {
      if (scaleAdjust != null) {
         setScaleAdjust = scaleAdjust;
      }
   }

   public boolean displaySettingsSame() {
      return Objects.equals(this.scaleAdjust, setScaleAdjust)
         && (this.controlStore == null || Objects.equals(this.controlStore, com.tann.dice.Main.self().control.getStore()))
         && (resolutionVector().equals(Tann.DG_VECTOR) || Objects.equals(this.savedResolution, resolutionVector()));
   }

   public boolean storeDisplaySettings() {
      int MIN_RES = 100;
      Vector2 tmpRes = resolutionVector();
      if (!(tmpRes.x < 100.0F) && !(tmpRes.y < 100.0F) && com.tann.dice.Main.scale != 0) {
         this.scaleAdjust = setScaleAdjust;
         this.controlStore = com.tann.dice.Main.self().control.getStore();
         this.savedResolution = tmpRes;
         this.save();
         return true;
      } else {
         return false;
      }
   }

   private static Vector2 resolutionVector() {
      return new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
   }

   public void resizeWindow() {
      Vector2 currentResolution = resolutionVector();
      if (this.savedResolution != null && !currentResolution.equals(this.savedResolution)) {
         Gdx.graphics.setWindowedMode((int)this.savedResolution.x, (int)this.savedResolution.y);
      }
   }

   public List<Scenario> getScenarios() {
      return this.savedScenarios;
   }

   public void saveScenario(Scenario scenario) {
      this.savedScenarios.add(scenario);
      this.save();
   }

   public boolean deleteScenario(Scenario sc) {
      boolean removed = this.savedScenarios.remove(sc);
      if (removed) {
         this.save();
      }

      return removed;
   }

   public boolean isBypass() {
      return OptionLib.BYPASS_UNLOCKS.c();
   }

   public void logVersion() {
      if (!"3.2.13".equals(this.lastVersion)) {
         MigrationManager.manageMigration("3.2.13", this.lastVersion, this);
         this.lastVersion = "3.2.13";
         this.save();
      }
   }

   public boolean isFirstLaunch() {
      return this.lastVersion == null;
   }

   public void clearAllAnticheese() {
      this.anticheeseMap.clear();
      this.save();
   }

   public boolean hasAttemptedLevel() {
      return this.hasAttemptedLevel || !this.tutorialEnabled;
   }

   public void setHasAttemptedLevel() {
      if (!this.hasAttemptedLevel) {
         this.hasAttemptedLevel = true;
         this.save();
      }
   }

   private void setupDefaults() {
      this.addScenarios(Scenario.getDefault());

      for (CustomPreset customPreset : CustomPreset.getDefault()) {
         this.addPreset(customPreset);
      }

      for (Option option : OptionUtils.getAll()) {
         if (option instanceof FlOption) {
            FlOption flp = (FlOption)option;
            this.flopValues.put(option.getName(), flp.getDefaultValue());
         }
      }
   }

   public void reset() {
      OptionUtils.init();
      this.enabledBooleans.clear();
      this.chopValues.clear();
      this.flopValues.clear();
      this.setupDefaults();
      this.setHighscoreIdentifier((long)(Math.random() * 5.555555555555556E17));
      this.setHighscoreName("enter name");
      this.loadBooleanOptions();
      this.save();
   }

   public void addScenarios(List<Scenario> scenarios) {
      this.savedScenarios.addAll(scenarios);
   }

   public List<CustomPreset> getCustomPresets() {
      return this.customPresets;
   }

   public void addPreset(CustomPreset preset) {
      this.customPresets.add(preset);
      this.save();
   }

   public void removePreset(CustomPreset preset) {
      this.customPresets.remove(preset);
      this.save();
   }

   public String getLastDBSV() {
      return this.lastDBSV;
   }

   public void saveDbsn(String section) {
      this.lastDBSV = section;
      this.save();
   }

   public String getLastTextmodPage() {
      return this.lastTextmodPage;
   }

   public void setLastTextmodPage(String lastTextmodPage) {
      this.lastTextmodPage = lastTextmodPage;
      this.save();
   }

   public String getLastAlmanacPage() {
      return this.lastAlmanacPage;
   }

   public void setLastAlmanacPage(String lastAlmanacPage) {
      if (lastAlmanacPage != null) {
         String[] split = lastAlmanacPage.split("-");
         if (split.length == 2) {
            String a = split[0];
            String b = split[1];
            if (a.contains("help")) {
               this.lastHelpPage = b;
            } else if (a.contains("ledger")) {
               this.lastLedgerPage = b;
            } else if (a.contains("stuff")) {
               this.lastStuffPage = b;
            }
         }
      }

      this.lastAlmanacPage = lastAlmanacPage;
      this.save();
   }

   public List<String> getPins() {
      return this.pins;
   }

   public void setPins(List<String> pins) {
      this.pins = pins;
      this.save();
   }

   public boolean isVolumeMuted() {
      return this.getVolumeMusic() <= 0.03F;
   }

   public void clearAllOptions() {
      this.enabledBooleans.clear();
      this.chopValues.clear();
      this.loadBooleanOptions();
      this.save();
   }

   public String getLastSearchIfFailed() {
      return this.lastSearchIfFailed;
   }

   public void setLastSearchIfFailed(String lastSearchIfFailed) {
      this.lastSearchIfFailed = lastSearchIfFailed;
      this.save();
   }

   public void skipTutorial() {
      this.setTutorialEnabled(false);
   }

   public boolean isDisabledSong(MusicData md) {
      return this.disabledSongPaths.contains(md.path);
   }

   public void setSongEnabled(MusicData md, boolean on) {
      String songPath = md.path;
      this.disabledSongPaths.remove(songPath);
      if (!on) {
         this.disabledSongPaths.add(songPath);
      }
   }

   public String getLastSpecificPage(BookPage bookPage) {
      if (bookPage instanceof HelpPage) {
         return this.lastHelpPage;
      } else if (bookPage instanceof LedgerPage) {
         return this.lastLedgerPage;
      } else {
         return bookPage instanceof StuffPage ? this.lastStuffPage : null;
      }
   }
}
