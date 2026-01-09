package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;

public class PipeItemCombined extends PipeRegexNamed<Item> {
   static final PRNPart SEP = prnS("#");

   public PipeItemCombined() {
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

         ItBill ib = ItBill.make(tier, name, a, b, "special/combined");
         ib.prs(a.getPersonals());
         ib.prs(b.getPersonals());
         return ib.bItem();
      } else {
         return null;
      }
   }

   public static Item makeChecked(Item a, Item b) {
      return !ChoosableUtils.collides(a, b) && !aBlankBAffect(a, b) && !aBlankBAffect(b, a) ? make(a, b) : null;
   }

   private static boolean aBlankBAffect(Item a, Item b) {
      boolean has = false;

      for (Personal personalTrigger : b.getPersonals()) {
         has |= personalTrigger instanceof AffectSides;
      }

      has |= (b.getCollisionBits() & Collision.GENERIC_ALL_SIDES_HERO) != 0L;
      has |= (b.getCollisionBits() & Collision.ALL_SIDES_HERO_COMPOSITE) != 0L;
      return has && (a.getCollisionBits() & Collision.BLANK_SIDE) != 0L;
   }

   public Item example() {
      return makeChecked(ItemLib.random(), ItemLib.random());
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Item generateInternal(boolean wild) {
      return makeChecked(ItemLib.random(), ItemLib.byName("(" + ItemLib.random().getName(false) + ")"));
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}
