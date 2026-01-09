package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.UndoCountStat;
import java.util.Arrays;
import java.util.List;

public class UndoAchievement extends StatAchievement {
   public UndoAchievement(String name, int target, Unlockable... unlockable) {
      super(name, DESCRIBE(target), UndoCountStat.NAME, target, unlockable);
   }

   private static String DESCRIBE(int target) {
      return "undo " + target + " times";
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new UndoAchievement("Indecisive", 20),
         new UndoAchievement("Indecisive+", 200),
         new UndoAchievement(
            "I wish I could undo this achievement",
            500,
            ItemLib.byeNames(
               "snake oil", "bond certificate", "toy sword", "fidget spinner", "hidden strength", "trick deck", "flea", "grass", "bent fork", "spanner"
            )
         ),
         new UndoAchievement("Indecisive++", 20000)
      );
   }
}
