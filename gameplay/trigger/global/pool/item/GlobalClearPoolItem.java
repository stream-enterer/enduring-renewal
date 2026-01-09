package com.tann.dice.gameplay.trigger.global.pool.item;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.List;

public class GlobalClearPoolItem extends Global {
   @Override
   public String describeForSelfBuff() {
      return "clear pool of items for loot";
   }

   @Override
   public void affectItemOptions(List<Item> results, int itemTier) {
      results.clear();
   }
}
