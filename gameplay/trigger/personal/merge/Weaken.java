package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.tp.TP;

public class Weaken extends Merge {
   int amt;

   public Weaken(int amt) {
      this.amt = amt;
   }

   @Override
   public String getImageName() {
      return "allSidesMalus";
   }

   @Override
   public String describeForSelfBuff() {
      return debuffString() + " " + this.amt + "[cu][n]All sides reduced by " + this.amt;
   }

   public static String debuffString() {
      return "[green]Weakened[cu]";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   protected boolean canMergeInternal(Personal personal) {
      return true;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      Eff e = sideState.getCalculatedEffect();
      e.setValue(e.getValue() - this.amt);
   }

   @Override
   public void merge(Personal personal) {
      this.amt = this.amt + ((Weaken)personal).amt;
      this.amt = GlobalNumberLimit.box(this.amt);
   }

   @Override
   public Cleansed.CleanseType getCleanseType() {
      return Cleansed.CleanseType.Weaken;
   }

   @Override
   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      int used = Math.min(cleanseAmt, this.amt);
      this.amt -= used;
      return new TP<>(used, this.amt <= 0);
   }
}
