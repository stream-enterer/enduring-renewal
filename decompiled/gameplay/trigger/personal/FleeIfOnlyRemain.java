package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.lang.Words;

public class FleeIfOnlyRemain extends Personal {
   final String type;

   public FleeIfOnlyRemain(String type) {
      this.type = type;
   }

   @Override
   public String describeForSelfBuff() {
      return "Rolls away if only " + Words.plural(this.type) + " remain";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "cowardly";
   }

   @Override
   public void onOtherDeath(Snapshot snapshot, EntState dead, EntState self) {
      if (!self.isDead()) {
         for (EntState state : snapshot.getStates(self.isPlayer(), false)) {
            if (!state.getEnt().getName(false).contains(this.type)) {
               return;
            }
         }

         self.flee();
      }
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.9F;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total * 0.95F;
   }
}
