package com.tann.dice.gameplay.trigger.global;

import com.tann.dice.gameplay.fightLog.Snapshot;

public class GlobalFleeAvoid extends Global {
   @Override
   public String describeForSelfBuff() {
      return "No Fleeing";
   }

   @Override
   public boolean avoidFlee(Snapshot snapshot) {
      return true;
   }
}
