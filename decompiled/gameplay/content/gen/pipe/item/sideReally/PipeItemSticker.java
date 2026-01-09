package com.tann.dice.gameplay.content.gen.pipe.item.sideReally;

import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;

public class PipeItemSticker extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("sticker");

   public PipeItemSticker() {
      super(PREF, ITEM);
   }

   protected Item internalMake(String[] groups) {
      return this.makeShortened(ItemLib.byName(groups[0]));
   }

   protected Item makeShortened(Item src) {
      if (src.isMissingno()) {
         return null;
      } else {
         int tier = Math.round(src.getTier() * 0.3F);
         EntSide es = new EnSiBi().sticker(src);
         return PipeItemCast.make(tier, PREF + src.getName(false), "sticker", es);
      }
   }

   public Item example() {
      return this.makeShortened(ItemLib.byName("shortsword"));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
