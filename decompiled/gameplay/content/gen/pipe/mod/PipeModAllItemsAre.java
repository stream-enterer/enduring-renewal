package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNItem;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import java.util.List;

public class PipeModAllItemsAre extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("peritem");

   public PipeModAllItemsAre() {
      super(PREF, ITEM);
   }

   public Modifier example() {
      return this.make(RandomCheck.checkedRandom(ItemLib.makeSupplier(), new Checker<Item>() {
         public boolean check(Item item) {
            return PipeModAllItemsAre.isValid(item);
         }
      }, null));
   }

   private static boolean isValid(Item item) {
      if (item != null && !item.isMissingno()) {
         List<Personal> srcPersonals = item.getPersonals();

         for (int i = 0; i < srcPersonals.size(); i++) {
            if (!srcPersonals.get(i).isMultiplable()) {
               return false;
            }
         }

         return !srcPersonals.isEmpty();
      } else {
         return false;
      }
   }

   private Modifier make(Item src) {
      if (!isValid(src)) {
         return null;
      } else {
         float tier = src.getTier() * 2;
         return new Modifier(tier, PREF + src.getName(), new GlobalHeroes(new PersonalPerN(src.getPersonals(), new PerNItem())));
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return super.canGenerate(wild);
   }

   protected Modifier internalMake(String[] groups) {
      return this.make(ItemLib.byName(groups[0]));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
