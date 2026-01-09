package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll;

import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.ArrayList;
import java.util.List;

public class YahtzeeAchievement extends AfterRollAchievement {
   final int num;

   public YahtzeeAchievement(int num, Unlockable... unlockables) {
      super(NAME_FOR(num), DESC_FOR(num), unlockables);
      this.num = num;
   }

   private static String DESC_FOR(int num) {
      return "Roll " + num + " on all sides";
   }

   private static String NAME_FOR(int num) {
      return "All " + num;
   }

   @Override
   public boolean allDiceLandedCheck(List<EntSideState> dice) {
      if (dice.size() < 5) {
         return false;
      } else {
         for (EntSideState ess : dice) {
            int val = ess.getCalculatedEffect().getValue();
            if (this.num != val) {
               return false;
            }
         }

         return true;
      }
   }

   public static List<YahtzeeAchievement> makeAll() {
      List<YahtzeeAchievement> rslt = new ArrayList<>();
      rslt.add(new YahtzeeAchievement(3, ItemLib.byName("Ordinary Triangle")));
      rslt.add(new YahtzeeAchievement(2, ItemLib.byName("Twisted Bar")));
      rslt.add(new YahtzeeAchievement(1, ItemLib.byName("Jewel Loupe")));
      return rslt;
   }
}
