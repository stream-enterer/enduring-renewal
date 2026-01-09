package com.tann.dice.gameplay.content.ent.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.battleTest.testProvider.MonsterPowerEstimate;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.KillsStat;
import com.tann.dice.util.ChanceHaver;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import java.util.List;
import java.util.Map;

public class MonsterType extends EntType implements ChanceHaver {
   final List<Integer> bannedLevels;
   public final String[] deathSound;
   final int maxInFight;
   final int minInFight;
   final boolean unique;
   float simulatedStrength;
   float btr = -1.0F;

   public MonsterType(
      String name,
      int hp,
      AtlasRegion portrait,
      EntSide[] sides,
      List<Trait> traits,
      EntSize size,
      List<Integer> bannedLevels,
      String[] deathSound,
      int maxInFight,
      int minInFight,
      boolean unique,
      Map<String, Integer> offsets
   ) {
      super(name, hp, portrait, sides, traits, size, offsets);
      this.bannedLevels = bannedLevels;
      this.deathSound = deathSound;
      this.maxInFight = maxInFight;
      this.minInFight = minInFight;
      this.unique = unique;
      if (this.readyForStats()) {
         this.setupStats();
      }
   }

   @Override
   public void setupStats() {
      super.setupStats();
      this.simulatedStrength = MonsterPowerEstimate.getValue(this);
   }

   private boolean readyForStats() {
      return !this.isMissingno() && MonsterTypeLib.isInit();
   }

   @Override
   protected String getColourTag() {
      return "[purple]";
   }

   @Override
   public String toString() {
      return this.getName(false);
   }

   public Monster makeEnt() {
      return new Monster(this);
   }

   public int getMaxInFight() {
      return this.maxInFight;
   }

   public int getMinInFight() {
      return this.minInFight;
   }

   public float getSimulatedStrength() {
      return this.simulatedStrength;
   }

   public float getSummonValue() {
      return this.getSimulatedStrength() * 3.0F;
   }

   public float getOldSummonValue() {
      return Math.min(Math.max(0.0F, this.getEffectiveHp() * 1.6F), this.getEffectiveHp() + this.getAvgEffectTier() * 0.5F);
   }

   public boolean validForLevel(int level) {
      return !this.bannedLevels.contains(level);
   }

   public boolean isUnique() {
      return this.unique;
   }

   public static Color getIdCol() {
      return Colours.orange;
   }

   public boolean encountered(Map<String, Stat> allMergedStats) {
      Stat s = allMergedStats.get(KillsStat.getStatName(this));
      if (s == null) {
         TannLog.log("Unable to find kills stat for " + this);
         return false;
      } else {
         return s.getValue() > 0;
      }
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return "[purple]E";
   }

   public float getBattleTestRatio() {
      if (this.btr != -1.0F) {
         return this.btr;
      } else {
         return this.getEffectiveHp() < 0.0F ? (this.btr = 100.0F - this.getEffectiveHp()) : (this.btr = this.getAvgEffectTier() / this.getEffectiveHp());
      }
   }

   public Float getOverridePowerEstimate() {
      for (Trait t : this.traits) {
         Float result = t.personal.getOverridePowerEstimate(this);
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   public boolean isAllowedInChallenges() {
      return this.getSimulatedStrength() > 0.0F;
   }

   @Override
   public Color getColour() {
      return Colours.purple;
   }

   public boolean validRarity(float rarityRandom) {
      return rarityRandom < this.chance;
   }

   public boolean hideUntilFound() {
      return this.isMissingno() || this.chance < 0.05F;
   }

   @Override
   public boolean isMissingno() {
      return this == PipeMonster.getMissingno();
   }

   public boolean isGenerated() {
      return this.getName().startsWith("rmon-");
   }

   @Override
   public long getCollisionBits() {
      long result = 0L;

      for (int i = 0; i < this.traits.size(); i++) {
         result |= this.traits.get(i).personal.getCollisionBits();
      }

      return result;
   }

   public float getSummonValueForModifier() {
      float sv = this.getSummonValue();
      float midpoint = 7.5F;
      float diff = sv - midpoint;
      float v = Math.min(Math.abs(diff) * 0.15F, 1.0F);
      float multiplier = Interpolation.linear.apply(1.0F, 1.0F + 0.15F * Math.signum(diff), v);
      return sv * multiplier;
   }
}
