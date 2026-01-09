package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

public class TurnRequirementFirstN extends TurnRequirement {
   final int n;

   public TurnRequirementFirstN(int n) {
      this.n = n;
   }

   @Override
   public boolean isValid(int turn) {
      return turn <= this.n;
   }

   @Override
   public String describe() {
      return "First " + this.n + " turns";
   }

   @Override
   public String hyphenTag() {
      return this.n + "";
   }
}
