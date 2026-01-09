package com.tann.dice.gameplay.trigger.personal.stats;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;

public class CopyOtherPowerEstimate extends CalcOnly {
   final MonsterType other;
   final float mult;

   public CopyOtherPowerEstimate(MonsterType other) {
      this(other, 1.0F);
   }

   public CopyOtherPowerEstimate(MonsterType other, float mult) {
      this.other = other;
      this.mult = mult;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return this.other.getAvgEffectTier(true) * this.mult;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return this.other.getEffectiveHp() * this.mult;
   }
}
