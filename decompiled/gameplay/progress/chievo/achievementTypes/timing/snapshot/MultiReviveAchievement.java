package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot;

import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.List;

public class MultiReviveAchievement extends SnapshotAchievement {
   final int amt;

   public MultiReviveAchievement(int amt) {
      super(NAME(amt), DESC(amt));
      this.amt = amt;
   }

   private static String DESC(int amt) {
      return "Revive " + amt + " heroes with a single action";
   }

   private static String NAME(int amt) {
      return Words.capitaliseFirst(Words.multiple(amt)) + "-revive";
   }

   public static List<MultiReviveAchievement> makeAll() {
      List<MultiReviveAchievement> result = new ArrayList<>();

      for (int i = 2; i <= 6; i++) {
         result.add(new MultiReviveAchievement(i));
      }

      return result;
   }

   @Override
   public boolean snapshotCheck(StatSnapshot ss) {
      return ss != null && ss.afterCommand != null && ss.beforeCommand != null
         ? ss.afterCommand.getStates(true, false).size() >= ss.beforeCommand.getStates(true, false).size() + this.amt
         : false;
   }
}
