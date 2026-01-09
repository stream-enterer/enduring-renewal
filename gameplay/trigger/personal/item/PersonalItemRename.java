package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class PersonalItemRename extends Personal {
   private final String override;

   public PersonalItemRename(String override) {
      this.override = override;
   }

   @Override
   public String affectItemName(String current) {
      return this.override;
   }

   @Override
   public boolean skipCalc() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean skipEquipImage() {
      return true;
   }
}
