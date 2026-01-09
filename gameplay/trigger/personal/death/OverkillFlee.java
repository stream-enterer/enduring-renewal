package com.tann.dice.gameplay.trigger.personal.death;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class OverkillFlee extends Personal {
   final int overkillBy;

   public OverkillFlee(int overkillBy) {
      this.overkillBy = overkillBy;
   }

   @Override
   public String describeForSelfBuff() {
      return "Flees if an adjacent monster is overkilled by " + this.overkillBy + " or more";
   }

   @Override
   public String getImageName() {
      return "mercenary";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void onOtherDeath(Snapshot snapshot, EntState dead, EntState self) {
      if (!self.isDead() && snapshot.getAdjacents(dead, 1).contains(self) && dead.getHp() <= -this.overkillBy) {
         self.flee();
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.8F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
