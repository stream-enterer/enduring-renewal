package com.tann.dice.gameplay.trigger.personal.equipRestrict;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class NoEquip extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Cannot hold items";
   }

   @Override
   public boolean skipEquipScreen() {
      return true;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total * 0.85F;
   }
}
