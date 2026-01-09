package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.SurrenderChoiceStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StatAchievement extends Achievement {
   transient Stat stat;
   transient int target;

   public StatAchievement(String name, String description, String statName, int target, Unlockable... unlockable) {
      super(name, description, unlockable);
      this.stat = com.tann.dice.Main.self().masterStats.getStat(statName);
      if (this.stat instanceof PickStat && !(this instanceof PickAchievement)) {
         throw new RuntimeException("should be a pickachievement");
      } else {
         this.target = target;
         if (this.stat == null) {
            TannLog.log("Can't find stat for achievement: " + name, TannLog.Severity.error);
         }

         this.diff(9.0F);
      }
   }

   private boolean check(Stat stat) {
      return stat.getValue() >= this.target;
   }

   public boolean statCheck(Map<String, Stat> mergedStats) {
      Stat s = mergedStats.get(this.stat.getName());
      return s == null ? false : this.check(s);
   }

   public static List<Achievement> make() {
      List<Achievement> all = new ArrayList<>();
      all.addAll(PickAchievement.make());
      all.addAll(KillsAchievement.make());
      all.addAll(BattleWinsAchievement.make());
      all.addAll(StreakAchievement.make());
      all.addAll(DeathAchievement.make());
      all.addAll(ChallengesAchievement.make());
      all.addAll(UndoAchievement.make());
      all.addAll(
         Arrays.asList(
            new StatAchievement("Caster", "Cast 100 spells", "spells-cast", 100, HeroTypeLib.byName("spellblade")).diff(8.0F),
            new StatAchievement("Caster+", "Cast 200 spells", "spells-cast", 200, MonsterTypeLib.byName("banshee")).diff(8.0F),
            new StatAchievement("Caster++", "Cast 300 spells", "spells-cast", 300, MonsterTypeLib.byName("wisp")).diff(12.0F),
            new StatAchievement("Heal", "Heal for 300 total", "total-healing", 300, ItemLib.byName("Scar")).diff(8.0F),
            new StatAchievement("Heal+", "Heal for 2000 total", "total-healing", 2000, ItemLib.byName("Ichor Chalice")).diff(12.0F),
            new StatAchievement("Change of Heart", "Heal for 1000 total", "total-healing", 1000, ItemLib.byName("Change of Heart")),
            new StatAchievement("Blessed Ring", "Shield for 300 total", "total-blocked", 300, ItemLib.byName("Blessed Ring")),
            new StatAchievement("Cursed Bolt", "Cast 1000 spells", "spells-cast", 1000, ItemLib.byName("Cursed Bolt")),
            new StatAchievement("Expert", "Defeat 1000 monsters", "total-kills", 1000, Feature.EVENTS_WEIRD),
            new StatAchievement("Benevolence", "Allow enemies to flee 20 times", SurrenderChoiceStat.NAME(true), 20, ItemLib.byName("Friendship Bracelet"))
         )
      );
      return all;
   }
}
