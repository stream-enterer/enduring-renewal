package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;

public class PipeModAllItem extends PipeRegexNamed<Modifier> {
   final boolean hero;

   public PipeModAllItem(boolean hero) {
      super(getPref(hero), ITEM);
      this.hero = hero;
   }

   private static PRNPart getPref(boolean hero) {
      return new PRNPref(hero ? "allitem" : "alliteme");
   }

   protected Modifier internalMake(String[] groups) {
      String ms = groups[0];
      return bad(ms) ? null : makeItemAll(ItemLib.byName(ms), this.hero);
   }

   public static Modifier makeItemAll(Item item, boolean hero) {
      if (item.isMissingno()) {
         return null;
      } else {
         float calcTier = item.getTier() * 4;
         if (item.getTier() > 0) {
            calcTier = item.getTier() * 2;
         }

         if (!hero) {
            calcTier *= -1.0F;
         }

         if (item.getReferencedKeywords().contains(Keyword.potion)) {
            calcTier *= 3.0F;
         }

         if (!item.hasTier()) {
            calcTier = -0.069F;
         }

         return new Modifier(calcTier, getPref(hero) + item.getName(), new GlobalAllEntities(hero, new AsIfHasItem(item)));
      }
   }

   public Modifier example() {
      Item i = ItemLib.random();
      return makeItemAll(i, this.hero);
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild && this.hero;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public boolean showHigher() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
