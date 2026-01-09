package com.tann.dice.gameplay.trigger.personal.onHit;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.fightLog.event.entState.MiscEvent;
import com.tann.dice.gameplay.trigger.Collision;

public class Spiky extends OnHit {
   int amount;

   public Spiky(int amount) {
      this.amount = amount;
   }

   @Override
   protected void onHit(EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable) {
      if (source != null) {
         if (targetable instanceof DieTargetable) {
            int prevHp = source.getHp();
            source.hit(new EffBill().damage(this.amount).bEff(), self.getEnt());
            if (ChatStateEvent.Spiked.chance() && source.getHp() < prevHp) {
               source.addEvent(ChatStateEvent.Spiked);
            }

            self.addEvent(MiscEvent.spike);
         }
      }
   }

   @Override
   public String getImageName() {
      switch (this.amount) {
         case 1:
            return "thorns";
         case 2:
            return "thorns2";
         case 3:
         case 4:
         default:
            return super.getImageName();
         case 5:
            return "thorns5";
      }
   }

   @Override
   protected String describeExtra() {
      return "damage the attacker for " + this.amount;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + this.amount * 0.9F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return (float)(hp * Math.pow(1.18F, this.amount));
   }

   @Override
   public String hyphenTag() {
      return this.amount + "";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.PHYSICAL_DAMAGE;
   }
}
