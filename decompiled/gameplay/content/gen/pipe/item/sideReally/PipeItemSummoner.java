package com.tann.dice.gameplay.content.gen.pipe.item.sideReally;

import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.EffBill;

public class PipeItemSummoner extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("summoner");

   public PipeItemSummoner() {
      super(PREF, HERO);
   }

   protected Item internalMake(String[] groups) {
      return this.makeShortened(HeroTypeLib.byName(groups[0]));
   }

   protected Item makeShortened(HeroType src) {
      if (src.isMissingno()) {
         return null;
      } else {
         EntSide es = new EnSiBi().effect(new EffBill().summon(src.getName(), 1)).image("special/summon").val(1);
         return PipeItemCast.make(-69, PREF + src.getName(false), "summon", es);
      }
   }

   public Item example() {
      return this.makeShortened(HeroTypeUtils.random());
   }
}
