package com.tann.dice.gameplay.progress.stats.stat.pickRate;

public class BitStat {
   public static int val(int val, boolean high) {
      return high ? val >> 16 & 65535 : val & 65535;
   }
}
