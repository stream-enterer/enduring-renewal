package com.tann.dice.gameplay.progress.chievo.achievementTypes.debug;

import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot.SnapshotAchievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class PlaceholderAchievement extends SnapshotAchievement {
   static int id;

   public PlaceholderAchievement(Unlockable... unlockables) {
      super("placeholder" + id++, "placeholder", unlockables);
      this.diff(500.0F);
   }

   public static List<Achievement> make() {
      return Arrays.asList();
   }

   @Override
   public boolean snapshotCheck(StatSnapshot ss) {
      return false;
   }
}
