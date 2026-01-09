package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.TotalBattleWinsStat;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class BattleWinsAchievement extends StatAchievement {
   public BattleWinsAchievement(int index, Unlockable... unlockable) {
      super("Victory" + Tann.repeat("+", index), "Win " + getAmt(index) + " fights", TotalBattleWinsStat.NAME, getAmt(index), unlockable);
      this.diff(this.target / 10);
   }

   private static int getAmt(int index) {
      return (index + 1) * 15;
   }

   public static List<Achievement> make() {
      Unlockable[] rewards = new Unlockable[]{
         Feature.ALTERNATE_RANDOM_ITEMS,
         Mode.CHOOSE_PARTY,
         Mode.LOOT,
         Mode.RAID,
         Mode.GENERATE_HEROES,
         Mode.ALTERNATE_HEROES,
         Mode.NIGHTMARE,
         Mode.DREAM,
         Mode.BALANCE,
         Mode.CUSTOM_FIGHT,
         Feature.WEIRD_RANDOM_ITEMS,
         OptionLib.SHOW_RARITY,
         Difficulty.Heaven
      };
      List<Achievement> result = new ArrayList<>();
      int amt = rewards.length;

      for (int i = 0; i < amt; i++) {
         result.add(new BattleWinsAchievement(i, rewards[i]));
      }

      return result;
   }
}
