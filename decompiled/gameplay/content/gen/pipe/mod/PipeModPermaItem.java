package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeModPermaItem extends PipeRegexNamed<Modifier> {
   static final String PREF = "i";

   public PipeModPermaItem() {
      super(new PRNPref("i"), ITEM);
   }

   protected Modifier internalMake(String[] groups) {
      Item it = ItemLib.byName(groups[0]);
      return make(it);
   }

   public static Modifier make(Item it) {
      if (it.isMissingno()) {
         return null;
      } else {
         float modTier = TierUtils.itemModTier(it.getTier());
         return new Modifier(modTier, GlobalStartWithItem.nameFor(it.getName(false)), new GlobalStartWithItem(it));
      }
   }

   public Modifier example() {
      return Tann.chance(0.2F) ? make(ItemLib.random()) : make(Tann.pick(getAllItemsWithQualityRange(-10, -1)));
   }

   private static List<Item> getAllItemsWithQualityRange(int min, int max) {
      List<Item> results = new ArrayList<>();
      boolean allowLocked = false;

      for (Item e : ItemLib.getMasterCopy()) {
         if (!UnUtil.isLocked(e) && e.getTier() >= min && e.getTier() <= max) {
            results.add(e);
         }
      }

      return results;
   }

   @Override
   public float getRarity(boolean wild) {
      return wild ? 1.0F : 1.0F;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Modifier generateInternal(boolean wild) {
      return wild ? make(PipeItem.makeGen()) : this.example();
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}
