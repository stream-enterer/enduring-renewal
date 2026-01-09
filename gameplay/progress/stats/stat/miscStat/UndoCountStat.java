package com.tann.dice.gameplay.progress.stats.stat.miscStat;

public class UndoCountStat extends MiscStat {
   public static String NAME = "undo-count";

   public UndoCountStat() {
      super(NAME);
   }

   @Override
   public void onUndo(int undosInARow) {
      this.addToValue(1);
   }

   @Override
   public boolean showInAlmanac(int side) {
      return side == 1;
   }

   @Override
   public int getOrder() {
      return 10;
   }
}
