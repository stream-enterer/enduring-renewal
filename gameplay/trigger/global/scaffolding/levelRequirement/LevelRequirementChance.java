package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.tann.dice.gameplay.context.DungeonContext;

public class LevelRequirementChance extends LevelRequirement {
   final float chance;

   public LevelRequirementChance(float chance) {
      this.chance = chance;
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return Math.random() < this.chance;
   }
}
