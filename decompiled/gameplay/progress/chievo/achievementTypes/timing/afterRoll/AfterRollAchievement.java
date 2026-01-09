package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.ArrayList;
import java.util.List;

public abstract class AfterRollAchievement extends Achievement {
   public AfterRollAchievement(String name, String description, Unlockable... unlockables) {
      super(name, description, unlockables);
      this.diff(15.0F);
   }

   public abstract boolean allDiceLandedCheck(List<EntSideState> var1);

   public static List<Achievement> make() {
      List<Achievement> all = new ArrayList<>();
      all.addAll(NumSidesRolledAchievement.makeAll());
      all.addAll(YahtzeeAchievement.makeAll());
      all.addAll(StraightAchievement.makeAll());
      return all;
   }
}
