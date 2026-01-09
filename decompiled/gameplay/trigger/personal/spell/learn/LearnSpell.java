package com.tann.dice.gameplay.trigger.personal.spell.learn;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.trigger.Collision;

public class LearnSpell extends LearnAbility {
   public LearnSpell(Spell spell) {
      super(spell);
   }

   public LearnSpell(SpellBill sb) {
      this(sb.bSpell());
   }

   @Override
   public String describeForSelfBuff() {
      return "Learn the spell: [blue]" + this.ability.getTitle() + "[cu]";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + TierUtils.totalHeroEffectTierForTactic(((HeroType)type).getTier()) * (this.ability.getPowerMult() - 1.0F);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }
}
