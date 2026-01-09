package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;

public class Mettley extends Personal {
   final Eff eff;

   public Mettley(int numShields) {
      this.eff = new EffBill().self().shield(numShields).bEff();
   }

   @Override
   public boolean keepShields() {
      return true;
   }

   @Override
   protected boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      snapshot.target(null, new SimpleTargetable(entState.getEnt(), this.eff), false);
   }

   @Override
   public String getImageName() {
      return "golem";
   }

   @Override
   public String describeForSelfBuff() {
      return "Start with " + this.eff.getValue() + " shields, unused shields are retained";
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp + this.eff.getValue();
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }
}
