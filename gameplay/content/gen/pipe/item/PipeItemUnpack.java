package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.LinkedPersonal;

public class PipeItemUnpack extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("unpack");

   public PipeItemUnpack() {
      super(PREF, ITEM);
   }

   public Item example() {
      for (int i = 0; i < 150; i++) {
         Item ex = this.make(ItemLib.random());
         if (ex != null) {
            return ex;
         }
      }

      return null;
   }

   protected Item internalMake(String[] groups) {
      return this.make(ItemLib.byName(groups[0]));
   }

   private Item make(Item src) {
      Personal p = src.getSinglePersonalOrNull();
      if (p instanceof LinkedPersonal) {
         LinkedPersonal lp = (LinkedPersonal)p;
         Personal lpp = lp.getLinkDebug();
         return new ItBill(-69, PREF + src.toString(), src.getImage()).prs(lpp).bItem();
      } else {
         return null;
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
