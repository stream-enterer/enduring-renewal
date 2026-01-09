package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd;

import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class FightEndCurseAchievement extends FightEndAchievement {
   final int num;

   public FightEndCurseAchievement(String name, int num, Unlockable... unlockables) {
      super(name, "win a fight with " + num + " " + Words.plural("curse", num), unlockables);
      this.num = num;
      this.diff(6 + num);
   }

   @Override
   public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
      return victory && ss.context.getNumberOfCurses() >= this.num;
   }

   public static List<FightEndAchievement> makeAll() {
      return Arrays.asList(
         new FightEndCurseAchievement("Curse Mastery", 1, Mode.CURSE, Mode.BLURSED),
         new FightEndCurseAchievement("Curse Mastery+", 7, Mode.CURSE_HYPER, Mode.CURSED_ULTRA, Mode.BLURTRA),
         new FightEndCurseAchievement("Curse Mastery++", 13, Mode.BLYPTRA)
      );
   }

   @Override
   public boolean isCompletable() {
      return !UnUtil.isLocked(Mode.CURSE);
   }
}
