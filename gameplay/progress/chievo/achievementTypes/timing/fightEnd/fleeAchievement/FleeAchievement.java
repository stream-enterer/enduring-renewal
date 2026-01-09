package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd.fleeAchievement;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd.FightEndAchievement;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FleeAchievement extends FightEndAchievement {
   MonsterType mt;

   public FleeAchievement(String name, MonsterType type) {
      super(name, MAKE_DESCRIPTION(type));
      this.mt = type;
   }

   private static String MAKE_DESCRIPTION(MonsterType type) {
      String result = "Allow ";
      if (!type.isUnique()) {
         result = result + Words.fullPlural(type.getName(true), 1);
      } else {
         result = result + type.getName(false);
      }

      return result + " to flee";
   }

   @Override
   public boolean endOfFightCheck(StatSnapshot ss, boolean victory) {
      List<EntState> states = ss.afterCommand.getStates(false, true);

      for (int i = 0; i < states.size(); i++) {
         EntState es = states.get(i);
         if (es.isFled() && es.getEnt().getEntType() == this.mt) {
            return true;
         }
      }

      return false;
   }

   public static Collection<FleeAchievement> makeAll() {
      return Arrays.asList(
         new FleeAchievement("Barrel Roll", MonsterTypeLib.byName("barrel")), new FleeAchievement("Dragon Flight", MonsterTypeLib.byName("dragon"))
      );
   }
}
