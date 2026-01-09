package com.tann.dice.gameplay.trigger.personal.linked.stateCondition;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;

public enum StateConditionType {
   HalfOrLessHP,
   FullHP,
   MostHP,
   LeastHP,
   Dying,
   Died,
   DiedLasFight,
   Damaged,
   Undamaged,
   HasShields,
   GainedNoShields,
   Used;

   public boolean isValid(EntState es) {
      switch (this) {
         case MostHP:
            int myHp = es.getHp();
            Snapshot s = es.getSnapshot();
            if (s == null) {
               return false;
            }

            int maxHp = 0;

            for (EntState state : s.getStates(null, false)) {
               maxHp = Math.max(maxHp, state.getHp());
            }

            return myHp == maxHp;
         case LeastHP:
            int myHp = es.getHp();
            Snapshot s = es.getSnapshot();
            if (s == null) {
               return false;
            }

            int minHp = 5000;

            for (EntState state : s.getStates(null, false)) {
               minHp = Math.min(minHp, state.getHp());
            }

            return myHp == minHp;
         case HalfOrLessHP:
            return es.getHp() <= es.getMaxHp() / 2;
         case FullHP:
            return es.getHp() == es.getMaxHp();
         case Dying:
            if (es.getSnapshot() == null) {
               return false;
            }

            EntState fut = es.getSnapshot().getFightLog().getState(FightLog.Temporality.Future, es.getEnt());
            return fut != null && fut.isDead();
         case DiedLasFight:
            if (!es.isPlayer()) {
               return false;
            }

            return ((Hero)es.getEnt()).isDiedLastRound();
         case Died:
            return es.getDeathsForStats() > 0;
         case Damaged:
            return es.isDamaged();
         case Undamaged:
            return !Damaged.isValid(es);
         case HasShields:
            return es.getShields() > 0;
         case GainedNoShields:
            return es.getShields() == 0 && es.getDamageBlocked() == 0;
         case Used:
            return es.isUsed();
         default:
            throw new IllegalStateException("Unexpected value: " + this);
      }
   }

   public String getInvalidString(Eff eff) {
      switch (this) {
         case Dying:
            return "Target must be dying";
         case DiedLasFight:
         case Died:
         default:
            return "????";
         case Damaged:
            return " who are damaged";
         case Undamaged:
            return " who are undamaged";
         case HasShields:
            return " target must have shields?";
      }
   }

   public String describeShort() {
      switch (this) {
         case DiedLasFight:
            return "Died last fight";
         case Damaged:
            return "Damaged";
         case GainedNoShields:
            return "Gained no shields";
         default:
            return "" + this;
      }
   }
}
