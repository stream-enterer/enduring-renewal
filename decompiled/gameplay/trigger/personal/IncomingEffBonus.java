package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;

public class IncomingEffBonus extends Personal {
   final EffType[] types;
   final int bonus;
   final boolean multiply;

   public IncomingEffBonus(int bonus, EffType... types) {
      this(bonus, false, types);
   }

   public IncomingEffBonus(int bonus, boolean multiply, EffType... types) {
      this.bonus = bonus;
      this.types = types;
      this.multiply = multiply;
   }

   @Override
   public String describeForSelfBuff() {
      String result = this.getDeltaString() + " to incoming ";

      for (int i = 0; i < this.types.length; i++) {
         EffType t = this.types[i];
         switch (t) {
            case Shield:
               result = result + "shields";
               break;
            case Heal:
               result = result + "healing";
               break;
            default:
               result = result + t.toString().toLowerCase();
         }

         if (i < this.types.length - 1) {
            result = result + " and ";
         }
      }

      return result;
   }

   private String getDeltaString() {
      return this.multiply ? "x" + this.bonus : Tann.delta(this.bonus);
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      if (this.multiply) {
         return "damageMultiply";
      } else if (this.bonus > 0) {
         if (this.types.length == 1) {
            switch (this.types[0]) {
               case Shield:
                  return "bonusShields";
               case Heal:
                  return "bonusHealing";
               case Damage:
                  return "vulnerable";
            }
         } else if (this.types.length == 2) {
            return "bonusHealingShields";
         }

         return super.getImageName();
      } else {
         return "heartBroken";
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      int gap = 1;
      Pixl p = new Pixl(0);

      for (int i = 0; i < this.types.length; i++) {
         EffType et = this.types[i];
         Eff e = new EffBill().type(et).value(this.bonus).bEff();
         p.actor(e.getBasicImage(this.getDeltaString()));
         if (i < this.types.length - 1) {
            p.gap(1);
         }
      }

      return p.pix();
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (damage <= 0) {
         return damage;
      } else {
         return Tann.contains(this.types, EffType.Damage) ? this.newAmt(damage) : super.alterTakenDamage(damage, eff, snapshot, self, targetable);
      }
   }

   private int newAmt(int amt) {
      return this.multiply ? amt * this.bonus : Math.max(0, amt + this.bonus);
   }

   @Override
   public int affectShields(int shield) {
      if (shield <= 0) {
         return shield;
      } else {
         return Tann.contains(this.types, EffType.Shield) ? this.newAmt(shield) : super.affectShields(shield);
      }
   }

   @Override
   public int affectHealing(int heal) {
      if (heal <= 0) {
         return heal;
      } else {
         return Tann.contains(this.types, EffType.Heal) ? this.newAmt(heal) : super.affectHealing(heal);
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = Collision.INCOMING_BONUS;

      for (int i = 0; i < this.types.length; i++) {
         result |= this.types[i].getCollisionBits(player);
      }

      return Collision.ignored(result, Collision.PHYSICAL_DAMAGE);
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
