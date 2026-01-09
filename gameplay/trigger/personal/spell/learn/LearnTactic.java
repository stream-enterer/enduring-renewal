package com.tann.dice.gameplay.trigger.personal.spell.learn;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Colours;

public class LearnTactic extends LearnAbility {
   public LearnTactic(Tactic tactic) {
      super(tactic);
      tactic.setCol(Colours.grey);
   }

   @Override
   public String describeForSelfBuff() {
      return "Learn the tactic: [orange]" + this.ability.getTitle() + "[cu]";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + TierUtils.totalHeroEffectTierForTactic(((HeroType)type).getTier()) * this.ability.getPowerMult();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.TACTIC;
   }
}
