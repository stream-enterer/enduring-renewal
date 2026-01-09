package com.tann.dice.gameplay.trigger.global;

public class GlobalCollision extends Global {
   final long bit;

   public GlobalCollision(long bit) {
      this.bit = bit;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.bit;
   }

   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean skipEquipImage() {
      return true;
   }

   @Override
   public boolean metaOnly() {
      return true;
   }
}
