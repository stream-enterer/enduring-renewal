package com.tann.dice.gameplay.save;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.ContextConfigUtils;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.VersionUtils;
import com.tann.dice.util.online.BugReport;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.List;

public class SaveState {
   public final DungeonContext dungeonContext;
   public final List<String> commandData;
   public final String sides;
   public final List<String> phases;
   public final String saveVersion;
   public static boolean lastSaveFacade = false;

   public SaveState(DungeonContext dungeonContext, List<String> commandData, String sides, List<String> phases) {
      this(dungeonContext, commandData, sides, phases, null);
   }

   public SaveState(DungeonContext dungeonContext, List<String> commandData, String sides, List<String> phases, String saveVersion) {
      this.dungeonContext = dungeonContext;
      this.commandData = commandData;
      this.sides = sides;
      this.phases = phases;
      this.saveVersion = saveVersion;
   }

   public String getSaveString() {
      return com.tann.dice.Main.getJson().toJson(this.toData());
   }

   public SaveStateData toData() {
      return new SaveStateData(this.dungeonContext.toData(), this.commandData, this.sides, this.phases);
   }

   public void save() {
      if (this.dungeonContext.getContextConfig().getGeneralSaveKey() == null) {
         throw new RuntimeException("trying to save a null save");
      } else {
         String ss = this.getSaveString();
         updateFacadeRenderingStatus(ss);
         Prefs.setString(this.dungeonContext.getContextConfig().getGeneralSaveKey(), ss);
         com.tann.dice.Main.getSettings().saveForHs();
      }
   }

   public static void updateFacadeRenderingStatus(String fullJson) {
      lastSaveFacade = calcFacadeStatus(fullJson);
   }

   private static boolean calcFacadeStatus(String fullJson) {
      if (fullJson != null) {
         return fullJson.contains("facade");
      } else {
         DungeonScreen ds = DungeonScreen.get();
         return ds == null ? false : ds.getDungeonContext().getContextConfig().mode.getFolderType() == FolderType.creative;
      }
   }

   public DungeonScreen makeDungeonScreen() {
      DungeonScreen ds = new DungeonScreen(this.dungeonContext, this.commandData, this.sides, this.phases, this.getSaveString());
      if ("ignore".equalsIgnoreCase(this.saveVersion)) {
         return ds;
      } else {
         Color col = Colours.red;
         String start = "Save created with";
         String end = "";
         String full = null;
         if (this.saveVersion != null && !this.saveVersion.equals(VersionUtils.PASTE_VERSION)) {
            full = "[notranslate]" + com.tann.dice.Main.t(start) + " " + this.saveVersion + end;
         }

         if (full != null) {
            if (this.saveVersion != null) {
               full = full + "[n][grey](your version is " + VersionUtils.PASTE_VERSION + ")";
            }

            full = TextWriter.getTag(col) + full;
            ds.abilityHolder.addWisp(full, 2.3F);
         }

         return ds;
      }
   }

   public void start() {
      try {
         com.tann.dice.Main.self().setScreen(this.makeDungeonScreen());
      } catch (LoadCrashException var3) {
         Group panel = BugReport.makeBugReportPanel(
            "[red]Load error", "[text]Something broke loading your save, sorry... Send a bug report?", "crashed loading savestate-", false
         );
         com.tann.dice.Main.getCurrentScreen().push(panel, 0.7F);
         Tann.center(panel);
      }
   }

   public static SaveState load(String saveFileKey) {
      if (saveFileKey == null) {
         throw new RuntimeException("trying to load a null save");
      } else {
         String save = Prefs.getString(saveFileKey, null);
         return save == null ? null : loadRawString(save);
      }
   }

   public static SaveState loadRawString(String raw) {
      return loadRawString(raw, null);
   }

   public static SaveState loadPasteModeString(String raw, boolean replaceWithPasteConfig) {
      return loadRawString(raw, replaceWithPasteConfig ? "PasteConfig" : null);
   }

   public static SaveState loadRawString(String raw, String replacementCCString) {
      if (raw.contains("{")) {
         raw = raw.substring(raw.indexOf(123));
      }

      if (raw.contains("}")) {
         raw = raw.substring(0, raw.lastIndexOf(125) + 1);
      }

      SaveStateData ssd = (SaveStateData)com.tann.dice.Main.getJson().fromJson(SaveStateData.class, raw);
      if (ssd.d.cc != null
         && replacementCCString != null
         && !ssd.d.cc.equalsIgnoreCase(replacementCCString)
         && !replacementCCString.equalsIgnoreCase("storedconfig")) {
         throw new RuntimeException("Invalid paste: msp");
      } else {
         if (replacementCCString != null || ssd.d.cc == null) {
            ssd.d.cc = replacementCCString;
         }

         return ssd.toState();
      }
   }

   public static boolean hasSave(String saveFileKey) {
      return Prefs.getString(saveFileKey, null) != null;
   }

   public static Group getLoadButton(final String saveFileKey) {
      try {
         String contString = loadSaveButtonText(saveFileKey);
         if (contString == null) {
            return null;
         } else {
            StandardButton loadSave = new StandardButton(contString);
            loadSave.setRunnable(new Runnable() {
               @Override
               public void run() {
                  Sounds.playSound(Sounds.pip);
                  SaveState.load(saveFileKey).start();
               }
            });
            return loadSave;
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         Pixl p = new Pixl(1, 1).fill(Colours.dark);
         p.text("oops, failed to load save").row();
         return p.pix();
      }
   }

   public static DungeonContext loadContext(String saveFileKey) {
      String save = Prefs.getString(saveFileKey, null);
      return save == null ? null : load(saveFileKey).dungeonContext;
   }

   public static String loadSaveButtonText(String saveFileKey) {
      String save = Prefs.getString(saveFileKey, null);
      if (save == null) {
         return null;
      } else {
         SaveStateData ssd = (SaveStateData)com.tann.dice.Main.getJson().fromJson(SaveStateData.class, save);
         int level = ssd.d.n;
         ContextConfig cc = ContextConfigUtils.fromJson(ssd.d.cc, ssd.d.c);
         return cc == null
            ? "?cc"
            : "[notranslate][text]" + com.tann.dice.Main.t("Continue") + " (" + com.tann.dice.Main.t("fight " + level) + cc.getSaveFileButtonName() + ")";
      }
   }

   public FightLog makeFightLog() {
      return new FightLog(
         this.dungeonContext.getParty().getHeroes(),
         MonsterTypeLib.monsterList(this.dungeonContext.getCurrentLevel().getMonsterList()),
         this.commandData,
         this.sides,
         this.dungeonContext
      );
   }

   public boolean validForPaste() {
      Phase p = Phase.deserialise(this.phases.get(0));
      return p.isPastey();
   }
}
