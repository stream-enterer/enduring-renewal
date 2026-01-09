package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class PetrifySide extends Merge {
   List<Integer> petrified = new ArrayList<>();

   public PetrifySide(int side) {
      this.petrified.add(side);
   }

   public PetrifySide(List<Integer> sides) {
      this.petrified.addAll(sides);
   }

   @Override
   public String describeForSelfBuff() {
      return debuffString()
         + " "
         + this.petrified.size()
         + "[n]"
         + this.petrified.size()
         + " "
         + Words.plural("side", this.petrified.size())
         + " turned to stone "
         + KUtils.describeThisFight();
   }

   public static String debuffString() {
      return "[yellow]Petrified[cu]";
   }

   @Override
   public Cleansed.CleanseType getCleanseType() {
      return Cleansed.CleanseType.Petrify;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      boolean stoned = this.petrified.contains(owner.getSideIndex(sideState.getOriginal()));
      if (stoned) {
         sideState.changeTo(ESB.blankPetrified);
      }
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
   public String getImageName() {
      return "petrified";
   }

   @Override
   public void merge(Personal personal) {
      PetrifySide tps = (PetrifySide)personal;

      for (Integer i : tps.petrified) {
         if (i != null && !this.petrified.contains(i)) {
            this.petrified.add(i);
         } else {
            this.petrified.add(null);
         }
      }
   }

   @Override
   protected void onClone() {
      this.petrified = new ArrayList<>(this.petrified);
   }

   public List<Integer> getPetrified() {
      return this.petrified;
   }

   @Override
   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      int used = Math.min(cleanseAmt, this.petrified.size());

      for (int i = 0; i < used; i++) {
         this.petrified.remove(this.petrified.size() - 1);
      }

      return new TP<>(used, this.petrified.size() == 0);
   }
}
