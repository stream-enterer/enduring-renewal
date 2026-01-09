package com.tann.dice.gameplay.progress.chievo.achievementTypes.weird;

import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.ArrayList;
import java.util.List;

public abstract class MetaAchievement extends Achievement {
   public MetaAchievement(String name, String desc, Unlockable... unlockables) {
      super(name, desc, unlockables);
      this.diff(100.0F);
   }

   public abstract boolean onAchieveOther(List<Achievement> var1);

   public static List<MetaAchievement> make(List<Achievement> tmp) {
      List<MetaAchievement> result = new ArrayList<>();
      result.addAll(ProgressAchievement.makeAll());
      return result;
   }
}
