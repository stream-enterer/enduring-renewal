package com.tann.dice.gameplay.trigger.personal.death;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class DamageAdjacentsOnDeath extends Personal {
   final int damageAmount;

   public DamageAdjacentsOnDeath(int damageAmount) {
      this.damageAmount = damageAmount;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      switch (this.damageAmount) {
         case 5:
            return "explode";
         default:
            return "boneDeath";
      }
   }

   @Override
   public String describeForSelfBuff() {
      return this.damageAmount + " damage to adjacent allies upon death";
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      for (EntState es : snapshot.getAdjacents(self, true, false, 1, 1)) {
         es.hit(new EffBill().damage(this.damageAmount).visual(this.getVisualEffectType()).bEff(), null);
      }
   }

   private VisualEffectType getVisualEffectType() {
      switch (this.damageAmount) {
         case 1:
            return VisualEffectType.Slice;
         case 5:
            return VisualEffectType.Flame;
         default:
            return VisualEffectType.Singularity;
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return this.damageAmount > hp / 2.0F ? hp - this.damageAmount * 1.55F : hp - this.damageAmount * 0.9F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
