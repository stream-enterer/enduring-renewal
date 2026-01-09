package com.tann.dice.gameplay.trigger.global.weird;

import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalRename extends Global {
   final String newName;

   public GlobalRename(String newName) {
      this.newName = newName;
   }

   @Override
   public String overrideDisplayName(String name) {
      return this.newName;
   }

   @Override
   public boolean skipEquipImage() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean metaOnly() {
      return true;
   }
}
