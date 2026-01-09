package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.Sidesc;
import com.tann.dice.util.Separators;
import com.tann.dice.util.Tann;

public class PipeItemSideDesc extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("sidesc");

   public PipeItemSideDesc() {
      super(PREF, RICH_TEXT);
   }

   protected Item internalMake(String[] groups) {
      return this.makeShortened(groups[0]);
   }

   protected Item makeShortened(String desc) {
      return Separators.bannedFromDocument(desc) ? null : new ItBill(PREF + desc).prs(new AffectSides(SpecificSidesType.All, new Sidesc(desc))).bItem();
   }

   public Item example() {
      return this.makeShortened(Tann.randomString(10));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
