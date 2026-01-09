package com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition;

import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.lang.Words;

public enum SnapshotConditionType {
   OrFewerHeroes,
   OrMoreMonsters;

   public boolean holdsFor(Snapshot s, int val) {
      switch (this) {
         case OrFewerHeroes:
            return s.getAliveHeroEntities().size() <= val;
         case OrMoreMonsters:
            return s.getAliveMonsterEntities().size() >= val;
         default:
            return false;
      }
   }

   public String getBasicString(int val) {
      switch (this) {
         case OrFewerHeroes:
            if (val == 1) {
               return "there is exactly one living hero";
            }

            return "there are " + val + " or fewer living heroes";
         case OrMoreMonsters:
            String monsterPl = Words.plural("monster", val);
            return "there " + Words.plural("is", val) + " " + val + " or more " + monsterPl;
         default:
            return "unknown: " + this;
      }
   }

   public String getShortString(int val) {
      switch (this) {
         case OrFewerHeroes:
            return "[yellow]<=" + val + " " + Words.plural("hero", val);
         case OrMoreMonsters:
            return "[purple]>=" + val + " " + Words.plural("monster", val);
         default:
            return "errr" + this.name();
      }
   }
}
