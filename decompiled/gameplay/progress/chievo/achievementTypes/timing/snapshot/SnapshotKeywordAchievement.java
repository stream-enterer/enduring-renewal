package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot;

import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class SnapshotKeywordAchievement extends SnapshotAchievement {
   final int amt;

   public SnapshotKeywordAchievement(String name, int amt, Unlockable... unlockables) {
      super(name, "Use a side with " + amt + "+ keywords", unlockables);
      this.amt = amt;
      this.diff(amt * 3 + 6);
   }

   @Override
   public boolean snapshotCheck(StatSnapshot ss) {
      return !(ss.origin instanceof DieCommand)
         ? false
         : ((DieCommand)ss.origin).targetable.getDerivedEffects(ss.beforeCommand).getKeywords().size() >= this.amt;
   }

   public static List<SnapshotKeywordAchievement> makeAll() {
      return Arrays.asList(
         new SnapshotKeywordAchievement("Chaos", 3, HeroTypeLib.byName("Collector")),
         new SnapshotKeywordAchievement("Chaos+", 4, HeroTypeLib.byName("Ninja")),
         new SnapshotKeywordAchievement("Chaos++", 5, HeroTypeLib.byName("Enchanter")),
         new SnapshotKeywordAchievement("Chaos+++", 6, ItemLib.byName("Chaos Wand")),
         new SnapshotKeywordAchievement("Chaos++++", 8),
         new SnapshotKeywordAchievement("Chaos+++++", 10),
         new SnapshotKeywordAchievement("Chaos++++++", 15),
         new SnapshotKeywordAchievement("Chaos+++++++", 20),
         new SnapshotKeywordAchievement("Chaos++++++++", 100)
      );
   }
}
