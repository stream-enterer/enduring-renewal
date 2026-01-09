package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll;

import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.ArrayList;
import java.util.List;

public class StraightAchievement extends AfterRollAchievement {
   final int start;
   static final int LENGTH = 5;

   public StraightAchievement(int start, Unlockable... unlockables) {
      super(NAME_FOR(start), DESC_FOR(start), unlockables);
      this.start = start;
   }

   private static String DESC_FOR(int num) {
      String p = "Roll sides with pips";

      for (int i = num; i < num + 5; i++) {
         p = p + " " + i;
      }

      return p;
   }

   private static String NAME_FOR(int num) {
      return num + "-" + (num + 4) + " Straight";
   }

   @Override
   public boolean allDiceLandedCheck(List<EntSideState> dice) {
      if (dice.size() != 5) {
         return false;
      } else {
         int bits = 0;

         for (EntSideState ess : dice) {
            int val = ess.getCalculatedEffect().getValue();
            int checkIndex = val - this.start;
            if (checkIndex >= 0 && checkIndex < 5) {
               bits |= 1 << checkIndex;
            }
         }

         return bits == 31;
      }
   }

   public static List<StraightAchievement> makeAll() {
      List<StraightAchievement> rslt = new ArrayList<>();

      for (int i = 1; i < 4; i++) {
         if (i != 0) {
            if (i == 1) {
               rslt.add(new StraightAchievement(1, ItemLib.byName("Golden D6")));
            } else {
               rslt.add(new StraightAchievement(i));
            }
         }
      }

      return rslt;
   }
}
