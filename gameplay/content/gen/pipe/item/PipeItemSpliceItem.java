package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.linked.LinkedPersonal;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.bsRandom.Supplier;

public class PipeItemSpliceItem extends PipeRegexNamed<Item> {
   private static PRNS SEP = new PRNMid("splice");

   public PipeItemSpliceItem() {
      super(ITEM, SEP, ITEM);
   }

   public Item example() {
      return RandomCheck.checkedRandomNull(new Supplier<Item>() {
         public Item supply() {
            return PipeItemSpliceItem.make(ItemLib.random(), ItemLib.random());
         }
      });
   }

   protected Item internalMake(String[] groups) {
      String a = groups[0];
      String b = groups[1];
      Item ia;
      Item ib;
      if (a.length() < b.length()) {
         if ((ia = ItemLib.byName(a)).isMissingno() || (ib = ItemLib.byName(b)).isMissingno()) {
            return null;
         }
      } else if ((ib = ItemLib.byName(b)).isMissingno() || (ia = ItemLib.byName(a)).isMissingno()) {
         return null;
      }

      return make(ia, ib);
   }

   private static Item make(Item outer, Item inner) {
      if (!inner.isMissingno() && !outer.isMissingno()) {
         String name = outer.getName() + SEP + inner.getName();
         Personal p = splicePersonal(outer.getSinglePersonalOrNull(), inner.getSinglePersonalOrNull());
         return p == null ? null : new ItBill(-69, name).prs(p).bItem();
      } else {
         return null;
      }
   }

   private static Personal splicePersonal(Personal outer, Personal inner) {
      if (outer != null && inner != null) {
         if (outer instanceof AffectSides && inner instanceof AffectSides) {
            AffectSides oa = (AffectSides)outer;
            AffectSides ia = (AffectSides)inner;
            if (!oa.getConditions().isEmpty()) {
               return oa.splice(ia);
            }
         }

         if (!(outer instanceof LinkedPersonal)) {
            return null;
         } else {
            LinkedPersonal ogl = (LinkedPersonal)outer;
            Personal spliced = ogl.splice(inner);
            return spliced != null ? spliced : null;
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
