package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.saves.Prefs;
import java.util.ArrayList;
import java.util.List;

public class RunHistoryStore {
   private List<RunHistory> runHistoryList;

   public void addRunHistory(RunHistory rh) {
      this.runHistoryList.add(rh);

      while (this.runHistoryList.size() > 5000) {
         this.runHistoryList.remove(0);
      }

      this.saveAll();
   }

   public List<RunHistory> getRunHistoryList() {
      return this.runHistoryList;
   }

   public List<RunHistory> getRuns(Mode mode) {
      List<RunHistory> result = new ArrayList<>();

      for (RunHistory rhd : this.runHistoryList) {
         if (rhd.getModeName().equalsIgnoreCase(mode.getSaveKey())) {
            result.add(rhd);
         }
      }

      return result;
   }

   public List<RunHistory> getRuns(ContextConfig config) {
      List<RunHistory> result = new ArrayList<>();

      for (RunHistory rhd : this.runHistoryList) {
         if (rhd.getModeAndDifficultyDescription().equalsIgnoreCase(config.getUntranslatedEndTitle())) {
            result.add(rhd);
         }
      }

      return result;
   }

   public void loadAll() {
      RunHistoryData rhd = this.loadData();
      this.runHistoryList = new ArrayList<>();
      if (rhd != null) {
         this.runHistoryList = rhd.runHistory;
      }
   }

   public void saveEdit() {
      this.saveAll();
   }

   private void saveAll() {
      RunHistoryData rhd = new RunHistoryData(this.runHistoryList);
      String json = com.tann.dice.Main.getJson(true).toJson(rhd);
      Prefs.setString("run_history", json);
   }

   private RunHistoryData loadData() {
      String save = Prefs.getString("run_history", "");
      if (save.isEmpty()) {
         return null;
      } else {
         try {
            return (RunHistoryData)com.tann.dice.Main.getJson().fromJson(RunHistoryData.class, save);
         } catch (Exception var3) {
            var3.printStackTrace();
            TannLog.log("Failed to load run history  - " + var3.getMessage(), TannLog.Severity.error);
            return null;
         }
      }
   }

   public void reset() {
      this.runHistoryList.clear();
      this.saveAll();
   }
}
