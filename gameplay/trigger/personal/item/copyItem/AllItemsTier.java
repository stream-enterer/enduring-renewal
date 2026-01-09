package com.tann.dice.gameplay.trigger.personal.item.copyItem;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.personal.Personal;
import java.util.ArrayList;
import java.util.List;

public class AllItemsTier extends Personal {
   final int tierMin;
   final int tierMax;

   public AllItemsTier(int tierMin, int tierMax) {
      this.tierMin = tierMin;
      this.tierMax = tierMax;
   }

   @Override
   public String describeForSelfBuff() {
      return this.tierMax == this.tierMin
         ? "Gain the effects of all tier " + this.tierMax + " items"
         : "Gain the effects of all tier " + this.tierMin + "-" + this.tierMax + " items";
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      List<Personal> result = new ArrayList<>();

      for (int tier = this.tierMin; tier <= this.tierMax; tier++) {
         for (Item e : ItemLib.getAllItemsWithQuality(tier)) {
            if (!UnUtil.isLocked(e)) {
               result.addAll(e.getPersonals());
            }
         }
      }

      result.remove(this);
      return result;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }
}
