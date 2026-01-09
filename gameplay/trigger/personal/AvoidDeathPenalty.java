package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.trigger.Collision;

public class AvoidDeathPenalty extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "No hp penalty when defeated";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "bone";
   }

   @Override
   public boolean avoidDeathPenalty() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }

   @Override
   public boolean singular() {
      return true;
   }
}
