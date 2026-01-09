package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.KillsStat;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class KillsAchievement extends StatAchievement {
   public KillsAchievement(MonsterType mt, int target, Unlockable... unlockables) {
      this(mt.getName(false) + "-kill", mt, target, unlockables);
   }

   public KillsAchievement(String name, MonsterType mt, int target, Unlockable... unlockables) {
      super(name, describe(mt, target), KillsStat.getStatName(mt), target, unlockables);
      this.target = target;
   }

   private static String describe(MonsterType mt, int target) {
      return "Kill " + target + " " + Words.plural(mt.getName(false), target).toLowerCase();
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new KillsAchievement("Pile of Bones", MonsterTypeLib.byName("bones"), 100, MonsterTypeLib.byName("grave")),
         new KillsAchievement("Spines", MonsterTypeLib.byName("imp"), 20, MonsterTypeLib.byName("thorn")),
         new KillsAchievement("Spines+", MonsterTypeLib.byName("imp"), 100, ItemLib.byName("Silver Imp")),
         new KillsAchievement("Brambles", MonsterTypeLib.byName("thorn"), 10, MonsterTypeLib.byName("bramble")),
         new KillsAchievement("Brambles+", MonsterTypeLib.byName("thorn"), 30, MonsterTypeLib.byName("seed")),
         new KillsAchievement("GONG", MonsterTypeLib.byName("fanatic"), 10, MonsterTypeLib.byName("bell")),
         new KillsAchievement("Troll Slayer", MonsterTypeLib.byName("troll"), 2, ItemLib.byName("Troll Nose")),
         new KillsAchievement("Troll Nemesis", MonsterTypeLib.byName("troll"), 10, ItemLib.byName("Troll Blood")),
         new KillsAchievement("Anger the Ghosts", MonsterTypeLib.byName("ghost"), 5, MonsterTypeLib.byName("baron")),
         new KillsAchievement("Stone Revenge", MonsterTypeLib.byName("slate"), 5, MonsterTypeLib.byName("quartz")),
         new KillsAchievement("Stone Revenge+", MonsterTypeLib.byName("slate"), 15, MonsterTypeLib.byName("basalt")),
         new KillsAchievement("Hidden Emerald", MonsterTypeLib.byName("slimer"), 10, ItemLib.byName("emerald shard"), ItemLib.byName("polished emerald")),
         new KillsAchievement("Caw Eggs", MonsterTypeLib.byName("caw"), 10, MonsterTypeLib.byName("caw egg")),
         new KillsAchievement("Dragon Kill", MonsterTypeLib.byName("dragon"), 1, MonsterTypeLib.byName("hexia")),
         new KillsAchievement("Hexia Kill", MonsterTypeLib.byName("hexia"), 1, MonsterTypeLib.byName("the hand")),
         new KillsAchievement("Hand Kill", MonsterTypeLib.byName("the hand"), 1, MonsterTypeLib.byName("Inevitable")),
         new KillsAchievement("Dragon Eggs", MonsterTypeLib.byName("dragon"), 3, MonsterTypeLib.byName("dragon egg")),
         new KillsAchievement("Fading", MonsterTypeLib.byName("wisp"), 15, MonsterTypeLib.byName("Illusion"))
      );
   }
}
