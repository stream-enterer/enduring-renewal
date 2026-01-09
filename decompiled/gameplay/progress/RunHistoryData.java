package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.save.RunHistory;
import java.util.List;

public class RunHistoryData {
   List<RunHistory> runHistory;

   public RunHistoryData(List<RunHistory> runHistory) {
      this.runHistory = runHistory;
   }

   public RunHistoryData() {
   }
}
