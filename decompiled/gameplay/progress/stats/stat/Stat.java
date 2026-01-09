package com.tann.dice.gameplay.progress.stats.stat;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.util.lang.Words;

public class Stat {
   private String n = "UNSET_STAT_NAME";
   private int v;

   public Stat(String name) {
      this.n = name;
      this.reset();
   }

   public Stat() {
   }

   void merge(Stat other) {
      switch (this.getMergeType()) {
         case Sum:
            this.addToValue(other.getValue());
            break;
         case Lowest:
            if (this.getValue() == -1 != (other.getValue() == -1)) {
               this.setValue(this.getValue() + other.getValue() + 1);
            } else {
               this.setValue(Math.min(this.getValue(), other.getValue()));
            }
            break;
         case Highest:
            this.setValue(Math.max(this.getValue(), other.getValue()));
            break;
         case Newest:
            this.setValue(other.getValue());
            break;
         case BitMerge:
            this.setValue(this.getValue() | other.getValue());
      }
   }

   public boolean showInAlmanac(int side) {
      return false;
   }

   @Override
   public String toString() {
      return this.n + " : " + this.v;
   }

   public String getName() {
      return this.n;
   }

   public int getValue() {
      return this.v;
   }

   public void addToValue(int add) {
      this.v += add;
   }

   public void setValue(int value) {
      this.v = value;
   }

   protected StatMergeType getMergeType() {
      return StatMergeType.Sum;
   }

   public int getOrder() {
      return 0;
   }

   public void metaEndOfRun(DungeonContext context, boolean victory) {
   }

   public String getNameForDisplay() {
      return "[text]" + Words.capitaliseFirst(this.getName().replaceAll("-", " ")) + "[cu]";
   }

   public String getValueForDisplay() {
      return "[text]" + this.getValue() + "[cu]";
   }

   public boolean validFor(ContextConfig contextConfig) {
      return true;
   }

   public boolean isBoring() {
      return false;
   }

   public void reset() {
      this.v = 0;
      if (this.getMergeType() == StatMergeType.Lowest || this.getMergeType() == StatMergeType.Newest) {
         this.v = -1;
      }
   }
}
