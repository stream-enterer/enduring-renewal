package com.tann.dice.gameplay.trigger.personal;

public class RenameHero extends Personal {
   final String to;

   public RenameHero(String to) {
      this.to = to;
   }

   @Override
   public String getDisplayName(String name) {
      return this.to;
   }

   @Override
   public boolean skipCalc() {
      return true;
   }

   @Override
   public boolean skipTraitPanel() {
      return true;
   }
}
