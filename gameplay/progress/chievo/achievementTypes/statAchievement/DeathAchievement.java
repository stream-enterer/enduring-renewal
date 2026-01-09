package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class DeathAchievement extends StatAchievement {
   public DeathAchievement(int target, Unlockable... unlockable) {
      super(target + " deaths", target + " hero deaths", "total-deaths", target, unlockable);
      this.diff(2 + target / 10);
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new DeathAchievement(25, ItemLib.byeNames("Early Grave")),
         new DeathAchievement(50, HeroTypeUtils.byName("wraith")),
         new DeathAchievement(75, ItemLib.byName("ritual dagger")),
         new DeathAchievement(100, HeroTypeUtils.byName("roulette")),
         new DeathAchievement(200),
         new DeathAchievement(500),
         new DeathAchievement(1000),
         new DeathAchievement(10000),
         new DeathAchievement(100000),
         new DeathAchievement(1000000)
      );
   }
}
