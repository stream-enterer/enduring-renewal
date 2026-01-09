package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.sound.Sounds;

public class Undying extends Personal {
   @Override
   public String getImageName() {
      return "undying";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String[] getSound() {
      return Sounds.undying;
   }

   @Override
   public boolean allowDeath(EntState state) {
      return false;
   }

   @Override
   protected boolean removeGainsFromGiveText() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "Cannot die";
   }

   @Override
   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return targetFuture.isDead();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.UNDYING;
   }

   @Override
   public boolean singular() {
      return true;
   }
}
