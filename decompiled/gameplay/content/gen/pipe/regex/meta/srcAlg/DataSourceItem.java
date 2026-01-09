package com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemPerTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.RenameUtils;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.item.PersonalTextureReplaced;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.weird.DescribeOnly;
import java.util.List;

public class DataSourceItem extends DataSource<Item> {
   public DataSourceItem() {
      super(PipeRegexNamed.ITEM);
   }

   public Item combine(Item src, AtlasRegion ar, String realName) {
      ItBill ib = new ItBill(src.getTier(), realName, ar).prs(src.getPersonals());
      if (!hasPTR(ib)) {
         ib.prs(new PersonalTextureReplaced());
      }

      return ib.bItem();
   }

   public Item makeT(String name) {
      return ItemLib.byName(name);
   }

   public Item exampleBase() {
      return ItemLib.random();
   }

   public AtlasRegion getImage(Item heroType) {
      return wrap(heroType.getImage());
   }

   public Item upscale(Item item, int multiplier) {
      return multiMake(item, multiplier);
   }

   public Item rename(Item src, String rename, String realName) {
      if (rename != null && !rename.isEmpty() && !src.isMissingno()) {
         ItBill ib = RenameUtils.copy(src, realName);
         RenameUtils.rename(ib, rename, true);
         return ib.bItem();
      } else {
         return null;
      }
   }

   public Item document(Item item, String documentation, String realName) {
      return RenameUtils.copy(item, realName).prs(new DescribeOnly(documentation)).bItem();
   }

   public Item retier(Item item, int newTier, String realName) {
      return new ItBill(newTier, realName, item.getImage()).prs(item.getPersonals()).bItem();
   }

   public Item makeIndexed(long val) {
      List<Item> cpy = ItemLib.getMasterCopy();
      return val >= 0L && val < cpy.size() ? cpy.get((int)val) : null;
   }

   public Item renameUnderlying(Item item, String rename) {
      return RenameUtils.copy(item, rename).bItem();
   }

   public Item withItem(Item item, Item i, String s) {
      return null;
   }

   public static Item multiMake(Item src, int mul) {
      if (!src.isMissingno() && src.isMultiplable(true)) {
         float tier = src.getTier() * mul;
         TextureRegion tr = src.getImage();
         ItBill ib = new ItBill(Math.round(tier), "x" + mul + "." + src.getName(), tr);
         List<Personal> personalTriggers = src.getPersonals();

         for (int i = 0; i < personalTriggers.size(); i++) {
            Personal p = personalTriggers.get(i);
            if (PipeItemPerTier.shouldMult(p)) {
               ib.prs(PersonalPerN.basicMultiple(mul, p));
            } else {
               ib.prs(p);
            }
         }

         return ib.bItem();
      } else {
         return null;
      }
   }

   public static boolean hasPTR(ItBill ib) {
      for (int i = 0; i < ib.getPersonals().size(); i++) {
         if (ib.getPersonals().get(i) instanceof PersonalTextureReplaced) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasPTR(Item i) {
      for (int i1 = 0; i1 < i.getPersonals().size(); i1++) {
         if (i.getPersonals().get(i1) instanceof PersonalTextureReplaced) {
            return true;
         }
      }

      return false;
   }
}
