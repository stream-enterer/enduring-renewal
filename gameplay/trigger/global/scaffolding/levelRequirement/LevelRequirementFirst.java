package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.tann.dice.gameplay.context.DungeonContext;

public class LevelRequirementFirst extends LevelRequirement {
   @Override
   public String describe() {
      return "the first fight";
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return dungeonContext.isFirstLevel();
   }
}
