package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.RenameUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Tann;
import java.util.List;

public class PipeItemPart extends PipeRegexNamed<Item> {
   private static final PRNPart sep = new PRNMid("part");

   public PipeItemPart() {
      super(ITEM, sep, PipeRegexNamed.DIGIT);
   }

   public Item example() {
      return this.make(ItemLib.random(), Tann.randomInt(2));
   }

   protected Item internalMake(String[] groups) {
      String itemS = groups[0];
      String digStr = groups[1];
      return bad(itemS, digStr) ? null : this.make(ItemLib.byName(itemS), Integer.parseInt(digStr));
   }

   private Item make(Item src, int index) {
      if (index >= 0 && !src.isMissingno()) {
         List<Personal> personals = src.getPersonals();
         if (personals.size() >= 2 && personals.size() > index) {
            ItBill ib = RenameUtils.copy(src, src.getName(false) + sep + index);
            List<Personal> personalsx = ib.getPersonals();
            Personal only = personalsx.get(index);
            personalsx.clear();
            personalsx.add(only);
            return ib.bItem();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
