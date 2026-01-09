package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.util.Tann;

public class LevelRequirementHash extends LevelRequirement {
   public final int minLevel;
   public final int maxLevel;
   int seedOffset;

   public void setSeedOffset(int off) {
      this.seedOffset = off;
   }

   public LevelRequirementHash(int minLevel, int maxLevel) {
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
   }

   public int getChallengeLevel(DungeonContext dc) {
      int seed = dc.getSeed() + this.seedOffset;
      return this.minLevel + Tann.tettHash(seed, this.maxLevel - this.minLevel);
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return this.getChallengeLevel(dungeonContext) == dungeonContext.getCurrentMod20LevelNumber();
   }

   @Override
   public float getEventStrengthMultiplier(DungeonContext dc) {
      int level = this.getChallengeLevel(dc);
      float lrMult = CurseDistribution.getMultLevelRange(level, 20);
      float naiveMult = 1.0F - level / 20.0F;
      return (lrMult + naiveMult) / 2.0F;
   }
}
