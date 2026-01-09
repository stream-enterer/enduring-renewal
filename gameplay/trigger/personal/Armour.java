package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.util.lang.Words;

public class Armour extends Personal {
   int reduction;

   public Armour(int reduction) {
      this.reduction = reduction;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "armour" + this.reduction;
   }

   @Override
   public String describeForSelfBuff() {
      return "Reduce damage taken from " + Words.spab(true) + " and dice by " + this.reduction;
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (targetable == null) {
         return damage;
      } else if (!(targetable instanceof Ability) && !(targetable instanceof DieTargetable)) {
         return damage;
      } else if (eff.getType() != EffType.Damage && !eff.hasKeyword(Keyword.damage)) {
         return super.alterTakenDamage(damage, eff, snapshot, self, targetable);
      } else {
         if (damage == 1) {
            snapshot.addEvent(SoundSnapshotEvent.clink);
         }

         return damage - this.reduction;
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * (this.reduction + 1);
   }

   @Override
   public float getPriority() {
      return -31.0F;
   }
}
