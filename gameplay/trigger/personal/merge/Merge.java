package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.trigger.personal.Personal;

public class Merge extends Personal {
   @Override
   public Personal transformForBuff() {
      return this.copy();
   }

   public Merge copy() {
      try {
         Merge pt = (Merge)this.clone();
         pt.onClone();
         return pt;
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   protected void onClone() {
      this.clearDescCache();
   }

   public final boolean canMerge(Personal personal) {
      if (this.buff != null && personal.buff != null) {
         return !this.getClass().isAssignableFrom(personal.getClass()) ? false : this.canMergeInternal(personal);
      } else {
         return false;
      }
   }

   protected boolean canMergeInternal(Personal personal) {
      return false;
   }

   public void merge(Personal personal) {
      throw new RuntimeException("Cannot merge triggers: " + this.getClass() + ":" + personal.getClass());
   }
}
