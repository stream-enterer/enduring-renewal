package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd.fleeAchievement.FleeAchievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FightEndAchievement extends Achievement {
   public FightEndAchievement(String name, String description, Unlockable... unlockables) {
      super(name, description, unlockables);
   }

   public abstract boolean endOfFightCheck(StatSnapshot var1, boolean var2);

   public static List<Achievement> make() {
      List<Achievement> all = new ArrayList<>();
      all.addAll(FightEndCurseAchievement.makeAll());
      all.addAll(FleeAchievement.makeAll());
      all.addAll(Arrays.asList((new FightEndAchievement("First Boss", "Beat level 4", HeroCol.red, HeroCol.blue) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.context.getCurrentLevelNumber() >= 4;
         }
      }).diff(0.0F), (new FightEndAchievement("Unlock a door", "Beat level 8", Mode.SHORTCUT) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.context.getCurrentLevelNumber() >= 8;
         }
      }).diff(0.0F), (new FightEndAchievement("Cheat Death", "Win the Tarantus fight with no dead heroes", ItemLib.byName("Determination")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            if (ss.afterCommand.getEntities(true, true).size() > 0) {
               return false;
            } else {
               for (Ent de : ss.afterCommand.getEntities(false, true)) {
                  if (de.name.equalsIgnoreCase(MonsterTypeLib.byName("tarantus").getName(false))) {
                     return true;
                  }
               }

               return false;
            }
         }
      }).diff(12.0F), (new FightEndAchievement("Draw", "Win and lose a fight at the same time", ItemLib.byName("knife bag")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return ss.afterCommand.isVictory() && ss.afterCommand.getEntities(true, false).size() == 0;
         }
      }).diff(10.0F), (new FightEndAchievement("Survivor", "Win a fight with only one surviving hero", ItemLib.byName("Candle")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.afterCommand.getEntities(true, false).size() == 1;
         }
      }).diff(7.0F), (new FightEndAchievement("Alpha Strike", "Win a fight on the first turn", ItemLib.byName("Hourglass")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.afterCommand.getTurn() == 1;
         }
      }).diff(7.0F), (new FightEndAchievement("Alpha Strike+", "Win a fight on the first turn whilst rolling") {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.afterCommand.getTurn() == 1 && PhaseManager.get().getPhase() instanceof PlayerRollingPhase;
         }
      }).diff(7.0F), (new FightEndAchievement("Crushed", "Beat a boss fight on the first turn", ItemLib.byName("Learn Infinity")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.afterCommand.getTurn() == 1 && ss.context.isBossFight();
         }
      }).diff(12.0F), (new FightEndAchievement("Crushed+", "Beat the final boss fight on the first turn", OptionLib.ROMAN_MODE) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            return victory && ss.afterCommand.getTurn() == 1 && ss.context.isAtLastLevel();
         }
      }).diff(12.0F), (new FightEndAchievement("Last legs", "Win a fight with 4+ heroes on 1hp", ItemLib.byName("Relic")) {
         @Override
         public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
            if (!victory) {
               return false;
            } else {
               int atOne = 0;

               for (EntState m : ss.afterCommand.getAliveHeroStates()) {
                  if (m.getHp() == 1) {
                     atOne++;
                  }
               }

               return atOne >= 4;
            }
         }
      }).diff(7.0F)));
      return all;
   }
}
