package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.tann.dice.gameplay.context.DungeonContext;

public class LevelRequirementAND extends LevelRequirement {
   final LevelRequirement a;
   final LevelRequirement b;

   public LevelRequirementAND(LevelRequirement a, LevelRequirement b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return this.a.validFor(dungeonContext) && this.b.validFor(dungeonContext);
   }
}
