package com.tann.dice.gameplay.trigger.personal.spell;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.lang.Words;

public class AbilityVulnerable extends Personal {
   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      return targetable instanceof Ability ? damage * 2 : damage;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "vulnerableSpell";
   }

   @Override
   public String describeForSelfBuff() {
      return "Takes double damage from " + Words.spab(true);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.78F;
   }

   @Override
   public float getPriority() {
      return 3.0F;
   }
}
