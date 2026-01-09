package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;

public class Cowardly extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Flees if alone";
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
      if (!self.isDead() && snapshot.getStates(self.isPlayer(), false).size() == 1) {
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
