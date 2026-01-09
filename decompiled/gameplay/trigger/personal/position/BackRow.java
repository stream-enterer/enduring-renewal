package com.tann.dice.gameplay.trigger.personal.position;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class BackRow extends Personal {
   boolean start;

   public BackRow(boolean start) {
      this.start = start;
   }

   @Override
   public boolean backRow() {
      return true;
   }

   @Override
   public String getImageName() {
      return "backRow";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.start;
   }

   @Override
   public String describeForSelfBuff() {
      return this.start ? "Back-row" : "Move back";
   }

   @Override
   protected boolean removeGiveFromGiveText() {
      return true;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 1.1F;
   }

   @Override
   public int calcBackRowTurn() {
      return 2;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.RANGED;
   }
}
