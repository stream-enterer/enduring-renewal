package com.tann.dice.util.saves;

import com.badlogic.gdx.Preferences;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.util.TannLog;
import java.util.Arrays;
import java.util.Map;

public class Prefs {
   private static final String MAIN_FILE = com.tann.dice.Main.self().control.getMainFileString();
   private static final String BACKUP_FILE = makeBackupString(MAIN_FILE);
   private static final String V2_FILE = "slice-and-dice-2";
   public static final String STATS = "stats";
   public static final String RUN_HISTORY = "run_history";
   public static final String SETTINGS = "settings";
   private static Preferences prefs;

   private static String makeBackupString(String main) {
      return main.contains(".") ? main.replace(".", "x.") : main + "x";
   }

   private static Preferences get() {
      if (prefs == null) {
         TannLog.log("init prefs1");
         prefs = com.tann.dice.Main.self().control.makePrefs(MAIN_FILE);
         TannLog.log("init prefs2");
         initPrefs();
         TannLog.log("init prefs3");
      }

      return prefs;
   }

   private static void initPrefs() {
      if (isNew(prefs)) {
         boolean backupd = false;
         if (com.tann.dice.Main.self().control.useBackups()) {
            TannLog.log("new save file, checking for files to restore from");
            backupd = attemptToRestoreFrom(BACKUP_FILE, prefs, false);
         }

         if (!backupd) {
            boolean v2r = attemptToRestoreFrom("slice-and-dice-2", prefs, true);
            if (v2r) {
               ContextConfig.CLEAR_ALL_SAVES();
            }
         }
      }
   }

   private static boolean attemptToRestoreFrom(String fileName, Preferences tmp, boolean migration) {
      Preferences otherFile = com.tann.dice.Main.self().control.makePrefs(fileName);
      if (isNew(otherFile)) {
         TannLog.log("No file detected with name " + fileName);
         return false;
      } else {
         TannLog.log("restoring from " + fileName);
         restore(tmp, otherFile, migration);
         return true;
      }
   }

   private static void restore(Preferences tmp, Preferences otherFile, boolean migration) {
      boolean plainText = otherFile.getString("settings").contains("lastVersion");

      for (String tag : migration ? Arrays.asList("stats") : Arrays.asList("stats", "run_history", "settings")) {
         String ofs = otherFile.getString(tag);
         String toPut = plainText && com.tann.dice.Main.self().control.saveContentEncryption() ? EncUtils.encrypt(ofs) : ofs;
         tmp.putString(tag, toPut);
      }

      tmp.flush();
   }

   private static boolean isNew(Preferences prefs) {
      return prefs.getString("settings", null) == null;
   }

   public static void setString(String key, String value) {
      if (com.tann.dice.Main.self().control.saveContentEncryption()) {
         value = EncUtils.encrypt(value);
      }

      get().putString(key, value);
      get().flush();
   }

   public static String getString(String key, String def) {
      String result = get().getString(key, def);
      if (com.tann.dice.Main.self().control.saveContentEncryption()) {
         result = EncUtils.decrypt(result);
      }

      return result;
   }

   public static void clearPref(String key) {
      if (get().contains(key)) {
         get().remove(key);
         get().flush();
      }
   }

   public static void RESETSAVEDATA() {
      for (String s : new String[]{MAIN_FILE, BACKUP_FILE}) {
         Preferences p = com.tann.dice.Main.self().control.makePrefs(s);
         p.clear();
         p.flush();
      }

      prefs = null;
   }

   public static void backupSave() {
      Preferences clone = com.tann.dice.Main.self().control.makePrefs(BACKUP_FILE);
      clone.clear();
      clone.put(getAllSaveData());
      clone.flush();
   }

   private static Map<String, ?> getAllSaveData() {
      return get().get();
   }
}
