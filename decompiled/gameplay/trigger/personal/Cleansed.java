package com.tann.dice.gameplay.trigger.personal;

public class Cleansed extends Personal {
   final int amt;

   public Cleansed(int amt) {
      this.amt = amt;
   }

   @Override
   public int getCleanseAmt() {
      return this.amt;
   }

   public static enum CleanseType {
      Poison,
      Petrify,
      Weaken,
      Inflict;
   }
}
