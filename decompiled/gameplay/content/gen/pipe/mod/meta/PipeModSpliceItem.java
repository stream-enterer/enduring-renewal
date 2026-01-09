package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNS;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;

public class PipeModSpliceItem extends PipeRegexNamed<Modifier> {
   private static PRNS SEP = new PRNMid("splice");

   public PipeModSpliceItem() {
      super(MODIFIER, SEP, ITEM);
   }

   public Modifier example() {
      return make(ModifierLib.random(), ItemLib.random());
   }

   protected Modifier internalMake(String[] groups) {
      String s1 = groups[0];
      String s2 = groups[1];
      Modifier mod;
      Item item;
      if (s1.length() < s2.length()) {
         if ((mod = ModifierLib.byName(s1)).isMissingno() || (item = ItemLib.byName(s2)).isMissingno()) {
            return null;
         }
      } else if ((item = ItemLib.byName(s2)).isMissingno() || (mod = ModifierLib.byName(s1)).isMissingno()) {
         return null;
      }

      return make(mod, item);
   }

   private static Modifier make(Modifier outer, Item inner) {
      return inner.isMissingno() ? null : PipeModSplice.make(outer, inner.getSinglePersonalOrNull(), inner.getName());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
