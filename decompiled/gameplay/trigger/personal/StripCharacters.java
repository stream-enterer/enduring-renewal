package com.tann.dice.gameplay.trigger.personal;

public class StripCharacters extends Personal {
   final String toStrip;

   public StripCharacters(String toStrip) {
      this.toStrip = toStrip;
   }

   @Override
   public String getDisplayName(String name) {
      return name.startsWith(this.toStrip) ? name.substring(this.toStrip.length()) : super.getDisplayName(name);
   }

   @Override
   public boolean skipCalc() {
      return true;
   }
}
