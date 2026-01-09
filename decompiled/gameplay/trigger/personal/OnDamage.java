package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.position.BackRow;
import com.tann.dice.util.Pixl;

public class OnDamage extends Personal {
   final Eff triggeredEff;
   final boolean first;
   final StateEvent event;

   public OnDamage(Eff triggeredEff, boolean first, StateEvent event) {
      this.triggeredEff = triggeredEff;
      this.first = first;
      this.event = event;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl();
      if (this.first) {
         p.text("[grey](1st").gap(1);
      }

      p.actor(new EffBill().damage(1).bEff().getBasicImage(""));
      if (this.first) {
         p.text("[grey])");
      }

      p.gap(3).text(":").gap(3);
      p.actor(this.triggeredEff.getBasicImage());
      return p.pix();
   }

   @Override
   protected boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      switch (this.triggeredEff.getType()) {
         case Buff:
            Personal t = this.triggeredEff.getBuff().personal;
            if (t instanceof Dodge) {
               return "ghostly";
            } else if (t instanceof BackRow) {
               return "feather";
            }
         default:
            return super.getImageName();
         case Shield:
            return "onDamageShield";
      }
   }

   @Override
   public String describeForSelfBuff() {
      String s = this.first ? "After taking damage for the first time each turn, " : "After taking damage, ";
      return "[notranslate]" + com.tann.dice.Main.t(s) + com.tann.dice.Main.t(this.triggeredEff.describe()).toLowerCase();
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (!this.first || damageTakenThisTurn == damage) {
         if (!this.triggeredEff.needsTarget() && this.triggeredEff.getTargetingType() != TargetingType.Self) {
            snapshot.target(null, new SimpleTargetable(self.getEnt(), this.triggeredEff), false);
         } else {
            self.hit(this.triggeredEff, self.getEnt());
         }

         if (this.event != null && this.event.chance()) {
            self.addEvent(this.event);
         }
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      switch (this.triggeredEff.getType()) {
         case Buff:
            Personal t = this.triggeredEff.getBuff().personal;
            if (t instanceof BackRow) {
               return total;
            } else if (t instanceof AffectSides) {
               return total * ((avgRawValue - 0.7F) / avgRawValue);
            } else if (t instanceof Dodge) {
               return total;
            }
         default:
            return super.affectStrengthCalc(total, avgRawValue, type);
      }
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      switch (this.triggeredEff.getType()) {
         case Buff:
            Personal t = this.triggeredEff.getBuff().personal;
            if (t instanceof AffectSides) {
               return hp;
            } else if (t instanceof BackRow) {
               return hp * 1.17F;
            } else if (t instanceof Dodge) {
               return hp * 1.6F;
            }
         default:
            return super.affectTotalHpCalc(hp, entType);
      }
   }

   @Override
   public int calcBackRowTurn() {
      switch (this.triggeredEff.getType()) {
         case Buff:
            Personal t = this.triggeredEff.getBuff().personal;
            if (t instanceof BackRow) {
               return 1;
            }
         default:
            return super.calcBackRowTurn();
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.triggeredEff.getCollisionBits(player);
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
