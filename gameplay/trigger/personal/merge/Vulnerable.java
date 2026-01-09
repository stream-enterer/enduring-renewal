package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class Vulnerable extends Merge {
   int bonus;

   public Vulnerable(int bonus) {
      this.bonus = bonus;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "vulnerable";
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonus) + " damage taken from " + Words.spab(true) + " and dice";
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (targetable == null) {
         return damage;
      } else if (!(targetable instanceof Ability) && !(targetable instanceof DieTargetable)) {
         return damage;
      } else {
         return eff.getType() != EffType.Damage && !eff.hasKeyword(Keyword.damage)
            ? super.alterTakenDamage(damage, eff, snapshot, self, targetable)
            : damage + this.bonus;
      }
   }

   @Override
   protected boolean canMergeInternal(Personal personal) {
      return personal instanceof Vulnerable;
   }

   @Override
   public void merge(Personal personal) {
      Vulnerable other = (Vulnerable)personal;
      this.bonus = this.bonus + other.bonus;
      this.bonus = GlobalNumberLimit.box(this.bonus);
   }

   @Override
   public float getPriority() {
      return -32.0F;
   }
}
