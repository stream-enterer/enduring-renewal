package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;

public class OnOverheal extends Personal {
   final EffBill eff;
   final boolean hasValue;
   float priority;

   public OnOverheal(EffBill eff) {
      this.eff = eff;
      this.hasValue = eff.bEff().hasValue();
      switch (eff.bEff().getType()) {
         case Damage:
            this.priority = super.getPriority() + 0.001F;
            break;
         default:
            this.priority = super.getPriority();
      }
   }

   @Override
   public String describeForSelfBuff() {
      return this.hasValue
         ? this.eff.value(1).bEff().describe() + com.tann.dice.Main.t(" for each wasted point of healing I receive")
         : this.eff.value(1).bEff().describe() + com.tann.dice.Main.t(" when I am overhealed");
   }

   @Override
   public String getImageName() {
      switch (this.eff.bEff().getType()) {
         case Damage:
            return "chaliceIchor";
         case Shield:
            return "chaliceBlood";
         case Mana:
            return "archmageOrb";
         case Kill:
            return "skullRed";
         default:
            return "healGlitch";
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void overHeal(EntState entState, int overHeal) {
      Eff toHit;
      if (this.hasValue) {
         toHit = this.eff.value(overHeal).bEff();
      } else {
         toHit = this.eff.bEff();
      }

      entState.getSnapshot().target(null, new SimpleTargetable(entState.getEnt(), toHit), false);
   }

   @Override
   public boolean allowOverheal() {
      return true;
   }

   @Override
   public float getPriority() {
      return this.priority;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.HEAL;
   }
}
