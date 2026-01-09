package com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PickAchievement extends StatAchievement {
   transient Choosable o;
   transient int target;

   public PickAchievement(Choosable o, int target, Unlockable... unlockable) {
      super(o + "-pick", describe(o, target), PickStat.nameFor(o), target, unlockable);
      this.target = target;
      this.o = o;
      this.diff(7 + target);
   }

   public PickAchievement(HeroType o, int target, Unlockable... unlockable) {
      this((Choosable)o, target, unlockable);
   }

   private static String describe(Choosable o, int target) {
      return "Choose " + o + " " + Words.nTimes(target);
   }

   @Override
   public boolean statCheck(Map<String, Stat> mergedStats) {
      Stat s = mergedStats.get(PickStat.nameFor(this.o));
      return s == null ? false : PickStat.val(s, false) >= this.target;
   }

   public static List<Achievement> make() {
      return Arrays.asList(
         new PickAchievement(HeroTypeUtils.byName("gambler"), 2, ItemLib.byName("Ace of Spades")),
         new PickAchievement(HeroTypeUtils.byName("ludus"), 2, MonsterTypeLib.byName("sudul")),
         new PickAchievement(HeroTypeUtils.byName("dancer"), 2, ItemLib.byName("Ballet Shoes")),
         new PickAchievement(HeroTypeUtils.byName("bard"), 2, ItemLib.byName("Enchanted Harp")),
         new PickAchievement(HeroTypeUtils.byName("Dabbler"), 2, HeroTypeUtils.byName("Dabblest")),
         new PickAchievement(HeroTypeUtils.byName("Statue"), 2, ItemLib.byName("statuette")),
         new PickAchievement(HeroTypeUtils.byName("Jester"), 2, ItemLib.byName("Jester Cap")),
         new PickAchievement(HeroTypeUtils.byName("Ace"), 2, ItemLib.byName("Cheating Sleeves")),
         new PickAchievement(HeroTypeUtils.byName("Poet"), 2, ItemLib.byName("Poem")),
         new PickAchievement(ItemLib.byName("Pocket Mirror"), 2, ItemLib.byName("rorrim tekcop")),
         new PickAchievement(ItemLib.byName("Pocket Phylactery"), 2, ItemLib.byName("cracked phylactery")),
         new PickAchievement(ItemLib.byName("emerald mirror"), 1, ItemLib.byName("cracked emerald")),
         new PickAchievement(ItemLib.byName("sling"), 1, ItemLib.byName("updog")),
         new PickAchievement(HeroTypeUtils.byName("Herbalist"), 2, ItemLib.getAllPotions()),
         new PickAchievement(ItemLib.byName("updog"), 1, ItemLib.byName("poodle"))
      );
   }

   @Override
   public boolean isCompletable() {
      return this.o instanceof Unlockable ? !UnUtil.isLocked((Unlockable)this.o) : true;
   }
}
