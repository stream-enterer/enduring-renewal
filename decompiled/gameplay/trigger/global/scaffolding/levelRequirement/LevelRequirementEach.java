package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

public class LevelRequirementEach extends LevelRequirementRange {
   public LevelRequirementEach() {
      super(1, 20);
   }

   @Override
   public String describe() {
      return "each fight";
   }
}
