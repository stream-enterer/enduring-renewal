package com.tann.dice.gameplay.progress;

import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import java.util.List;

public class PickRateData {
   public List<PickStat> pickRates;

   public PickRateData(List<PickStat> pickRates) {
      this.pickRates = pickRates;
   }

   public PickRateData() {
   }
}
