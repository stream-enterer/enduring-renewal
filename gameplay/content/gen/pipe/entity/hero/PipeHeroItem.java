package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;

public class PipeHeroItem extends PipeRegexNamed<HeroType> {
   public static final PRNPart SEP = new PRNMid("i");
   final boolean lazy;

   public PipeHeroItem() {
      this(false);
   }

   public PipeHeroItem(boolean lazy) {
      super(getHeroPart(lazy), SEP, ITEM);
      this.lazy = lazy;
   }

   private static PRNPart getHeroPart(boolean lazy) {
      return lazy ? PipeRegexNamed.HERO_LAZY : PipeRegexNamed.HERO;
   }

   protected HeroType internalMake(String[] groups) {
      String full = groups[0] + SEP + groups[1];
      String target = ".i.";
      int li = full.length();

      while ((li = full.lastIndexOf(".i.", li - 1)) != -1) {
         String heroName = full.substring(0, li);
         String itemName = full.substring(li + ".i.".length());
         HeroType h;
         Item i;
         if (heroName.length() < itemName.length()
            ? !(h = HeroTypeLib.byName(heroName)).isMissingno() && !(i = ItemLib.byName(itemName)).isMissingno()
            : !(i = ItemLib.byName(itemName)).isMissingno() && !(h = HeroTypeLib.byName(heroName)).isMissingno()) {
            return this.make(h, i);
         }
      }

      return null;
   }

   private HeroType make(HeroType ht, Item i) {
      if (!ht.isMissingno() && !i.isMissingno()) {
         AsIfHasItem aiha = new AsIfHasItem(i);
         String realHeroName = ht.getName() + SEP + i.getName();
         return HeroTypeUtils.withPassive(ht, realHeroName, aiha, "[grey]innate item: " + i.getName(true));
      } else {
         return null;
      }
   }

   @Override
   public boolean isHiddenAPI() {
      return this.lazy;
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random(), ItemLib.random());
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}
