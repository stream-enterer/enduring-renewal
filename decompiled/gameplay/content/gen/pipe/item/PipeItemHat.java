package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithEnt;

public class PipeItemHat extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("hat");

   public PipeItemHat() {
      super(PREF, ENTITY);
   }

   protected Item internalMake(String[] groups) {
      String hn = groups[0];
      return this.makeShortened(EntTypeUtils.byName(hn));
   }

   protected Item makeShortened(EntType src) {
      if (src.isMissingno()) {
         return null;
      } else {
         int tier = 0;
         if (src instanceof HeroType) {
            HeroType ht = (HeroType)src;
            tier = ht.getTier() * 3 - 2;
         }

         return new ItBill(tier, PREF + src.getName(false), "special/hat").prs(new AffectSides(SpecificSidesType.All, new ReplaceWithEnt(src))).bItem();
      }
   }

   public Item example() {
      HeroType ht = HeroTypeUtils.randomNonGreen();
      Item i = this.makeShortened(ht);
      String disName = ht.getName(true, false);
      return disName.equals(ht.getName(false)) ? i : ItemLib.byName("(" + i.getName() + ").n." + disName + " hat");
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.4F;
   }

   protected Item generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}
