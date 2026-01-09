package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.ChallengeStat;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.StuffPage;
import java.util.Arrays;
import java.util.List;

public class ChallengesAchievement extends StatAchievement {
   public ChallengesAchievement(String name, boolean accept, int target, Unlockable... unlockable) {
      super(name, getDesc(accept, target), ChallengeStat.GET_NAME(accept), target, unlockable);
   }

   private static String getDesc(boolean accept, int amt) {
      return (accept ? "accept" : "reject") + " " + amt + " challenges";
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new ChallengesAchievement("Challenger", true, 3, MonsterTypeLib.byName("chest")),
         new ChallengesAchievement("Challenger+", true, 10, ItemLib.byName("treasure chest")),
         new ChallengesAchievement("Wisdom", false, 20, StuffPage.StuffSection.Graph).diff(18.0F)
      );
   }
}
