package com.tann.dice.gameplay.trigger.personal.item;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotTextEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Tann;
import java.util.List;

public class Chest extends Personal {
   final int fleeTurn;
   final int itemLower;
   final int itemHigher;

   public Chest(int fleeTurn, int itemLower, int itemHigher) {
      this.fleeTurn = fleeTurn;
      this.itemLower = itemLower;
      this.itemHigher = itemHigher;
   }

   @Override
   public String getImageName() {
      return "chest";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      int tier = Tann.randomInt(this.itemLower, this.itemHigher);
      List<Item> list = ItemLib.randomWithExactQuality(1, tier, snapshot.getFightLog().getContext());
      if (list.size() == 1) {
         self.addBuff(new GainItem(list.get(0)));
         snapshot.addEvent(new SnapshotTextEvent("[yellow]+1 item [text](after combat)"));
      }
   }

   @Override
   public void endOfTurn(EntState entState) {
      if (entState.getTurnsElapsed() >= this.fleeTurn) {
         if (entState.getHp() > 0) {
            entState.flee();
         }
      }
   }

   @Override
   public int calcBackRowTurn() {
      return this.fleeTurn == 1 ? 1 : super.calcBackRowTurn();
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return this.fleeTurn == 1 ? hp * 0.13F - this.getAvgTier() * 0.28F : hp * (1.0F - 1.0F / (this.fleeTurn + 3.5F));
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total - this.getAvgTier() * 0.25F;
   }

   private float getAvgTier() {
      return (this.itemHigher - this.itemLower) / 2.0F + this.itemLower;
   }

   @Override
   public String describeForSelfBuff() {
      return this.describeFlees() + " and drop a [grey]t" + this.itemLower + "-" + this.itemHigher + " item[cu] if defeated.";
   }

   private String describeFlees() {
      return this.fleeTurn == 1 ? "I flee at the end of the turn" : "I flee at the end of turn " + this.fleeTurn;
   }
}
