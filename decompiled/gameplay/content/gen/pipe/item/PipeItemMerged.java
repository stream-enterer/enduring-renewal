package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;

public class PipeItemMerged extends PipeRegexNamed<Item> {
   static final PRNPart SEP = new PRNMid("mrg");

   public PipeItemMerged() {
      super(ITEM, SEP, ITEM);
   }

   protected Item internalMake(String[] groups) {
      String a = groups[0];
      String b = groups[1];
      if (bad(a, b)) {
         return null;
      } else {
         String full = groups[0] + SEP + groups[1];
         String target = SEP.toString();
         int li = full.length();

         while ((li = full.lastIndexOf(target, li - 1)) != -1) {
            String itemAN = full.substring(0, li);
            String itemBN = full.substring(li + target.length());
            Item i1;
            Item i2;
            if (itemAN.length() < itemBN.length()
               ? !(i1 = ItemLib.byName(itemAN)).isMissingno() && !(i2 = ItemLib.byName(itemBN)).isMissingno()
               : !(i2 = ItemLib.byName(itemBN)).isMissingno() && !(i1 = ItemLib.byName(itemAN)).isMissingno()) {
               return make(i1, i2);
            }
         }

         return null;
      }
   }

   public static Item make(Item a, Item b) {
      if (!a.isMissingno() && !b.isMissingno()) {
         String name = a.getName(false) + SEP + b.getName(false);
         float totalModTier = TierUtils.itemModTier(a.getTier()) + TierUtils.itemModTier(b.getTier());
         int tier = TierUtils.fromModTier(ChoosableType.Item, totalModTier);
         if (!a.hasTier() || !b.hasTier()) {
            tier = -69;
         }

         if (a.getPersonals().size() == 1 && b.getPersonals().size() == 1) {
            Personal pa = a.getPersonals().get(0);
            Personal pb = b.getPersonals().get(0);
            if (pa instanceof AffectSides && pb instanceof AffectSides) {
               AffectSides aa = (AffectSides)pa;
               AffectSides ab = (AffectSides)pb;
               AffectSides ax = AffectSides.mrgEffects(aa, ab);
               if (ax == null) {
                  return null;
               } else {
                  ItBill ib = ItBill.make(tier, name, a, b, "special/combined");
                  ib.prs(ax);
                  return ib.bItem();
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public Item example() {
      return make(ItemLib.random(), ItemLib.random());
   }
}
