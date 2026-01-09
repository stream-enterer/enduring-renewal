package com.tann.dice.gameplay.content.gen.pipe.item.dataFromHero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.trigger.personal.onHit.OnHit;
import com.tann.dice.gameplay.trigger.personal.onHit.OnHitFromPipe;

public class PipeItemOnHit extends PipeRegexNamed<Item> {
   public static final PRNPart PREF = new PRNPref("onhitdata");

   public PipeItemOnHit() {
      super(PREF, ONHIT);
   }

   protected Item internalMake(String[] groups) {
      String heroName = groups[0];
      if (heroName == null) {
         return null;
      } else {
         HeroType ht = HeroTypeLib.byName(heroName);
         return ht.isMissingno() ? null : this.makeInternal(ht);
      }
   }

   private Item makeInternal(HeroType ht) {
      OnHit oh = OnHitFromPipe.make(ht);
      return oh == null ? null : new ItBill(PREF + ht.getName()).prs(oh).bItem();
   }

   public Item example() {
      return this.makeInternal(HeroTypeUtils.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
