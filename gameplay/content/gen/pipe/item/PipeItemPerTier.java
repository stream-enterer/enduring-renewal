package com.tann.dice.gameplay.content.gen.pipe.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNHeroLevel;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.bsRandom.Supplier;
import java.util.List;

public class PipeItemPerTier extends PipeRegexNamed<Item> {
   static final PRNPart PREF = new PRNPref("pertier");

   public PipeItemPerTier() {
      super(PREF, ITEM);
   }

   protected Item internalMake(String[] groups) {
      return this.make(ItemLib.byName(groups[0]));
   }

   protected Item make(Item src) {
      if (!src.isMissingno() && src.isMultiplable(true)) {
         float tier = TierUtils.fromModTier(ChoosableType.Item, TierUtils.itemModTier(src.getTier()) * 2.1F);
         TextureRegion tr = src.getImage();
         ItBill ib = new ItBill(Math.round(tier), PREF + src.getName(), tr);
         List<Personal> personalTriggers = src.getPersonals();
         PerN perN = new PerNHeroLevel();

         for (int i = 0; i < personalTriggers.size(); i++) {
            Personal p = personalTriggers.get(i);
            if (!shouldMult(p)) {
               ib.prs(p);
            } else {
               ib.prs(new PersonalPerN(p, perN));
            }
         }

         return ib.bItem();
      } else {
         return null;
      }
   }

   public static boolean shouldMult(Trigger p) {
      return !p.metaOnly() && !p.skipMultiplable() && p.isMultiplable();
   }

   public Item example() {
      return this.make(randomCheckedMultipliable(true));
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return false;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.1F;
   }

   protected Item generateInternal(boolean wild) {
      return this.make(randomCheckedMultipliable(false));
   }

   public static Item randomCheckedMultipliable(final boolean liberal) {
      return RandomCheck.checkedRandom(new Supplier<Item>() {
         public Item supply() {
            return ItemLib.random();
         }
      }, new Checker<Item>() {
         public boolean check(Item item) {
            return item.isMultiplable(liberal);
         }
      }, ItemLib.byName("Leather Vest"));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
