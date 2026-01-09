package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.tann.dice.gameplay.context.DungeonContext;

public class LevelRequirementAllButFirst extends LevelRequirement {
   final LevelRequirementFirst lrf = new LevelRequirementFirst();

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return !this.lrf.validFor(dungeonContext);
   }

   @Override
   public String describe() {
      return "each fight";
   }
}
