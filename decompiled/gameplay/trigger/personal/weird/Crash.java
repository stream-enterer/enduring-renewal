package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class Crash extends Personal {
   @Override
   public int getBonusMaxHp(int maxHp, EntState state) {
      throw new RuntimeException("TriggerCrash");
   }

   @Override
   public String describeForSelfBuff() {
      return "Crash the game";
   }
}
