package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.item.ItBill;
import java.util.ArrayList;
import java.util.List;

public class ItemBlob {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(ItemBlobNegative.makeAll());
      result.addAll(ItemBlobZero.makeAll());
      result.addAll(ItemBlobOneFive.makeAll());
      result.addAll(ItemBlobSixNine.makeAll());
      result.addAll(ItemBlobTenPlus.makeAll());
      return result;
   }
}
