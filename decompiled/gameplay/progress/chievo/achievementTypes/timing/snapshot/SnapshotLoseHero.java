package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot;

import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class SnapshotLoseHero extends SnapshotAchievement {
   final int amt;

   public SnapshotLoseHero(int amt, Unlockable... unlockables) {
      super(GET_NAME(amt), GET_DESC(amt), unlockables);
      this.amt = amt;
      this.diff(7 * amt);
   }

   private static String GET_NAME(int amt) {
      return "Oops" + Tann.repeat("+", amt - 1);
   }

   private static String GET_DESC(int amt) {
      return amt == 1 ? "Lose a hero during your turn" : "Lose " + amt + " heroes during your turn";
   }

   @Override
   public boolean snapshotCheck(StatSnapshot ss) {
      if (ss.origin instanceof DieCommand) {
         if (((DieCommand)ss.origin).isEnemy()) {
            return false;
         }
      } else if (!(ss.origin instanceof AbilityCommand)) {
         return false;
      }

      return ss.beforeCommand.getAliveHeroStates().size() == ss.afterCommand.getAliveHeroStates().size() + this.amt;
   }

   public static List<SnapshotLoseHero> makeAll() {
      return Arrays.asList(
         new SnapshotLoseHero(1, ItemLib.byName("Thimble")),
         new SnapshotLoseHero(2, ItemLib.byName("Glass Helm")),
         new SnapshotLoseHero(3),
         new SnapshotLoseHero(4),
         new SnapshotLoseHero(5),
         new SnapshotLoseHero(6)
      );
   }
}
